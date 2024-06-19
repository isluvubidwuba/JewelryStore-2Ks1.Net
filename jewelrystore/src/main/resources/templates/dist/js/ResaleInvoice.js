$(document).ready(function () {
    $('#submitInvoice').click(function () {
        var invoiceInput = $('#invoiceInput').val();
        if (invoiceInput) {
            getInvoiceData(invoiceInput);
        } else {
            alert("Vui lòng nhập mã hóa đơn.");
        }
    });

    $('#createReinvoice').click(function () {
        $('#reinvoiceModal').removeClass('hidden');
    });

    $('#cancelReinvoice').click(function () {
        $('#reinvoiceModal').addClass('hidden');
    });

    $('#submitReinvoice').click(function () {
        createReinvoice();
    });

    // Đóng modal chi tiết hóa đơn
    $('.close-view-invoice-modal-btn').click(function () {
        $('#view-invoice-modal').addClass('hidden');
    });
});

function getInvoiceData(invoice) {
    const token = localStorage.getItem("token");

    $.ajax({
        url: 'http://localhost:8080/invoice/view-invoice',
        type: 'POST',
        data: { invoice: invoice },
        dataType: 'json',
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            if (response.status === "OK") {
                populateInvoice(response.data);
                if (response.data.invoiceTypeDTO.id !== 3) { // Kiểm tra nếu invoiceTypeId không phải là 3
                    $('#createReinvoice').removeClass('hidden');
                } else {
                    $('#createReinvoice').addClass('hidden');
                }
            } else {
                alert("Không thể lấy dữ liệu hóa đơn.");
            }
        },
        error: function () {
            alert("Đã xảy ra lỗi khi gọi API.");
        }
    });
}

function populateInvoice(data) {
    var content = `
        <div class="text-center mb-8">
            <div class="flex justify-between items-center">
                <div class="text-left">
                    <img src="https://placehold.co/50x50" alt="Logo" class="inline-block">
                    <h1 class="text-xl font-bold">CÔNG TY 2KS1NET</h1>
                </div>
                <h2 class="text-xl font-bold">HÓA ĐƠN</h2>
            </div>
        </div>
        <div class="mb-8">
            <h3 class="font-bold">${data.userInfoDTO.fullName}</h3>
            <p>Điện thoại Khách hàng: ${data.userInfoDTO.phoneNumber}</p>
            <p>Địa chỉ Khách hàng: ${data.userInfoDTO.address}</p>
        </div>
        <table class="w-full mb-8 border border-zinc-300">
            <thead>
                <tr class="bg-blue-200">
                    <th class="p-2 border border-zinc-300">SẢN PHẨM</th>
                    <th class="p-2 border border-zinc-300">SỐ LƯỢNG</th>
                    <th class="p-2 border border-zinc-300">ĐƠN GIÁ</th>
                    <th class="p-2 border border-zinc-300">THÀNH TIỀN</th>
                </tr>
            </thead>
            <tbody>
                ${data.listOrderInvoiceDetail.map(item => `
                    <tr class="text-center product" data-product-id="${item.productDTO.id}" data-barcode="${item.productDTO.barCode}">
                        <td class="p-2 border border-zinc-300">${item.productDTO.name}</td>
                        <td class="p-2 border border-zinc-300">
                            <input type="number" class="quantityInput w-full" min="1" max="${item.quantity}" value="${item.quantity}">
                        </td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.price)}</td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.totalPrice)}</td>

                    </tr>
                `).join('')}
            </tbody>
        </table>
        <div class="flex justify-between mb-8">
            <div class="border border-zinc-300 p-4">
                <p>Người lập hóa đơn: ${data.employeeDTO.firstName} ${data.employeeDTO.lastName}</p>
                <p>Ngày lập hóa đơn: ${data.date}</p>
                <p>Phương thức thanh toán: ${data.payment.trim()}</p>
            </div>
            <div class="text-right">
                <p class="font-bold">Tổng cộng: ${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.totalPrice)}</p>
            </div>
        </div>
        <div class="text-center mb-8">
            <h3 class="font-bold">XIN CẢM ƠN</h3>
        </div>
        <div class="text-center">
            <p>2KS1NET@gmail.com</p>
            <p>FPT UNIVERSITY HỒ CHÍ MINH CITY</p>
        </div>
    `;
    $('#invoiceContent').html(content);
    populateModal(data);
}

function populateModal(data) {
    var content = `
        <div class="text-center mb-8">
            <div class="flex justify-between items-center">
                <div class="text-left">
                    <img src="https://placehold.co/50x50" alt="Logo" class="inline-block">
                    <h1 class="text-xl font-bold">CÔNG TY 2KS1NET</h1>
                </div>
                <h2 class="text-xl font-bold">HÓA ĐƠN</h2>
            </div>
        </div>
        <div class="mb-8">
            <h3 class="font-bold">${data.userInfoDTO.fullName}</h3>
            <p>Điện thoại Khách hàng: ${data.userInfoDTO.phoneNumber}</p>
            <p>Địa chỉ Khách hàng: ${data.userInfoDTO.address}</p>
        </div>
        <table class="w-full mb-8 border border-zinc-300">
            <thead>
                <tr class="bg-blue-200">
                    <th class="p-2 border border-zinc-300">SẢN PHẨM</th>
                    <th class="p-2 border border-zinc-300">SỐ LƯỢNG</th>
                    <th class="p-2 border border-zinc-300">ĐƠN GIÁ</th>
                    <th class="p-2 border border-zinc-300">THÀNH TIỀN</th>
                </tr>
            </thead>
            <tbody>
                ${data.listOrderInvoiceDetail.map(item => `
                    <tr class="text-center product" data-product-id="${item.productDTO.id}" data-barcode="${item.productDTO.barCode}">
                        <td class="p-2 border border-zinc-300">${item.productDTO.name}</td>
                        <td class="p-2 border border-zinc-300">
                            <input type="number" class="quantityInput w-full" min="1" max="${item.quantity}" value="${item.quantity}">
                        </td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.price)}</td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.totalPrice)}</td>

                    </tr>
                `).join('')}
            </tbody>
        </table>
        <div class="flex justify-between mb-8">
            <div class="border border-zinc-300 p-4">
                <p>Người lập hóa đơn: ${data.employeeDTO.firstName} ${data.employeeDTO.lastName}</p>
                <p>Ngày lập hóa đơn: ${data.date}</p>
                <p>Phương thức thanh toán: ${data.payment.trim()}</p>
            </div>
            <div class="text-right">
                <p class="font-bold">Tổng cộng: ${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.totalPrice)}</p>
            </div>
        </div>
    `;
    $('#modalContent').html(content);
    $('#modalContent').data('user-id', data.userInfoDTO.id); // Lưu userId vào modal
}

