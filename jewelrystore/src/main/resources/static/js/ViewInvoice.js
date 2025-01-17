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

  $(".close-cancel-invoice-modal-btn").on("click", function () {
    $("#cancel-invoice-modal").addClass("hidden");
  });

  $("#confirm-cancel-btn").on("click", function () {
    const invoiceId = $("#invoice-details").data("invoice-id");

    const cancelNote = $("#cancel-note").val();

    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/invoice/cancel`,
        "POST",
        $.param({
          invoiceId: invoiceId,
          note: cancelNote,
        })
      )
      .then((response) => {
        if (response.status === "OK") {
          showNotification("Invoice cancelled successfully", "OK");

          $("#cancel-invoice-modal").addClass("hidden");
          $("#invoiceContent").empty();
        }
      })
      .catch((error) => {
        $("#cancel-invoice-modal").addClass("hidden");
        $("#invoiceContent").empty();
        showNotification(error.responseJSON.desc, "Error");
      });
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
      if (xhr.responseJSON) {
        showNotification(xhr.responseJSON.desc, "error");
      } else {
        showNotification(
          "An error occurred while calling the API !!!",
          "error"
        );
      }
    });
}
function populateInvoice(data) {
  const dateUpdate = new Date(data.date).toLocaleString();
  

  let promotionPriceForUser = 0;
  if (data.listPromotionOnInvoice && data.listPromotionOnInvoice.length > 0) {
    promotionPriceForUser = data.listPromotionOnInvoice[0].value;
  }
  console.log("Check gia tien lay tu data : " + promotionPriceForUser);


  var content = `
    <div id="invoice-details" data-invoice-id="${data.id
    }" class="text-center mb-8 py-10">
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
                <th class="p-2">DISCOUNT PRODUCT</th>
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
          ).format(item.price - item.totalPrice)}</td>
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
            <p><strong>Invoice Date:</strong> ${dateUpdate}</p>
            <p><strong>Payment Method:</strong> ${data.payment.trim()}</p>
        </div>
        <div class="text-right mx-10">
            <p class="font-bold text-xl">User Discount(${data.listPromotionOnInvoice[0].value}%): ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format((data.totalPrice * promotionPriceForUser) / 100)}</p>
            <p class="font-bold text-xl">Total Discount: ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format(data.discountPrice)}</p>
            <p class="font-bold text-xl">Total: ${new Intl.NumberFormat(
      "vi-VN",
      { style: "currency", currency: "VND" }
    ).format(data.totalPrice)}</p>
      
        </div>
        
    </div>
<div class="text-right mx-10"><button
            id="cancel-invoice-btn"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-red-500 text-base font-medium text-white hover:bg-red-600  focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm  "
          >
            Cancel
          </button></div>
    <div class="text-center mb-8">
        <h3 class="font-bold">THANK YOU</h3>
        <p>2KS1NET@gmail.com</p>
        <p>FPT UNIVERSITY HỒ CHÍ MINH CITY</p>
    </div>
  `;
  $("#invoiceContent").html(content);
  $("#cancel-invoice-btn").click(function () {
    $("#cancel-invoice-modal").removeClass("hidden");
  });
}
