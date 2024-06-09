let exchangeRateId = ''; // sử dụng khi click vào side bar , nó dùng để lưu trữ khi truyền về để save invoice details
let addedBarcodes = new Set(); // Set to store added barcodes
let selectedProducts = []; // Danh sách sản phẩm đã chọn
let discountValue = 0;

$(document).ready(function () {
    $('#toggleFeaturesButton').click(toggleFeatures);
    $('#profileOverviewButton').click(redirectToProfileOverview);
    $('#addProductButton').click(function () {
        var barcode = $('#barcodeInput').val();
        if (checkBarcode(barcode)) {
            addProductToInvoice(barcode, exchangeRateId);
        }
    });
    $('#sideBarButton').click(toggleSideTurnOnOff);
    $('#ERP_DEF-button').click(fetchExchangeRateId);

    // Bổ sung sự kiện click vào các div sản phẩm để tăng quantity
    $('#product-info').on('click', 'div[data-id]', function () {
        const productId = $(this).data('id');
        const productName = $(this).find('.text-xl').text();
        const productCode = $(this).find('.text-xl').text();
        const productFee = parseFloat($(this).find('.bg-white').text().replace('$', ''));

        const product = {
            id: productId,
            name: productName,
            productCode: productCode,
            price: productFee,
            quantity: 1
        };
        addProductToSelected(product);
    });
});

const token = localStorage.getItem("token");

function toggleSideTurnOnOff() {
    $('#mySidebar').toggleClass('hidden');
    $('#mainContent').toggleClass('col-span-5 col-span-7');
}

function toggleFeatures() {
    $('#menu1').slideToggle();
    $('#icon1').toggleClass('rotate-180');
}

function redirectToProfileOverview() {
    window.location.href = 'SaleStaff.html';
}

function checkBarcode(barcode) {
    if (addedBarcodes.has(barcode)) {
        alert('Sản phẩm này đã được nhập rồi.');
        return false;
    }
    return true;
}

function addProductToInvoice(barcode, exchangeRateId) {
    if (!exchangeRateId) {
        console.error('Exchange Rate ID is missing.');
        return;
    }

    if (addedBarcodes.has(barcode)) {
        alert('Sản phẩm này đã được nhập rồi.');
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/order/invoice-detail',
        type: 'GET',
        data: {
            barcode: barcode,
            exchangeRateId: exchangeRateId
        },
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            console.log('Dữ liệu đã được gửi thành công:', response);
            if (response.status === 'OK' && response.data && response.data.productDTO) {
                appendProductInfo(response.data.productDTO);
                addedBarcodes.add(barcode); // Thêm barcode vào set
                addProductToSelected({
                    id: response.data.productDTO.id,
                    name: response.data.productDTO.name,
                    productCode: response.data.productDTO.productCode,
                    price: response.data.productDTO.fee,
                    quantity: 1,
                    barcode: barcode
                });
                if (Array.isArray(response.data.listPromotion)) {
                    displayPromotions(response.data.listPromotion); // Gọi hàm để hiển thị thông tin khuyến mãi
                } else {
                    console.error('Promotions is not an array');
                }
            } else {
                console.error('Không tìm thấy thông tin sản phẩm');
            }
        },
        error: function (error) {
            console.error('Đã xảy ra lỗi khi gửi dữ liệu:', error);
        }
    });
}

function fetchExchangeRateId() {
    $.ajax({
        url: 'http://localhost:8080/policy/getExchangeApply',
        type: 'GET',
        data: {
            invoiceType: 1
        },
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            console.log('Dữ liệu chính sách đã được lấy thành công:', response);
            exchangeRateId = response.data[0].id; // Lưu giá trị exchangeRateId để sử dụng sau này
        },
        error: function (error) {
            console.error('Đã xảy ra lỗi khi lấy dữ liệu chính sách:', error);
        }
    });
}

