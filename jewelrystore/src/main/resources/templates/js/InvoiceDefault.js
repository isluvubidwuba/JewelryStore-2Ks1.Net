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
    let userPromotions = [];
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
                    console.log("Check link hình ảnh của card product : " + productData.productDTO.imgPath);
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
                alert('Số lượng vượt quá số lượng tồn kho.');
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

        if (userPromotions.length > 0) {
            userPromotions.forEach(promotion => {
                discountTotal += totalPrice * (promotion.value / 100);
            });
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
                    userTableBody.empty(); // Clear existing data

                    data.data.forEach(user => {
                        const row = `
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${user.userInfoDTO.fullName}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${user.point}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <button type="button" class="text-blue-600 hover:text-blue-900" onclick="selectUser(${user.userInfoDTO.id}, '${user.userInfoDTO.fullName}')">Select</button>
                                </td>
                            </tr>
                        `;
                        userTableBody.append(row);
                    });

                    $('#user-modal').removeClass('hidden');
                } else {
                    alert('Failed to load user data');
                }
            },
            error: function (error) {
                console.error('Error fetching user data:', error);
                alert('Error fetching user data');
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
            success: function (data) {
                if (data.status === 'OK') {
                    userPromotions = data.data;
                    const promotionDetails = $('#promotion-details');
                    promotionDetails.empty(); // Clear existing data

                    userPromotions.forEach(promotion => {
                        const promotionInfo = `
                            <div class="p-2 border-b border-gray-200">
                                <p><strong>Name:</strong> ${promotion.name}</p>
                                <p><strong>Value:</strong> ${promotion.value}%</p>
                            </div>
                        `;
                        promotionDetails.append(promotionInfo);
                    });

                    console.log('User promotions loaded successfully'); // Log for checking success
                    updateTotalPrice(); // Cập nhật giá trị sau khi có khuyến mãi
                } else {
                    alert('Failed to load user promotions');
                }
            },
            error: function (error) {
                console.error('Error fetching user promotions:', error);
                alert('Error fetching user promotions');
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
                    alert('Invoice created successfully');
                    createdInvoiceId = response.data; // Lưu ID của hóa đơn vừa tạo
                    viewInvoice(createdInvoiceId); // Gọi hàm để hiển thị hóa đơn

                    // Reset lại các giá trị sau khi tạo hóa đơn thành công
                    productMap = {};
                    selectedUserId = null;
                    selectedUserName = null;
                    userPromotions = [];
                    $('#selected-user-info').addClass('hidden');
                    $('#user-details').empty();
                    $('#promotion-details').empty();
                    selectedProductsContainer.empty();
                    updateTotalPrice();
                } else {
                    alert('Failed to create invoice');
                }
            },
            error: function (error) {
                console.error('Error creating invoice:', error);
                alert('Error creating invoice');
            }
        });
    });

    function viewInvoice(invoiceId) {
        $.ajax({
            url: 'http://localhost:8080/invoice/view-invoice',
            method: 'POST',
            data: { invoice: invoiceId }, // Truyền tham số trực tiếp
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.status === 'OK') {
                    const invoiceData = response.data;
                    const invoiceDetails = $('#invoice-details');
                    invoiceDetails.empty(); // Clear existing data

                    const userInfo = invoiceData.userInfoDTO;
                    const employeeInfo = invoiceData.employeeDTO;
                    const orderDetails = invoiceData.listOrderInvoiceDetail;

                    invoiceDetails.append(`
                        <div class="grid grid-cols-12 gap-4">
                            <div class="col-span-6">
                                <h3 class="text-lg font-medium">User Information</h3>
                                <p><strong>ID:</strong> ${userInfo.id}</p>
                                <p><strong>Name:</strong> ${userInfo.fullName}</p>
                            </div>
                            <div class="col-span-6">
                                <h3 class="text-lg font-medium">Employee Information</h3>
                                <p><strong>ID:</strong> ${employeeInfo.id}</p>
                                <p><strong>Name:</strong> ${employeeInfo.firstName} ${employeeInfo.lastName}</p>
                            </div>
                        </div>
                        <hr class="my-4">
                    `);

                    // Add invoice info
                    invoiceDetails.append(`
                        <div class="grid grid-cols-12 gap-4">
                            <div class="col-span-6">
                                <h3 class="text-lg font-medium">Invoice Information</h3>
                                <p><strong>ID:</strong> ${invoiceData.id}</p>
                                <p><strong>Total Price Raw:</strong> ${invoiceData.totalPriceRaw}</p>
                                <p><strong>Discount Price:</strong> ${invoiceData.discountPrice}</p>
                                <p><strong>Total Price:</strong> ${invoiceData.totalPrice}</p>
                            </div>
                        </div>
                        <hr class="my-4">
                    `);

                    // Add order details
                    invoiceDetails.append('<div><h3 class="text-lg font-medium">Order Details</h3></div>');
                    orderDetails.forEach(order => {
                        const product = order.productDTO;
                        invoiceDetails.append(`
                            <div class="grid grid-cols-12 gap-4">
                                <div class="col-span-6">
                                    <p><strong>Product Name:</strong> ${product.name}</p>
                                    <p><strong>Product Code:</strong> ${product.productCode}</p>
                                    <p><strong>Quantity:</strong> ${order.quantity}</p>
                                    <p><strong>Total Price:</strong> ${order.totalPrice}</p>
                                </div>
                                <div class="col-span-6">
                                    <img src="${product.imgPath}" alt="${product.name}" class="w-full h-auto my-2 rounded-md">
                                </div>
                            </div>
                            <hr class="my-4">
                        `);
                    });

                    $('#view-invoice-modal').removeClass('hidden');
                } else {
                    alert('Failed to load invoice details');
                }
            },
            error: function (error) {
                console.error('Error loading invoice details:', error);
                alert('Error loading invoice details');
            }
        });
    }


    // Đảm bảo rằng các hàm đã được định nghĩa trước khi gọi sự kiện onclick
    window.openUserModal = openUserModal;
    window.closeModal = closeModal;
    window.selectUser = selectUser;
    window.closeViewInvoiceModal = closeViewInvoiceModal;
});
