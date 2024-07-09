import UserService from "./userService.js";

const userService = new UserService();

$(document).ready(function () {
  let currentUser = null; // Biến lưu thông tin người dùng hiện tại
  //Function
  initializeClearButton();

  // Function to initialize the clear button functionality for specified input and clear button
  function initializeClearButton() {
    // Show or hide the clear input button based on the input value
    $("#invoiceid-input").on("input", function () {
      if ($(this).val().length > 0) {
        $("#clear-input").removeClass("hidden");
      } else {
        $("#clear-input").addClass("hidden");
      }
    });

    // Clear input field when "X" is clicked
    $("#clear-input").click(function () {
      $("#invoiceid-input").val("").trigger("input"); // Clear input and trigger input event to hide the clear button
    });
  }

  function clearInputInvoiceID() {
    $("#invoiceid-input").val("");
    $("#clear-input").addClass("hidden");
  }

  $("#add-invoiceid-button").click(function () {
    var invoiceId = $("#invoiceid-input").val();
    if (invoiceId) {
      var formData = new FormData();
      formData.append("invoice", invoiceId);
      userService.sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/invoice/view-invoice-resale`,
        "POST",
        function (response) {
          if (response.status === "OK") {
            if (currentUser === null) {
              currentUser = response.data.userInfoDTO;
              updateUserInfo(currentUser);
              updateInvoiceDetails(response.data, invoiceId);
              showNotification(response.desc, "OK");
            } else if (currentUser.id === response.data.userInfoDTO.id) {
              updateInvoiceDetails(response.data, invoiceId);
            } else {
              showNotification(
                "Different users. Please check again !!!",
                "error"
              );
            }
          }
        },
        function (error) {
          console.error("Error when getting invoice: ", error);
          var errorMessage =
            error.responseJSON && error.responseJSON.desc
              ? error.responseJSON.desc
              : "Unknown error occurred";
          showNotification(errorMessage, "error");
        },
        formData
      );
    } else {
      showNotification("Please enter Invoice ID", "error");
    }
  });

  function updateInvoiceDetails(invoice, invoiceId) {
    var productTableBody = $("#product-table-body");
    productTableBody.empty();

    $.each(invoice.listOrderInvoiceDetail, function (index, detail) {
      var row =
        '<tr class="text-center">' +
        '<td class="px-4 py-2">' +
        detail.id +
        "</td>" +
        '<td class="px-4 py-2">' +
        detail.productDTO.productCode +
        "</td>" +
        '<td class="px-4 py-2">' +
        detail.productDTO.name +
        "</td>" +
        '<td class="px-4 py-2">' +
        formatCurrency(detail.price) +
        "</td>" +
        '<td class="px-4 py-2">' +
        '<button class="add-product-button bg-green-500 hover:bg-green-700 text-white p-2" data-barcode="' +
        detail.productDTO.barCode +
        '" data-available-return-quantity="' +
        detail.availableReturnQuantity +
        '" data-price="' +
        detail.price +
        '" data-detail-id="' +
        detail.id +
        '">Add</button>' +
        "</td>" +
        "</tr>";
      productTableBody.append(row);
    });

    $(".add-product-button").click(function () {
      var barcode = $(this).data("barcode");
      var availableReturnQuantity = $(this).data("available-return-quantity");
      var price = $(this).data("price");
      var detailId = $(this).data("detail-id");
      var quantity = 1; // Số lượng mặc định

      var existingProductRow = $("#selected-products").find(
        'tr[data-barcode="' + barcode + '"][data-detail-id="' + detailId + '"]'
      );
      if (existingProductRow.length > 0) {
        var currentQuantity = parseInt(
          existingProductRow.find(".product-quantity-input").val()
        );
        var totalAvailableQuantity = parseInt(
          existingProductRow.find(".product-available-quantity").text()
        );
        if (currentQuantity + quantity <= totalAvailableQuantity) {
          currentQuantity += quantity;
          existingProductRow
            .find(".product-quantity-input")
            .val(currentQuantity);

          var unitPrice = parseFloat(
            existingProductRow
              .find(".product-price-unit")
              .text()
              .replace(/[^\d.-]/g, "")
          );
          var newPrice = unitPrice * currentQuantity;
          existingProductRow
            .find(".product-price")
            .text(formatCurrency(newPrice));

          updateTotalPrice();
        } else {
          showNotification("Invalid quantity. Please check again !!!", "error");
        }
      } else {
        if (quantity <= availableReturnQuantity) {
          var requestData = {
            invoiceTypeId: 3,
            quantity: quantity,
            barcode: barcode,
          };
          userService.sendAjaxWithAuthen(
            `http://${userService.getApiUrl()}/api/invoice/create-detail`,
            "POST",
            function (response) {
              if (response.status === "OK") {
                addProductToSidebar(
                  response.data,
                  availableReturnQuantity,
                  detailId
                );
              } else {
                showNotification("Error when creating invoice !!!", "error");
              }
            },
            function (error) {
              console.error("Error when creating invoice: ", error);
            },
            requestData
          );
        } else {
          showNotification("Invalid quantity. Please check again !!!", "error");
        }
      }
    });
  }

  function updateUserInfo(userInfo) {
    var userInfoDiv = $("#user-details");
    userInfoDiv.empty();
    userInfoDiv.append(
      "<p><strong>Full Name:</strong> " + userInfo.fullName + "</p>"
    );
    userInfoDiv.append(
      "<p><strong>Phone Number:</strong> " +
        userInfo.phoneNumber.trim() +
        "</p>"
    );
    userInfoDiv.append("<p><strong>Email:</strong> " + userInfo.email + "</p>");
    $("#selected-user-info").removeClass("hidden");
  }

  function addProductToSidebar(data, availableReturnQuantity, detailId) {
    // Hiển thị các chi tiết sản phẩm trong sidebar
    var selectedProductsContainer = $("#selected-products");
    var row =
      '<tr data-barcode="' +
      data.productDTO.barCode +
      '" data-detail-id="' +
      detailId +
      '">' +
      '<td class="px-4 py-2">' +
      detailId +
      "</td>" +
      '<td class="px-4 py-2">' +
      data.productDTO.name +
      "</td>" +
      '<td class="px-4 py-2">' +
      data.productDTO.productCode +
      "</td>" +
      '<td class="px-4 py-2 product-price">' +
      formatCurrency(data.price * data.quantity) +
      "</td>" +
      '<td class="px-4 py-2">' +
      '<input type="number" class="product-quantity-input border p-1" value="' +
      data.quantity +
      '" min="1" max="' +
      availableReturnQuantity +
      '">' +
      "</td>" +
      '<td class="px-4 py-2">' +
      '<button class="remove-product-button bg-red-500 hover:bg-red-700 text-white p-2">Xóa</button>' +
      "</td>" +
      '<td class="px-4 py-2 product-price-unit" style="display:none">' +
      data.price +
      "</td>" +
      '<td class="px-4 py-2 product-available-quantity" style="display:none">' +
      availableReturnQuantity +
      "</td>" +
      "</tr>";
    selectedProductsContainer.append(row);
    updateTotalPrice();

    $(".product-quantity-input")
      .off("change")
      .on("change", function () {
        var newQuantity = parseInt($(this).val());
        var maxQuantity = parseInt($(this).attr("max"));
        if (newQuantity > maxQuantity) {
          showNotification("Invalid quantity. Please check again !!!", "error");
          $(this).val(maxQuantity);
          newQuantity = maxQuantity;
        } else if (newQuantity <= 0) {
          $(this).closest("tr").remove();
        }
        updateProductPriceAndTotal($(this));
      });

    $(".remove-product-button")
      .off("click")
      .on("click", function () {
        $(this).closest("tr").remove();
        showNotification("Removed product", "error");

        updateTotalPrice();
      });
  }

  function updateProductPriceAndTotal(quantityInput) {
    var row = quantityInput.closest("tr");
    var unitPrice = parseFloat(
      row
        .find(".product-price-unit")
        .text()
        .replace(/[^\d.-]/g, "")
    );
    var newQuantity = parseInt(quantityInput.val());
    var newPrice = unitPrice * newQuantity;
    row.find(".product-price").text(formatCurrency(newPrice));
    updateTotalPrice();
  }

  function updateTotalPrice() {
    var totalPrice = 0;
    $("#selected-products tr").each(function () {
      var quantity = parseInt($(this).find(".product-quantity-input").val());
      var unitPrice = parseFloat(
        $(this)
          .find(".product-price-unit")
          .text()
          .replace(/[^\d.-]/g, "")
      );
      totalPrice += quantity * unitPrice;
    });
    $("#total-price").text(formatCurrency(totalPrice));
  }

  function formatCurrency(amount) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  }

  // Code to show confirmation modal when clicking "Create Invoice" button
  $("#checkout-button").click(function () {
    var selectedProducts = $("#selected-products tr");

    if (selectedProducts.length === 0) {
      showNotification(
        "Please select at least one product before creating an invoice !!!",
        "error"
      );
      return;
    }

    // Populate modal content with relevant information
    var confirmModalContent = $("#confirm-modal-content");
    confirmModalContent.empty();

    confirmModalContent.append(`
        <div class="bg-white rounded-lg shadow-lg px-8 py-10 max-w-7xl mx-auto w-full">
            <div class="flex items-center justify-between mb-8">
                <div class="flex items-center">
                    <img class="h-8 w-8 mr-2" src="https://tailwindflex.com/public/images/logos/favicon-32x32.png" alt="Logo" />
                    <div class="text-gray-700 font-semibold text-lg">2KS 1NET</div>
                </div>
                <div class="text-gray-700 text-right">
                    <div class="font-bold text-xl mb-2">CONFIRMATION</div>
                </div>
            </div>
            <div class="border-b-2 border-gray-300 pb-8 mb-8">
                <h2 class="text-2xl font-bold mb-4">Selected Products</h2>
                <table class="w-full text-left mb-8">
                    <thead>
                        <tr>
                            <th class="text-gray-700 font-bold uppercase py-2">Product</th>
                            <th class="text-gray-700 font-bold uppercase py-2">Quantity</th>
                        </tr>
                    </thead>
                    <tbody>
                    ${selectedProducts
                      .map((index, product) => {
                        var productName = $(product)
                          .find("td:nth-child(2)")
                          .text();
                        var productQuantity = $(product)
                          .find(".product-quantity-input")
                          .val();
                        return `
                        <tr>
                            <td class="py-4 text-gray-700">${productName}</td>
                            <td class="py-4 text-gray-700">${productQuantity}</td>
                        </tr>`;
                      })
                      .get()
                      .join("")}
                    </tbody>
                </table>
            </div>
           
        </div>
    `);

    // Show the confirmation modal
    $("#confirm-modal").removeClass("hidden");
  });

  // Handle confirmation button click
  $("#confirm-modal-yes").click(function () {
    // Proceed with creating the invoice
    var selectedProducts = $("#selected-products tr");
    var barcodeQuantityMap = {};

    selectedProducts.each(function () {
      var detailId = $(this).data("detail-id");
      var quantity = parseInt($(this).find(".product-quantity-input").val());
      barcodeQuantityMap[detailId] = quantity;
    });

    var requestData = {
      barcodeQuantityMap: barcodeQuantityMap,
      invoiceTypeId: 3,
      userId: currentUser.id,
      employeeId: userService.getUserId(),
      payment: "COD",
      note: "Ghi chú buyback",
    };
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/invoice/buyback`,
      "POST",
      function (response) {
        if (response.status === "OK") {
          showNotification(response.desc, "OK");
          viewInvoice(response.data); // Thêm dòng này để gọi hàm viewInvoice
        } else {
          showNotification("Error when buyingback !!!", "error");
        }
      },
      function (error) {
        console.error("Error when buyingback: ", error);
      },
      requestData
    );

    // Hide the confirmation modal
    $("#confirm-modal").addClass("hidden");
  });

  // Handle cancellation button click
  $("#confirm-modal-no").click(function () {
    // Hide the confirmation modal
    $("#confirm-modal").addClass("hidden");
  });

  // Hàm viewInvoice để hiển thị chi tiết hóa đơn
  function viewInvoice(invoiceId) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/invoice/view-invoice`,
      "POST",
      function (response) {
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
                                    <div class="font-bold text-xl mb-2">INVOICE</div>
                                    <div class="text-sm">Date: ${new Date(
                                      invoiceData.date
                                    ).toLocaleDateString()}</div>
                                    <div class="text-sm">Invoice #: ${
                                      invoiceData.id
                                    }</div>
                                </div>
                            </div>
                            <div class="border-b-2 border-gray-300 pb-8 mb-8">
                                <h2 class="text-2xl font-bold mb-4">Customer and Employee Information</h2>
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <div class="text-gray-700 mb-2"><strong>Customer: </strong> ${
                                          userInfo.fullName
                                        }</div>
                                        <div class="text-gray-700 mb-2"><strong>ID: </strong> ${
                                          userInfo.id
                                        }</div>
                                    </div>
                                    <div>
                                        <div class="text-gray-700 mb-2"><strong>STAFF: </strong> ${
                                          employeeInfo.firstName
                                        } ${employeeInfo.lastName}</div>
                                        <div class="text-gray-700 mb-2"><strong>ID: </strong> ${
                                          employeeInfo.id
                                        }</div>
                                    </div>
                                </div>
                            </div>
                            <table class="w-full text-left mb-8">
                                <thead>
                                    <tr>
                                        <th class="text-gray-700 font-bold uppercase py-2">Product code</th>
                                        <th class="text-gray-700 font-bold uppercase py-2">Product name</th>
                                        <th class="text-gray-700 font-bold uppercase py-2">Quantity</th>
                                        <th class="text-gray-700 font-bold uppercase py-2">Total price</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${orderDetails
                                      .map(
                                        (order) => `
                                    <tr>
                                        <td class="py-4 text-gray-700">${
                                          order.productDTO.productCode
                                        }</td>
                                        <td class="py-4 text-gray-700">${
                                          order.productDTO.name
                                        }</td>
                                        <td class="py-4 text-gray-700">${
                                          order.quantity
                                        }</td>
                                        <td class="py-4 text-gray-700">${new Intl.NumberFormat(
                                          "vi-VN",
                                          { style: "currency", currency: "VND" }
                                        ).format(order.totalPrice)}</td>
                                    </tr>`
                                      )
                                      .join("")}
                                </tbody>
                            </table>
                            <div class="grid grid-cols-2 gap-4">
                                <div class="text-gray-700">Total original price: </div>
                                <div class="text-gray-700 text-right">${new Intl.NumberFormat(
                                  "vi-VN",
                                  { style: "currency", currency: "VND" }
                                ).format(invoiceData.totalPriceRaw)}</div>
                                <div class="text-gray-700">Reduced price: </div>
                                <div class="text-gray-700 text-right">${new Intl.NumberFormat(
                                  "vi-VN",
                                  { style: "currency", currency: "VND" }
                                ).format(invoiceData.discountPrice)}</div>
                                <div class="text-gray-700 font-bold text-xl">Total price:</div>
                                <div class="text-gray-700 font-bold text-xl text-right">${new Intl.NumberFormat(
                                  "vi-VN",
                                  { style: "currency", currency: "VND" }
                                ).format(invoiceData.totalPrice)}</div>
                            </div>
                            <div class="flex items-center justify-center font-playwrite text-2xl text-center">
                            THANK YOU
                           </div>
                        </div>
                    `);

          $("#view-invoice-modal").removeClass("hidden");

          // Clear all information for a new transaction
          $("#selected-products").empty();
          $("#product-table-body").empty();
          $("#user-details").empty();
          $("#total-price").text(formatCurrency(0));
          currentUser = null;
        } else {
          showNotification("Unable to load invoice details", "error");
        }
      },
      function (error) {
        console.error("Unable to load invoice details", error);
        showNotification("Unable to load invoice details !!!", "error");
      },
      $.param({ invoice: invoiceId })
    );
  }

  // Sự kiện đóng modal khi bấm nút Đóng
  $(document).on("click", ".close-view-invoice-modal-btn", function () {
    $("#view-invoice-modal").addClass("hidden");

    // Clear all information for a new transaction
    $("#selected-products").empty();
    $("#product-table-body").empty();
    $("#user-details").empty();
    $("#total-price").text(formatCurrency(0));
    clearInputInvoiceID();
    currentUser = null;
  });

  $("#clear-infomation-button").click(function () {
    // Clear all information for a new transaction
    $("#selected-products").empty();
    $("#product-table-body").empty();
    $("#user-details").empty();
    $("#total-price").text(formatCurrency(0));
    currentUser = null;
    clearInputInvoiceID();
    showNotification("All information has been cleared", "OK");
  });

  // Hàm in hóa đơn
  function printInvoice() {
    var printContents = document.getElementById("invoice-details").innerHTML;
    document.body.innerHTML = printContents;
    window.print();

    setTimeout(function () {
      location.reload();
    }, 100);
  }

  // Gắn sự kiện click cho nút in hóa đơn
  $("#print-invoice-btn").click(function () {
    printInvoice();
  });
});