// Cập nhật hàm appendProductInfo để thêm data-id
function appendProductInfo(product) {
    var productInfoHtml = `
        <div class="flex-shrink-0 m-6 relative overflow-hidden bg-orange-500 rounded-lg max-w-xs shadow-lg" data-id="${product.id}">
            <svg class="absolute bottom-0 left-0 mb-8" viewBox="0 0 375 283" fill="none" style="transform: scale(1.5); opacity: 0.1;">
                <rect x="159.52" y="175" width="152" height="152" rx="8" transform="rotate(-45 159.52 175)" fill="white" />
                <rect y="107.48" width="152" height="152" rx="8" transform="rotate(-45 0 107.48)" fill="white" />
            </svg>
            <div class="relative pt-10 px-10 flex items-center justify-center">
                <div class="block absolute w-48 h-48 bottom-0 left-0 -mb-24 ml-3" style="background: radial-gradient(black, transparent 60%); transform: rotate3d(0, 0, 1, 20deg) scale3d(1, 0.6, 1); opacity: 0.2;"></div>
                <img class="relative w-40" src="https://user-images.githubusercontent.com/2805249/64069899-8bdaa180-cc97-11e9-9b19-1a9e1a254c18.png" alt="">
            </div>
            <div class="relative text-white px-6 pb-6 mt-6">
                <div class="flex justify-between">
                    <div>
                        <span class="block font-semibold text-xl">${product.productCode}</span>
                        <span class="block bg-white rounded-full text-orange-500 text-xs font-bold px-3 py-2 leading-none flex items-center">$${product.fee}</span>
                    </div>
                    <button class="bg-white text-orange-500 font-bold py-2 px-4 rounded" onclick="viewDetail(${product.id})">View Detail</button>
                </div>
            </div>
        </div>
    `;
    $('#product-info').append(productInfoHtml);
}


function updateSelectedProductsTable() {
    let totalPriceRaw = 0;
    let discountPrice = 0;
    let totalPrice = 0;

    $('#selected-products').empty(); // Xóa bảng trước khi thêm dữ liệu mới

    selectedProducts.forEach(product => {
        const row = `
            <tr>
                <td class="px-4 py-2">${product.name}</td>
                <td class="px-4 py-2">${product.productCode}</td>
                <td class="px-4 py-2">$${product.price.toFixed(2)}</td>
                <td class="px-4 py-2">${product.quantity}</td>
                <td class="px-4 py-2">
                    <button class="text-red-600 hover:text-red-900" onclick="removeProduct(${product.id})">Remove</button>
                </td>
            </tr>
        `;
        $('#selected-products').append(row);
        totalPriceRaw += product.price * product.quantity;
    });

    discountPrice = totalPriceRaw * (discountValue / 100);
    totalPrice = totalPriceRaw - discountPrice;

    $('#totalPriceRaw').text(`$${totalPriceRaw.toFixed(2)}`);
    $('#discountPrice').text(`$${discountPrice.toFixed(2)}`);
    $('#subtotal').text(`$${totalPrice.toFixed(2)}`);
}


function addProductToSelected(product) {
    const existingProduct = selectedProducts.find(p => p.id === product.id);
    if (existingProduct) {
        existingProduct.quantity += 1;
    } else {
        selectedProducts.push({ ...product, quantity: 1, barcode: product.barcode });
    }
    updateSelectedProductsTable();
}


function removeProduct(productId) {
    const productIndex = selectedProducts.findIndex(product => product.id === productId);
    if (productIndex !== -1) {
        const product = selectedProducts[productIndex];
        product.quantity -= 1;
        if (product.quantity === 0) {
            const barcode = product.barcode;
            selectedProducts.splice(productIndex, 1);
            // Xóa sản phẩm khỏi product-info khi quantity bằng 0
            $(`#product-info div[data-id="${productId}"]`).remove();
            addedBarcodes.delete(barcode); // Xóa barcode khỏi set
            console.log(addedBarcodes);
        }
        updateSelectedProductsTable();
    }
}

function viewDetail(productId) {
    $.ajax({
        url: 'http://localhost:8080/gemStone/product',
        type: 'GET',
        data: { id: productId },
        success: function (response) {
            if (response.status === 'OK') {
                showProductModal(response.data);
            } else {
                console.error('Không tìm thấy thông tin sản phẩm');
            }
        },
        error: function (error) {
            console.error('Đã xảy ra lỗi khi lấy dữ liệu sản phẩm:', error);
        }
    });
}