function createReinvoice() {
    const token = localStorage.getItem("token");
    const employeeId = localStorage.getItem("userId"); // ID của nhân viên từ token
    var note = $('#noteInput').val();
    var userId = $('#modalContent').data('user-id'); // Lấy userId từ modal

    // Khởi tạo HashMap cho barcode và quantity
    let barcodeQuantityMap = {};
    $('#modalContent .product').each(function () {
        var barcode = $(this).data('barcode');
        var quantity = $(this).find('.quantityInput').val();
        barcodeQuantityMap[barcode] = quantity;
    });

    var newInvoice = {
        barcodeQuantityMap: barcodeQuantityMap,
        invoiceTypeId: 3,
        userId: userId,
        employeeId: employeeId,
        payment: "COD", // Hoặc lấy phương thức thanh toán từ một input nào đó nếu có
        note: note
    };

    console.log(barcodeQuantityMap);

    $.ajax({
        url: 'http://localhost:8080/invoice/create-invoice',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(newInvoice),
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            if (response.status === "OK") {
                alert("Tạo hóa đơn thành công!");
                $('#reinvoiceModal').addClass('hidden');
                viewInvoice(response.data);
            } else {
                alert("Không thể tạo hóa đơn.");
            }
        },
        error: function () {
            alert("Đã xảy ra lỗi khi gọi API.");
        }
    });
}

function viewInvoice(invoiceId) {
    const token = localStorage.getItem("token");

    $.ajax({
        url: `http://localhost:8080/invoice/view-invoice`,
        type: 'POST',
        data: { invoice: invoiceId },
        dataType: 'json',
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            if (response.status === "OK") {
                const data = response.data;
                var content = `
                    <div class="bg-white rounded-lg shadow-lg px-8 py-10 max-w-7xl mx-auto">
                        <div class="flex items-center justify-between mb-8">
                            <div class="flex items-center">
                                <img class="h-8 w-8 mr-2" src="https://tailwindflex.com/public/images/logos/favicon-32x32.png" alt="Logo" />
                                <div class="text-gray-700 font-semibold text-lg">2KS 1NET</div>
                            </div>
                            <div class="text-gray-700 text-right">
                                <div class="font-bold text-xl mb-2">HÓA ĐƠN</div>
                                <div class="text-sm">Date: ${new Date(data.date).toLocaleDateString()}</div>
                                <div class="text-sm">Invoice #: ${data.id}</div>
                            </div>
                        </div>
                        <div class="border-b-2 border-gray-300 pb-8 mb-8">
                            <h2 class="text-2xl font-bold mb-4">Thông Tin Khách Hàng và Nhân Viên</h2>
                            <div class="grid grid-cols-2 gap-4">
                                <div>
                                    <div class="text-gray-700 mb-2"><strong>Khách Hàng:</strong> ${data.userInfoDTO.fullName}</div>
                                    <div class="text-gray-700 mb-2"><strong>ID:</strong> ${data.userInfoDTO.id}</div>
                                </div>
                                <div>
                                    <div class="text-gray-700 mb-2"><strong>Nhân Viên:</strong> ${data.employeeDTO.firstName} ${data.employeeDTO.lastName}</div>
                                    <div class="text-gray-700 mb-2"><strong>ID:</strong> ${data.employeeDTO.id}</div>
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
                                ${data.listOrderInvoiceDetail.map(order => `
                                <tr>
                                    <td class="py-4 text-gray-700">${order.productDTO.name}</td>
                                    <td class="py-4 text-gray-700">${order.productDTO.productCode}</td>
                                    <td class="py-4 text-gray-700">${order.quantity}</td>
                                    <td class="py-4 text-gray-700">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(order.totalPrice)}</td>
                                </tr>
                                `).join('')}
                            </tbody>
                        </table>
                        <div class="grid grid-cols-2 gap-4">
                            <div class="text-gray-700">Tổng giá gốc:</div>
                            <div class="text-gray-700 text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.totalPriceRaw)}</div>
                            <div class="text-gray-700">Giá giảm:</div>
                            <div class="text-gray-700 text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.discountPrice)}</div>
                            <div class="text-gray-700 font-bold text-xl">Tổng giá:</div>
                            <div class="text-gray-700 font-bold text-xl text-right">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.totalPrice)}</div>
                        </div>
                    </div>
                `;
                $('#invoice-details').html(content);
                $('#view-invoice-modal').removeClass('hidden');
            } else {
                alert("Không thể lấy dữ liệu hóa đơn.");
            }
        },
        error: function () {
            alert("Đã xảy ra lỗi khi gọi API.");
        }
    });
}
