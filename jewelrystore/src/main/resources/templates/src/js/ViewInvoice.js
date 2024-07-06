const apiurl = process.env.API_URL;
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
  const token = localStorage.getItem("token");

  $.ajax({
    url: `http://${apiurl}/invoice/view-invoice`,
    type: "POST",
    data: { invoice: invoice },
    dataType: "json",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        populateInvoice(response.data);
        $("#invoiceContent").removeClass("hidden"); // Hiển thị div sau khi có dữ liệu
      } else if (response.status === "BAD_REQUEST") {
        showNotification(response.desc, "error"); // Hiển thị thông báo lỗi khi không tìm thấy hóa đơn
      } else {
        showNotification("Unable to get invoice data !!!", "error");
      }
    },
    error: function (xhr) {
      if (xhr.status === 400) {
        var response = JSON.parse(xhr.responseText);
        showNotification(response.desc, "error");
      } else {
        showNotification("An error occurred while calling the API !!!", "error");
      }
    },
  });
}



function populateInvoice(data) {
  var content = `
    <div class="text-center mb-8">
        <div class="flex justify-between items-center">
            <div class="text-left">
                <img src="https://storage.googleapis.com/jewelrystore-2ks1dotnet.appspot.com/User/411b0a45-6851-4765-876d-cfdb19b82b00_2024-07-06" alt="Logo" class="inline-block w-24 h-auto">
            </div>   
            <h1 class="text-2xl font-bold">COMPANY 2KS1NET</h1>
        </div>
    </div>

    <div class="flex justify-between mb-4">
        <div class="border border-zinc-300 p-4">
            <p class="text-sm"><strong>Customer Name:</strong> ${data.userInfoDTO.fullName}</p>
            <p class="text-sm"><strong>Customer Phone:</strong> ${data.userInfoDTO.phoneNumber}</p>
            <p class="text-sm"><strong>Customer Address:</strong> ${data.userInfoDTO.address}</p>
        </div>
        <h2 class="text-xl font-bold self-end">INVOICE ${data.invoiceTypeDTO.name.toUpperCase()}</h2>
    </div>

    <table class="w-full mb-8 border border-zinc-300 mt-1">
        <thead>
            <tr class="bg-gray-900 text-white">
                <th class="p-2">PRODUCT</th>
                <th class="p-2">QUANTITY</th>
                <th class="p-2">UNIT PRICE</th>
                <th class="p-2">TOTAL PRICE</th>
            </tr>
        </thead>
        <tbody>
            ${data.listOrderInvoiceDetail.map(item => `
                <tr class="text-center product" data-product-id="${item.productDTO.id}" data-barcode="${item.productDTO.barCode}">
                    <td class="p-2 border border-zinc-300">${item.productDTO.name}</td>
                    <td class="p-2 border border-zinc-300">${item.quantity}</td>
                    <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.price)}</td>
                    <td class="p-2 border border-zinc-300">${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(item.totalPrice)}</td>
                </tr>
            `).join("")}
        </tbody>
    </table>

    <div class="flex justify-between mb-8">
        <div class="border border-zinc-300 p-4">
            <p><strong>Biller:</strong> ${data.employeeDTO.firstName} ${data.employeeDTO.lastName}</p>
            <p><strong>Invoice Date:</strong> ${data.date}</p>
            <p><strong>Payment Method:</strong> ${data.payment.trim()}</p>
        </div>
        <div class="text-right">
            <p class="font-bold text-xl">Total: ${new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(data.totalPrice)}</p>
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
