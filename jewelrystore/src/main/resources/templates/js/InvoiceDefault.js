$(document).ready(function () {
    const productSoldDiv = $('#product-sold');
    const selectedProductsContainer = $('#selected-products');
    const totalPriceRaw = $('#totalPriceRaw');
    const discountPrice = $('#discountPrice');
    const subtotal = $('#subtotal');
    const token = localStorage.getItem("token");
    const employeeID = localStorage.getItem("userId"); // ID của nhân viên từ token

    let productMap = {};
    let selectedUserId = null;
    let selectedUserName = null;
    let userPromotion = null; // Biến để lưu khuyến mãi của người dùng
    let createdInvoiceId = null; // Biến để lưu ID của hóa đơn vừa được tạo

    $('#add-barcode-button').click(function () {
        const barcode = $('#barcode-input').val().trim();
        if (barcode) {
            addProductByBarcode(barcode);
            $('#barcode-input').val('');
        }
    });

    function addProductByBarcode(barcode) {
        if (productMap[barcode]) {
            updateProductQuantity(barcode, productMap[barcode].quantity + 1);
            return;
        }

        $.ajax({
            url: 'http://localhost:8080/invoice/create-detail',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                barcode: barcode,
                quantity: 1,
                invoiceTypeId: 1
            }),
            headers: {
                Authorization: `Bearer ${token}`,
            },
            success: function (response) {
                if (response.status === 'OK') {
                    const productData = response.data;
                    productMap[barcode] = {
                        product: productData.productDTO,
                        price: productData.price,
                        quantity: productData.quantity,
                        totalPrice: productData.totalPrice,
                        inventory: productData.productDTO.inventoryDTO.quantity
                    };

                    renderProductCard(barcode);
                    renderSidebarProduct(barcode);
                    updateTotalPrice();
                }
            },
            error: function (error) {
                console.error('Error fetching product data', error);
            }
        });
    }

    function renderProductCard(barcode) {
        const productData = productMap[barcode];
        const productCard = $(`
            <div id="product-${barcode}" class="product-card border p-4 mb-4 rounded-md shadow-md grid grid-cols-12 gap-4">
                <div class="col-span-4">
                    <img src="${productData.product.imgPath}" alt="${productData.product.name}" class="w-full h-auto rounded-md">
                </div>
                <div class="col-span-8">
                    <h3 class="text-xl font-semibold mb-2">${productData.product.name}</h3>
                    <p class="text-sm text-gray-600 mb-1"><strong>Mã sản phẩm:</strong> ${productData.product.productCode}</p>
                    <p class="text-sm text-gray-600 mb-1"><strong>Chất liệu:</strong> ${productData.product.materialDTO.name}</p>
                    <p class="text-sm text-gray-600 mb-1"><strong>Danh mục:</strong> ${productData.product.productCategoryDTO.name}</p>
                    <p class="text-sm text-gray-600 mb-1"><strong>Barcode:</strong> ${productData.product.barCode}</p>
                    <p class="text-sm text-gray-600 mb-1"><strong>Giá:</strong> ${productData.totalPrice}</p>
                    <p class="text-sm text-gray-600 mb-1"><strong>Số lượng:</strong> <span id="quantity-${barcode}">${productData.quantity}</span></p>
                </div>
            </div>
        `);
        productSoldDiv.append(productCard);
    }

    function renderSidebarProduct(barcode) {
        const productData = productMap[barcode];
        const productRow = $(`
            <tr id="sidebar-product-${barcode}">
                <td class="px-4 py-2">${productData.product.name}</td>
                <td class="px-4 py-2">${productData.product.productCode}</td>
                <td class="px-4 py-2 total-price">${productData.totalPrice}</td>
                <td class="px-4 py-2">
                    <input type="number" id="sidebar-quantity-${barcode}" class="quantity-input border p-1" value="${productData.quantity}" min="1" max="${productData.inventory}">
                </td>
                <td class="px-4 py-2">
                    <button class="remove-product-btn bg-red-500 text-white p-1" data-barcode="${barcode}">Xóa</button>
                </td>
            </tr>
        `);
        selectedProductsContainer.append(productRow);

        $(`#sidebar-quantity-${barcode}`).on('change', function () {
            const newQuantity = parseInt($(this).val());
            if (newQuantity > 0 && newQuantity <= productData.inventory) {
                updateProductQuantity(barcode, newQuantity);
            } else {
                alert('Quantity exceeds inventory quantity.');
                $(this).val(productData.quantity);
            }
        });

        $(`.remove-product-btn[data-barcode="${barcode}"]`).click(function () {
            removeProduct(barcode);
        });
    }

    function updateProductQuantity(barcode, newQuantity) {
        const productData = productMap[barcode];
        productData.quantity = newQuantity;
        productData.totalPrice = productData.price * newQuantity;

        $(`#quantity-${barcode}`).text(newQuantity);
        $(`#sidebar-quantity-${barcode}`).val(newQuantity);
        $(`#sidebar-product-${barcode} .total-price`).text(productData.totalPrice.toFixed(2));

        updateTotalPrice();

        if (newQuantity === 0) {
            removeProduct(barcode);
        }
    }

    function removeProduct(barcode) {
        delete productMap[barcode];
        $(`#product-${barcode}`).remove();
        $(`#sidebar-product-${barcode}`).remove();
        updateTotalPrice();
    }

    function updateTotalPrice() {
        let totalPrice = 0;
        let discountTotal = 0;

        for (const barcode in productMap) {
            totalPrice += productMap[barcode].totalPrice;
        }

        if (userPromotion) {
            discountTotal = totalPrice * (userPromotion.value / 100);
        }

        const subtotalPrice = totalPrice - discountTotal;

        totalPriceRaw.text(totalPrice.toFixed(2));
        discountPrice.text(discountTotal.toFixed(2));
        subtotal.text(subtotalPrice.toFixed(2));
    }

    function openUserModal() {
        $.ajax({
            url: 'http://localhost:8080/earnpoints/rank',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (data) {
                if (data.status === 'OK') {
                    const userTableBody = $('#user-table-body');
                    userTableBody.empty(); // Xóa dữ liệu cũ

                    data.data.forEach(user => {
                        const row = `
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${user.userInfoDTO.fullName}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${user.point}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <button type="button" class="text-blue-600 hover:text-blue-900" onclick="selectUser(${user.userInfoDTO.id}, '${user.userInfoDTO.fullName}')">Chọn</button>
                                </td>
                            </tr>
                        `;
                        userTableBody.append(row);
                    });

                    $('#user-modal').removeClass('hidden');
                } else {
                    alert('Unable to load user data');
                }
            },
            error: function (error) {
                console.error('Error loading user data', error);
                alert('Error loading user data');
            }
        });
    }

    function selectUser(id, name) {
        selectedUserId = id;
        selectedUserName = name;
        $('#user-modal').addClass('hidden');
        $('#selected-user-info').removeClass('hidden').find('#user-details').html(`
            <p>${name}</p>
            <p>ID: ${id}</p>
        `);

        fetchUserPromotions(id); // Gọi hàm để lấy khuyến mãi của người dùng
    }

    function fetchUserPromotions(userId) {
        $.ajax({
            url: `http://localhost:8080/promotion/by-user`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            data: { userId: userId }, // Truyền ID người dùng vào đây
            success: function (response) {
                if (response.status === 'OK') {
                    userPromotion = response.data;
                    const promotionDetails = $('#promotion-details');
                    promotionDetails.empty(); // Xóa dữ liệu cũ

                    if (userPromotion) {
                        const promotionInfo = `
                            <div class="p-2 border-b border-gray-200">
                                <p><strong>Tên:</strong> ${userPromotion.name}</p>
                                <p><strong>Value:</strong> ${userPromotion.value}%</p>
                                <p><strong>Start day:</strong> ${userPromotion.startDate}</p>
                                <p><strong>End date:</strong> ${userPromotion.endDate}</p>
                            </div>
                        `;
                        promotionDetails.append(promotionInfo);
                    }

                    console.log('Promotions Loading Successful'); // Log for checking success
                    updateTotalPrice(); // Cập nhật giá trị sau khi có khuyến mãi
                } else {
                    alert('Can Not Load Information User');
                }
            },
            error: function (error) {
                console.error('Error While Loading Information User:', error);
                alert('Error While Loading Information User!');
            }
        });
    }

    function closeModal() {
        $('#user-modal').addClass('hidden');
    }

    function closeViewInvoiceModal() {
        $('#view-invoice-modal').addClass('hidden');
    }

    // Tạo hóa đơn
    $('#create-invoice-button').click(function () {
        const invoiceDetails = {
            barcodeQuantityMap: {},
            invoiceTypeId: 1,
            userId: selectedUserId,
            employeeId: employeeID
        };

        for (const barcode in productMap) {
            invoiceDetails.barcodeQuantityMap[barcode] = productMap[barcode].quantity.toString();
        }

        $.ajax({
            url: 'http://localhost:8080/invoice/create-invoice',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(invoiceDetails),
            headers: {
                Authorization: `Bearer ${token}`,
            },
            success: function (response) {
                if (response.status === 'OK') {
                    alert('The invoice has been created successfully');
                    createdInvoiceId = response.data; // Lưu ID của hóa đơn vừa tạo
                    viewInvoice(createdInvoiceId); // Gọi hàm để hiển thị hóa đơn

                    // Reset lại các giá trị sau khi tạo hóa đơn thành công
                    productMap = {};
                    selectedUserId = null;
                    selectedUserName = null;
                    userPromotion = null;
                    $('#selected-user-info').addClass('hidden');
                    $('#user-details').empty();
                    $('#promotion-details').empty();
                    selectedProductsContainer.empty();
                    updateTotalPrice();
                } else {
                    alert('Unable to create invoice');
                }
            },
            error: function (error) {
                console.error('Error when creating invoice:', error);
                alert('Error when creating invoice:');
            }
        });
    });

    function viewInvoice(invoiceId) {
        $.ajax({
            url: 'http://localhost:8080/invoice/view-invoice',
            method: 'POST',
            data: { invoice: invoiceId },
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.status === 'OK') {
                    const invoiceData = response.data;
                    const invoiceDetails = $('#invoice-details');
                    invoiceDetails.empty();

                    const userInfo = invoiceData.userInfoDTO;
                    const employeeInfo = invoiceData.employeeDTO;
                    const orderDetails = invoiceData.listOrderInvoiceDetail;

                    invoiceDetails.append(`
                    <div class="bg-white rounded-lg shadow-lg px-8 py-10 max-w-xl mx-auto">
                        <div class="flex items-center justify-between mb-8">
                            <div class="flex items-center">
                                <img class="h-8 w-8 mr-2" src="https://tailwindflex.com/public/images/logos/favicon-32x32.png" alt="Logo" />
                                <div class="text-gray-700 font-semibold text-lg">2KS 1NET</div>
                            </div>
                            <div class="text-gray-700">
                                <div class="font-bold text-xl mb-2">HÓA ĐƠN</div>
                                <div class="text-sm">Date: ${new Date(invoiceData.createdDate).toLocaleDateString()}</div>
                                <div class="text-sm">Invoice #: ${invoiceData.id}</div>
                            </div>
                        </div>
                        <div class="border-b-2 border-gray-300 pb-8 mb-8">
                            <h2 class="text-2xl font-bold mb-4">Thông Tin Khách Hàng</h2>
                            <div class="text-gray-700 mb-2">${userInfo.fullName}</div>
                            <div class="text-gray-700 mb-2">ID: ${userInfo.id}</div>
                        </div>
                        <div class="border-b-2 border-gray-300 pb-8 mb-8">
                            <h2 class="text-2xl font-bold mb-4">Thông Tin Nhân Viên</h2>
                            <div class="text-gray-700 mb-2">${employeeInfo.firstName} ${employeeInfo.lastName}</div>
                            <div class="text-gray-700 mb-2">ID: ${employeeInfo.id}</div>
                        </div>
                        <table class="w-full text-left mb-8">
                            <thead>
                                <tr>
                                    <th class="text-gray-700 font-bold uppercase py-2">Sản phẩm</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Mã sản phẩm</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Số lượng</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Tổng giá</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${orderDetails.map(order => `
                                <tr>
                                    <td class="py-4 text-gray-700">${order.productDTO.name}</td>
                                    <td class="py-4 text-gray-700">${order.productDTO.productCode}</td>
                                    <td class="py-4 text-gray-700">${order.quantity}</td>
                                    <td class="py-4 text-gray-700">${order.totalPrice}</td>
                                </tr>
                                `).join('')}
                            </tbody>
                        </table>
                        <div class="flex justify-end mb-8">
                            <div class="text-gray-700 mr-2">Tổng giá gốc:</div>
                            <div class="text-gray-700">${invoiceData.totalPriceRaw}</div>
                        </div>
                        <div class="flex justify-end mb-8">
                            <div class="text-gray-700 mr-2">Giá giảm:</div>
                            <div class="text-gray-700">${invoiceData.discountPrice}</div>
                        </div>
                        <div class="flex justify-end mb-8">
                            <div class="text-gray-700 mr-2">Tổng giá:</div>
                            <div class="text-gray-700 font-bold text-xl">${invoiceData.totalPrice}</div>
                        </div>
                        <div class="border-t-2 border-gray-300 pt-8 mb-8">
                            <div class="text-gray-700 mb-2">Thanh toán trong vòng 30 ngày. Thanh toán trễ sẽ bị tính phí.</div>
                            <div class="text-gray-700 mb-2">Vui lòng viết séc thanh toán cho Tên Công Ty Của Bạn và gửi đến:</div>
                            <div class="text-gray-700">FPT University</div>
                        </div>
                    </div>
                    `);

                    $('#view-invoice-modal').removeClass('hidden');
                } else {
                    alert('Không thể tải chi tiết hóa đơn');
                }
            },
            error: function (error) {
                console.error('Không thể tải chi tiết hóa đơn', error);
                alert('Không thể tải chi tiết hóa đơn !!!');
            }
        });
    }

    // Hàm để đóng modal
    function closeViewInvoiceModal() {
        $('#view-invoice-modal').addClass('hidden');
    }


    // Đảm bảo rằng các hàm đã được định nghĩa trước khi gọi sự kiện onclick
    window.openUserModal = openUserModal;
    window.closeModal = closeModal;
    window.selectUser = selectUser;
    window.closeViewInvoiceModal = closeViewInvoiceModal;
});
