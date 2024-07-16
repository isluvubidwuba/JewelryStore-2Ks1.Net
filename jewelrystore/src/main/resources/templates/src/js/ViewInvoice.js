import UserService from "./userService.js";

const userService = new UserService();

$(document).ready(function () {
  $("#submitInvoice").click(function () {
    var invoiceInput = $("#invoiceInput").val();
    if (invoiceInput) {
      getInvoiceData(invoiceInput);
    } else {
      showNotification("Please enter the invoice code !!!", "error");
    }
  });
});

function getInvoiceData(invoice) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/invoice/view-invoice`,
      "POST",
      $.param({ invoice: invoice })
    )
    .then((response) => {
      if (response.status === "OK") {
        populateInvoice(response.data);
        $("#invoiceContent").removeClass("hidden"); // Hiển thị div sau khi có dữ liệu
      } else if (response.status === "BAD_REQUEST") {
        showNotification(response.desc, "error"); // Hiển thị thông báo lỗi khi không tìm thấy hóa đơn
      } else {
        showNotification("Unable to get invoice data !!!", "error");
      }
    })
    .catch((xhr) => {
      if (xhr.status === 400) {
        var response = JSON.parse(xhr.responseText);
        showNotification(response.desc, "error");
      } else {
        showNotification(
          "An error occurred while calling the API !!!",
          "error"
        );
      }
    });
}
function populateInvoice(data) {
  console.log("ban da chay toi dong nay :");
  const dateUpdate = new Date(data.date);

  const offSetDate = new Date(dateUpdate.getTime() + 7 * 60 * 60 * 1000);
  const formatDate = offSetDate.toISOString().split("T")[0];

  // Lấy giờ và phút
  const hours = offSetDate.getHours().toString().padStart(2, '0');
  const minutes = offSetDate.getMinutes().toString().padStart(2, '0');

  // Kết hợp ngày, giờ và phút
  const formattedDateTime = `${formatDate} ${hours}:${minutes}`;
  console.log("Check ngay  : " + formattedDateTime);

  var content = `
    <div class="text-center mb-8 py-10">
        <div class="flex justify-between items-center ">
            <div class="text-left ">
                                <img class="w-32 h-auto mx-10" src="https://storage.googleapis.com/jewelrystore-2ks1dotnet.appspot.com/User/c3b3e699-1466-48cf-b1ae-1db13264e44e_2024-07-12" alt="Logo" />
            </div>   
            <h1 class="text-2xl font-bold mx-10">2KS1.NET® Jewelry</h1>
        </div>
    </div>

    <div class="flex justify-between mb-4">
        <div class="p-4">
            <p class="text-sm"><strong>Customer Name:</strong> ${data.userInfoDTO.fullName
    }</p>
            <p class="text-sm"><strong>Customer Phone:</strong> ${data.userInfoDTO.phoneNumber
    }</p>
            <p class="text-sm"><strong>Customer Address:</strong> ${data.userInfoDTO.address
    }</p>
        </div>

        
        <h2 class="text-xl font-bold self-end mx-10">INVOICE ${data.invoiceTypeDTO.name.toUpperCase()}</h2>
    </div>

    <table class="w-full mb-8 mt-1">
        <thead>
            <tr class="bg-gray-900 text-white">
                <th class="p-2">PRODUCT</th>
                <th class="p-2">QUANTITY</th>
                <th class="p-2">UNIT PRICE</th>
                <th class="p-2">TOTAL PRICE</th>
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
                    <td class="p-2 border border-zinc-300">${item.quantity}</td>
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
        <div class="p-4">
            <p><strong>Biller:</strong> ${data.employeeDTO.firstName} ${data.employeeDTO.lastName
    }</p>
            <p><strong>Invoice Date:</strong> ${formattedDateTime}</p>
            <p><strong>Payment Method:</strong> ${data.payment.trim()}</p>
        </div>
        <div class="text-right mx-10">
            <p class="font-bold text-xl">Total: ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format(data.totalPrice)}</p>
        </div>
    </div>

    <div class="text-center mb-8">
        <h3 class="font-bold">THANK YOU</h3>
        <p>2KS1NET@gmail.com</p>
        <p>FPT UNIVERSITY HỒ CHÍ MINH CITY</p>
    </div>
  `;
  $("#invoiceContent").html(content);
}