function displayPromotions(promotions) {
    if (!Array.isArray(promotions)) {
        console.error('Promotions is not an array');
        return;
    }

    var promotionsHtml = '';
    promotions.forEach(function (promotion) {
        promotionsHtml += `
            <div class="flex items-center space-x-4">
                <img class="w-12 h-12 mr-4" src="${promotion.image}" alt="${promotion.name}">
                <div>
                    <p class="text-sm font-semibold">${promotion.name}</p>
                    <p class="text-xs text-gray-500">Start: ${promotion.startDate}</p>
                    <p class="text-xs text-gray-500">End: ${promotion.endDate}</p>
                    <p class="text-xs text-gray-500">Last Modified: ${promotion.lastModified}</p>
                </div>
            </div>
        `;
    });

    $('#promotionList').html(promotionsHtml);
}

function showProductModal(gemstones) {
    if (gemstones.length === 0) return;

    const product = gemstones[0].product;

    // Sử dụng URL hình ảnh cố định
    $('#productImage').attr('src', 'https://via.placeholder.com/300');

    $('#productCode').text(product.productCode);
    $('#productFee').text(`$${product.fee}`);
    $('#productPrice').text(`$${product.materialDTO.priceAtTime}`);

    var productDetailsHtml = `
        <div>
            <p class="text-lg font-medium text-gray-900">Name: ${product.name}</p>
            <p class="text-sm text-gray-500">Barcode: ${product.barCode}</p>
            <p class="text-sm text-gray-500">Quantity: ${gemstones[0].quantity}</p>
        </div>
    `;

    $('#productDetails').html(productDetailsHtml);

    // Hiển thị thông tin đá quý
    var gemstonesHtml = '';
    gemstones.forEach(function (gemstone) {
        gemstonesHtml += `
            <tr>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${gemstone.color}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${gemstone.clarity}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${gemstone.carat}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">$${gemstone.price}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${gemstone.gemstoneType.name}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${gemstone.gemstoneCategory.name}</td>
            </tr>
        `;
    });

    $('#gemstoneList').html(gemstonesHtml);

    // Show modal
    $('#productModal').removeClass('hidden');
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

function closeModal() {
    $('#user-modal').addClass('hidden');
    $('#productModal').addClass('hidden');
}

function selectUser(userId, userName) {
    // Hiển thị thông tin người dùng đã chọn
    const userDetails = `
        <p><strong>ID:</strong> ${userId}</p>
        <p><strong>Name:</strong> ${userName}</p>
    `;
    $('#user-details').html(userDetails);
    $('#selected-user-info').removeClass('hidden');

    // Gọi API lấy thông tin khuyến mãi của người dùng
    $.ajax({
        url: `http://localhost:8080/promotion/by-user?userId=${userId}`,
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        success: function (data) {
            if (data.status === 'OK') {
                const promotionDetails = data.data.map(promotion => `
                    <div class="p-2 mb-2 border-b border-gray-200">
                        <p><strong>Name:</strong> ${promotion.name}</p>
                        <p><strong>Value:</strong> ${promotion.value}</p>
                    </div>
                `).join('');
                $('#promotion-details').html(promotionDetails);

                if (data.data.length > 0) {
                    discountValue = data.data[0].value; // Lấy giá trị giảm giá từ promotion
                } else {
                    discountValue = 0;
                }

                updateSelectedProductsTable();
            } else {
                alert('Failed to load promotions data');
            }
        },
        error: function (error) {
            console.error('Error fetching promotions data:', error);
            alert('Error fetching promotions data');
        }
    });

    closeModal();
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
                const promotionDetails = $('#promotion-details');
                promotionDetails.empty(); // Clear existing data

                data.data.forEach(promotion => {
                    const promotionInfo = `
                        <div class="p-2 border-b border-gray-200">
                            <p><strong>Name:</strong> ${promotion.name}</p>
                            <p><strong>Value:</strong> ${promotion.value}%</p>
                        </div>
                    `;
                    promotionDetails.append(promotionInfo);
                });

                $('#selected-user-info').removeClass('hidden');
                console.log('User promotions loaded successfully'); // Log for checking success
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