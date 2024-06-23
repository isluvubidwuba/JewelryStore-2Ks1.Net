$(document).ready(function () {
  $("#submitInvoice").click(function () {
    var invoiceInput = $("#invoiceInput").val();
    if (invoiceInput) {
      getInvoiceData(invoiceInput);
    } else {
      alert("Vui lòng nhập mã hóa đơn.");
    }
  });

});

function getInvoiceData(invoice) {
  const token = localStorage.getItem("token");

  $.ajax({
    url: "http://localhost:8080/invoice/view-invoice",
    type: "POST",
    data: { invoice: invoice },
    dataType: "json",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        populateInvoice(response.data);
        if (response.data.invoiceTypeDTO.id !== 3) {
          // Kiểm tra nếu invoiceTypeId không phải là 3
          $("#createReinvoice").removeClass("hidden");
        } else {
          $("#createReinvoice").addClass("hidden");
        }
      } else {
        alert("Không thể lấy dữ liệu hóa đơn.");
      }
    },
    error: function () {
      alert("Đã xảy ra lỗi khi gọi API.");
    },
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
                <h2 class="text-xl font-bold">HÓA ĐƠN ${data.invoiceTypeDTO.name.toUpperCase()}</h2>
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
                ${data.listOrderInvoiceDetail
      .map(
        (item) => `
                    <tr class="text-center product" data-product-id="${item.productDTO.id
          }" data-barcode="${item.productDTO.barCode}">
                        <td class="p-2 border border-zinc-300">${item.productDTO.name
          }</td>
                        <td class="p-2 border border-zinc-300">
                            <input type="number" class="quantityInput w-full" min="1" max="${item.quantity
          }" value="${item.quantity}">
                        </td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat(
            "vi-VN",
            { style: "currency", currency: "VND" }
          ).format(item.price)}</td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat(
            "vi-VN",
            { style: "currency", currency: "VND" }
          ).format(item.totalPrice)}</td>

                    </tr>
                `
      )
      .join("")}
            </tbody>
        </table>
        <div class="flex justify-between mb-8">
            <div class="border border-zinc-300 p-4">
                <p>Người lập hóa đơn: ${data.employeeDTO.firstName} ${data.employeeDTO.lastName
    }</p>
                <p>Ngày lập hóa đơn: ${data.date}</p>
                <p>Phương thức thanh toán: ${data.payment.trim()}</p>
            </div>
            <div class="text-right">
                <p class="font-bold">Tổng cộng: ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format(data.totalPrice)}</p>
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
  $("#invoiceContent").html(content);
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
                ${data.listOrderInvoiceDetail
      .map(
        (item) => `
                    <tr class="text-center product" data-product-id="${item.productDTO.id
          }" data-barcode="${item.productDTO.barCode}">
                        <td class="p-2 border border-zinc-300">${item.productDTO.name
          }</td>
                        <td class="p-2 border border-zinc-300">
                            <input type="number" class="quantityInput w-full" min="1" max="${item.quantity
          }" value="${item.quantity}">
                        </td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat(
            "vi-VN",
            { style: "currency", currency: "VND" }
          ).format(item.price)}</td>
                        <td class="p-2 border border-zinc-300">${new Intl.NumberFormat(
            "vi-VN",
            { style: "currency", currency: "VND" }
          ).format(item.totalPrice)}</td>

                    </tr>
                `
      )
      .join("")}
            </tbody>
        </table>
        <div class="flex justify-between mb-8">
            <div class="border border-zinc-300 p-4">
                <p>Người lập hóa đơn: ${data.employeeDTO.firstName} ${data.employeeDTO.lastName
    }</p>
                <p>Ngày lập hóa đơn: ${data.date}</p>
                <p>Phương thức thanh toán: ${data.payment.trim()}</p>
            </div>
            <div class="text-right">
                <p class="font-bold">Tổng cộng: ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format(data.totalPrice)}</p>
            </div>
        </div>
    `;
  $("#modalContent").html(content);
  $("#modalContent").data("user-id", data.userInfoDTO.id); // Lưu userId vào modal
}

