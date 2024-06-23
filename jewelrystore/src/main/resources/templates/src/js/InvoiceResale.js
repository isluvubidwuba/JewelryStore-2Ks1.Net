$(document).ready(function () {

    const token = localStorage.getItem("token");
    let currentUser = null; // Biến lưu thông tin người dùng hiện tại
    let employeeId = localStorage.getItem("userId"); // ID của nhân viên từ localStorage

    $('#add-invoiceid-button').click(function () {
        var invoiceId = $('#invoiceid-input').val();
        if (invoiceId) {
            var formData = new FormData();
            formData.append('invoice', invoiceId);

            $.ajax({
                url: 'http://localhost:8080/invoice/view-invoice',
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                success: function (response) {
                    if (response.status === "OK") {
                        if (currentUser === null) {
                            currentUser = response.data.userInfoDTO;
                            updateUserInfo(currentUser);
                            updateInvoiceDetails(response.data, invoiceId);
                        } else if (currentUser.id === response.data.userInfoDTO.id) {
                            updateInvoiceDetails(response.data, invoiceId);
                        } else {
                            alert("Người dùng khác nhau. Vui lòng kiểm tra lại.");
                        }
                    } else {
                        alert("Không tìm thấy invoice");
                    }
                },
                error: function (error) {
                    console.error('Lỗi khi lấy invoice:', error);
                }
            });
        } else {
            alert("Vui lòng nhập Invoice ID");
        }
    });

    function updateInvoiceDetails(invoice, invoiceId) {
        var productTableBody = $('#product-table-body');
        productTableBody.empty();

        $.each(invoice.listOrderInvoiceDetail, function (index, detail) {
            var row = '<tr>' +
                '<td class="px-4 py-2">' + detail.id + '</td>' +
                '<td class="px-4 py-2">' + detail.productDTO.productCode + '</td>' +
                '<td class="px-4 py-2">' + detail.productDTO.name + '</td>' +
                '<td class="px-4 py-2">' + formatCurrency(detail.price) + '</td>' +
                '<td class="px-4 py-2">' +
                '<button class="add-product-button bg-green-500 hover:bg-green-700 text-white p-2" data-barcode="' + detail.productDTO.barCode + '" data-available-return-quantity="' + detail.availableReturnQuantity + '" data-price="' + detail.price + '" data-detail-id="' + detail.id + '">Thêm</button>' +
                '</td>' +
                '</tr>';
            productTableBody.append(row);
        });

        $('.add-product-button').click(function () {
            var barcode = $(this).data('barcode');
            var availableReturnQuantity = $(this).data('available-return-quantity');
            var price = $(this).data('price');
            var detailId = $(this).data('detail-id');
            var quantity = 1; // Số lượng mặc định

            var existingProductRow = $('#selected-products').find('tr[data-barcode="' + barcode + '"][data-detail-id="' + detailId + '"]');
            if (existingProductRow.length > 0) {
                var currentQuantity = parseInt(existingProductRow.find('.product-quantity-input').val());
                var totalAvailableQuantity = parseInt(existingProductRow.find('.product-available-quantity').text());
                if (currentQuantity + quantity <= totalAvailableQuantity) {
                    currentQuantity += quantity;
                    existingProductRow.find('.product-quantity-input').val(currentQuantity);

                    var unitPrice = parseFloat(existingProductRow.find('.product-price-unit').text().replace(/[^\d.-]/g, ''));
                    var newPrice = unitPrice * currentQuantity;
                    existingProductRow.find('.product-price').text(formatCurrency(newPrice));

                    updateTotalPrice();
                } else {
                    alert('Số lượng không hợp lệ. Vui lòng kiểm tra lại.');
                }
            } else {
                if (quantity <= availableReturnQuantity) {
                    var requestData = {
                        invoiceTypeId: 3,
                        quantity: quantity,
                        barcode: barcode
                    };

                    $.ajax({
                        url: 'http://localhost:8080/invoice/create-detail',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(requestData),
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                        success: function (response) {
                            if (response.status === "OK") {
                                addProductToSidebar(response.data, availableReturnQuantity, detailId);
                            } else {
                                alert("Lỗi khi tạo invoice");
                            }
                        },
                        error: function (error) {
                            console.error('Lỗi khi tạo invoice:', error);
                        }
                    });
                } else {
                    alert('Số lượng không hợp lệ. Vui lòng kiểm tra lại.');
                }
            }
        });
    }

    function updateUserInfo(userInfo) {
        var userInfoDiv = $('#user-details');
        userInfoDiv.empty();
        userInfoDiv.append('<p><strong>Full Name:</strong> ' + userInfo.fullName + '</p>');
        userInfoDiv.append('<p><strong>Phone Number:</strong> ' + userInfo.phoneNumber.trim() + '</p>');
        userInfoDiv.append('<p><strong>Email:</strong> ' + userInfo.email + '</p>');
        $('#selected-user-info').removeClass('hidden');
    }

    function addProductToSidebar(data, availableReturnQuantity, detailId) {
        // Hiển thị các chi tiết sản phẩm trong sidebar
        var selectedProductsContainer = $('#selected-products');
        var row = '<tr data-barcode="' + data.productDTO.barCode + '" data-detail-id="' + detailId + '">' +
            '<td class="px-4 py-2">' + detailId + '</td>' +
            '<td class="px-4 py-2">' + data.productDTO.name + '</td>' +
            '<td class="px-4 py-2">' + data.productDTO.productCode + '</td>' +
            '<td class="px-4 py-2 product-price">' + formatCurrency(data.price * data.quantity) + '</td>' +
            '<td class="px-4 py-2">' +
            '<input type="number" class="product-quantity-input border p-1" value="' + data.quantity + '" min="1" max="' + availableReturnQuantity + '">' +
            '</td>' +
            '<td class="px-4 py-2">' +
            '<button class="remove-product-button bg-red-500 hover:bg-red-700 text-white p-2">Xóa</button>' +
            '</td>' +
            '<td class="px-4 py-2 product-price-unit" style="display:none">' + data.price + '</td>' +
            '<td class="px-4 py-2 product-available-quantity" style="display:none">' + availableReturnQuantity + '</td>' +
            '</tr>';
        selectedProductsContainer.append(row);
        updateTotalPrice();

        $('.product-quantity-input').off('change').on('change', function () {
            var newQuantity = parseInt($(this).val());
            var maxQuantity = parseInt($(this).attr('max'));
            if (newQuantity > maxQuantity) {
                alert('Số lượng không hợp lệ. Vui lòng kiểm tra lại.');
                $(this).val(maxQuantity);
                newQuantity = maxQuantity;
            } else if (newQuantity <= 0) {
                $(this).closest('tr').remove();
            }
            updateProductPriceAndTotal($(this));
        });

        $('.remove-product-button').off('click').on('click', function () {
            $(this).closest('tr').remove();
            updateTotalPrice();
        });
    }

    function updateProductPriceAndTotal(quantityInput) {
        var row = quantityInput.closest('tr');
        var unitPrice = parseFloat(row.find('.product-price-unit').text().replace(/[^\d.-]/g, ''));
        var newQuantity = parseInt(quantityInput.val());
        var newPrice = unitPrice * newQuantity;
        row.find('.product-price').text(formatCurrency(newPrice));
        updateTotalPrice();
    }

    function updateTotalPrice() {
        var totalPrice = 0;
        $('#selected-products tr').each(function () {
            var quantity = parseInt($(this).find('.product-quantity-input').val());
            var unitPrice = parseFloat($(this).find('.product-price-unit').text().replace(/[^\d.-]/g, ''));
            totalPrice += quantity * unitPrice;
        });
        $('#total-price').text(formatCurrency(totalPrice));
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(amount);
    }

    $('#checkout-button').click(function () {
        var barcodeQuantityMap = {};

        $('#selected-products tr').each(function () {
            var detailId = $(this).data('detail-id');
            var quantity = parseInt($(this).find('.product-quantity-input').val());

            barcodeQuantityMap[detailId] = quantity;
        });

        var requestData = {
            barcodeQuantityMap: barcodeQuantityMap,
            invoiceTypeId: 3,
            userId: currentUser.id,
            employeeId: employeeId,
            payment: "COD",
            note: "Ghi chú buyback"
        };

        $.ajax({
            url: 'http://localhost:8080/invoice/buyback',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            headers: {
                Authorization: `Bearer ${token}`,
            },
            success: function (response) {
                if (response.status === "OK") {
                    alert("Buyback thành công!");
                    viewInvoice(response.data); // Thêm dòng này để gọi hàm viewInvoice
                } else {
                    alert("Lỗi khi buyback.");
                }
            },
            error: function (error) {
                console.error('Lỗi khi buyback:', error);
            }
        });
    });

    // Hàm viewInvoice để hiển thị chi tiết hóa đơn
    function viewInvoice(invoiceId) {
        $.ajax({
            url: "http://localhost:8080/invoice/view-invoice",
            method: "POST",
            data: { invoice: invoiceId },
            headers: {
                Authorization: `Bearer ${token}`,
            },
            success: function (response) {
                if (response.status === "OK") {
                    const invoiceData = response.data;
                    const invoiceDetails = $("#invoice-details");
                    invoiceDetails.empty();

                    const userInfo = invoiceData.userInfoDTO;
                    const employeeInfo = invoiceData.employeeDTO;
                    const orderDetails = invoiceData.listOrderInvoiceDetail;

                    invoiceDetails.append(`
                        <div class="bg-white rounded-lg shadow-lg px-8 py-10 max-w-7xl mx-auto">
                            <div class="flex items-center justify-between mb-8">
                                <div class="flex items-center">
                                    <img class="h-8 w-8 mr-2" src="https://tailwindflex.com/public/images/logos/favicon-32x32.png" alt="Logo" />
                                    <div class="text-gray-700 font-semibold text-lg">2KS 1NET</div>
                                </div>
                                <div class="text-gray-700 text-right">
                                    <div class="font-bold text-xl mb-2">HÓA ĐƠN</div>
                                    <div class="text-sm">Date: ${new Date(invoiceData.createdDate).toLocaleDateString()}</div>
                                    <div class="text-sm">Invoice #: ${invoiceData.id}</div>
                                </div>
                            </div>
                            <div class="border-b-2 border-gray-300 pb-8 mb-8">
                                <h2 class="text-2xl font-bold mb-4">Thông Tin Khách Hàng và Nhân Viên</h2>
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <div class="text-gray-700 mb-2"><strong>Khách Hàng:</strong> ${userInfo.fullName}</div>
                                        <div class="text-gray-700 mb-2"><strong>ID:</strong> ${userInfo.id}</div>
                                    </div>
                                    <div>
                                        <div class="text-gray-700 mb-2"><strong>Nhân Viên:</strong> ${employeeInfo.firstName} ${employeeInfo.lastName}</div>
                                        <div class="text-gray-700 mb-2"><strong>ID:</strong> ${employeeInfo.id}</div>
                                    </div>
                                </div>
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
                                        <td class="py-4 text-gray-700">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(order.totalPrice)}</td>
                                    </tr>`).join("")}
                                </tbody>
                            </table>
                            <div class="grid grid-cols-2 gap-4">
                                <div class="text-gray-700">Tổng giá gốc:</div>
                                <div class="text-gray-700 text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(invoiceData.totalPriceRaw)}</div>
                                <div class="text-gray-700">Giá giảm:</div>
                                <div class="text-gray-700 text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(invoiceData.discountPrice)}</div>
                                <div class="text-gray-700 font-bold text-xl">Tổng giá:</div>
                                <div class="text-gray-700 font-bold text-xl text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(invoiceData.totalPrice)}</div>
                            </div>
                        </div>
                    `);

                    $("#view-invoice-modal").removeClass("hidden");

                    // Clear all information for a new transaction
                    $('#selected-products').empty();
                    $('#product-table-body').empty();
                    $('#user-details').empty();
                    $('#total-price').text(formatCurrency(0));
                    currentUser = null;
                } else {
                    alert("Không thể tải chi tiết hóa đơn");
                }
            },
            error: function (error) {
                console.error("Không thể tải chi tiết hóa đơn", error);
                alert("Không thể tải chi tiết hóa đơn !!!");
            },
        });
    }

    // Sự kiện đóng modal khi bấm nút Đóng
    $(document).on('click', '.close-view-invoice-modal-btn', function () {
        $("#view-invoice-modal").addClass("hidden");

        // Clear all information for a new transaction
        $('#selected-products').empty();
        $('#product-table-body').empty();
        $('#user-details').empty();
        $('#total-price').text(formatCurrency(0));
        currentUser = null;
    });

});
