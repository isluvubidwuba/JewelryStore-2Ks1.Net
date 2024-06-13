$(document).ready(function () {
    const productMap = new Map();
    initAddBarcodeButton(productMap);
    initProductClickHandler(productMap);
});

const token = localStorage.getItem("token");

function initAddBarcodeButton(productMap) {
    $('#add-barcode-button').click(function () {
        addBarcodeToSell(productMap);
    });
}

function addBarcodeToSell(productMap) {
    const barcode = $('#barcode-input').val();
    const invoiceTypeId = 1;
    const quantity = 1;

    if (productMap.has(barcode)) {
        alert('Sản phẩm đã tồn tại trong danh sách.');
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/invoice/create-detail',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            barcode: barcode,
            invoiceTypeId: invoiceTypeId,
            quantity: quantity
        }),
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (data) {
            if (data.status === 'OK') {
                const productData = {
                    ...data.data,
                    quantity: quantity,
                    maxQuantity: data.data.quantity // lưu lại số lượng tồn kho tối đa
                };
                productMap.set(barcode, productData);
                updateSidebar(productMap);
                displayProductCard(productData);
            } else {
                alert('Lỗi: ' + data.desc);
            }
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });
}

function displayProductCard(productData) {
    const productInfo = productData.productDTO;
    const productSoldDiv = $('#product-sold');

    const card = `
        <div class="product-card p-4 mb-4 border shadow-sm" data-barcode="${productInfo.barCode}">
            <h3 class="font-bold text-lg">${productInfo.name}</h3>
            <p>Mã sản phẩm: ${productInfo.productCode}</p>
            <p>Barcode: ${productInfo.barCode}</p>
            <p>Giá: ${productInfo.fee}</p>
            <p>Trọng lượng: ${productInfo.weight}</p>
            <p>Chất liệu: ${productInfo.materialDTO.name}</p>
            <p>Độ tinh khiết: ${productInfo.materialDTO.purity}</p>
            <p>Loại sản phẩm: ${productInfo.productCategoryDTO.name}</p>
            <p>Quầy: ${productInfo.counterDTO.name}</p>
            <img src="${productInfo.imgPath}" alt="${productInfo.name}" class="product-image mt-2">
        </div>
    `;

    productSoldDiv.append(card);
}

function updateSidebar(productMap) {
    const selectedProductsContainer = $('#selected-products');
    selectedProductsContainer.empty();

    productMap.forEach(function (productData, barcode) {
        const productInfo = productData.productDTO;

        const row = `
            <tr data-barcode="${barcode}" class="product-row">
                <td class="px-4 py-2">${productInfo.name}</td>
                <td class="px-4 py-2">${productInfo.productCode}</td>
                <td class="px-4 py-2">${productInfo.fee}</td>
                <td class="px-4 py-2">
                    <button class="bg-blue-500 text-white px-2 py-1 rounded increase-quantity">+</button>
                    <span class="quantity mx-2">${productData.quantity}</span>
                    <button class="bg-red-500 text-white px-2 py-1 rounded decrease-quantity">-</button>
                </td>
                <td class="px-4 py-2">
                    <button class="bg-red-500 text-white px-2 py-1 rounded remove-product">Xóa</button>
                </td>
            </tr>
        `;

        selectedProductsContainer.append(row);
    });

    updateTotalPrice(productMap);
}

function initProductClickHandler(productMap) {
    $('#selected-products').on('click', '.increase-quantity', function () {
        const barcode = $(this).closest('.product-row').data('barcode');
        const productData = productMap.get(barcode);
        if (productData) {
            if (productData.quantity < productData.maxQuantity) {
                productData.quantity += 1;
                $(this).siblings('.quantity').text(productData.quantity);
                updateTotalPrice(productMap); // cập nhật lại giá sau khi thay đổi số lượng
            } else {
                alert('Số lượng sản phẩm không được vượt quá định mức tồn kho.');
            }
        }
    });

    $('#selected-products').on('click', '.decrease-quantity', function () {
        const barcode = $(this).closest('.product-row').data('barcode');
        const productData = productMap.get(barcode);
        if (productData) {
            productData.quantity -= 1;

            if (productData.quantity === 0) {
                productMap.delete(barcode);
                $(this).closest('.product-row').remove();
            } else {
                $(this).siblings('.quantity').text(productData.quantity);
            }

            updateTotalPrice(productMap); // cập nhật lại giá sau khi thay đổi số lượng
        }
    });

    $('#selected-products').on('click', '.remove-product', function () {
        const barcode = $(this).closest('.product-row').data('barcode');
        productMap.delete(barcode);
        $(this).closest('.product-row').remove();
        updateTotalPrice(productMap); // cập nhật lại giá sau khi xoá sản phẩm
    });
}

function updateTotalPrice(productMap) {
    let totalPriceRaw = 0;
    let discountPrice = 0;
    let subtotal = 0;

    productMap.forEach(function (productData) {
        totalPriceRaw += productData.productDTO.fee * productData.quantity;
        // Giả sử discount là một thuộc tính trong productData
        discountPrice += (productData.productDTO.fee * productData.quantity) * (productData.discount || 0) / 100;
        subtotal += productData.productDTO.fee * productData.quantity - discountPrice;
    });

    $('#totalPriceRaw').text('$' + totalPriceRaw.toFixed(2));
    $('#discountPrice').text('$' + discountPrice.toFixed(2));
    $('#subtotal').text('$' + subtotal.toFixed(2));
}




// Set up cho lưu hóa đơn
$('#save-invoice-button').click(function () {
    const invoiceDetails = Array.from(productMap.values()).map(productData => ({
        barcode: productData.productDTO.barCode,
        quantity: productData.quantity
    }));

    $.ajax({
        url: 'http://localhost:8080/invoice/save',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(invoiceDetails),
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            alert('Hóa đơn đã được lưu thành công.');
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });
});
