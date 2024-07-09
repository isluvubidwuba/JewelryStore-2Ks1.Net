import UserService from "./userService.js";

const userService = new UserService();
let employeeID = userService.getUserId(); // ID của nhân viên từ token
var keybuffer = [];

$(document).ready(function () {
  const selectedProductsTable = $("#selectedProductsTable");
  let totalPrice = 0;
  let supplierId = null;


  $(document).on("keypress", press);

  function press(event) {
    if (event.which === 13) {
      searchProductByBarcode(keybuffer.join(""));
      keybuffer.length = 0;
      return;
    }

    var number = null;
    if (event.which >= 48 && event.which <= 57) {
      // Handle numbers on the main keyboard (0-9)
      number = event.which - 48;
    } else if (event.which >= 96 && event.which <= 105) {
      // Handle numbers on the numpad (0-9)
      number = event.which - 96;
    }

    if (number !== null) {
      keybuffer.push(number);
    }
  }

  $("#searchProductByBarcode").on("click", function () {
    const barcode = $("#barcodeInput").val();
    if (barcode) {
      searchProductByBarcode(barcode);
    }
  });

  $("#searchSupplier").on("click", function () {
    const supplierIdInput = $("#supplierInput").val();

    if (!supplierIdInput) {
      showNotification("Please enter phone number or email", "error");
      return;
    }

    if (isValidPhoneNumber(supplierIdInput) || isValidEmail(supplierIdInput)) {
      searchSupplier(supplierIdInput);
    } else {
      showNotification("Invalid phone number or email", "error");
    }
  });

  $("#createInvoice").on("click", function () {
    createInvoice();
  });

  $("#clear-all-information").on("click", function () {
    clearAllInformation();
    // Thông báo
    showNotification("Clear all information successful", "OK");
  });

  function isValidPhoneNumber(phoneNumber) {
    const phoneRegex = /^[0-9]{10,11}$/; // Số điện thoại chỉ chứa 10-11 chữ số
    return phoneRegex.test(phoneNumber);
  }

  function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Kiểm tra định dạng email
    return emailRegex.test(email);
  }

  function formatCurrency(value) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(value);
  }

  function updateTotalPrice() {
    totalPrice = 0;
    selectedProductsTable.find("tr").each(function () {
      const quantity = $(this).find(".product-quantity").val();
      const price = $(this).find(".product-price").val();
      totalPrice += quantity * price;
    });
    $("#totalPrice").text(formatCurrency(totalPrice));
  }

  function addProductToTable(product) {
    let existingRow = selectedProductsTable.find(
      `tr[data-barcode="${product.barCode}"]`
    );

    if (existingRow.length > 0) {
      let quantityInput = existingRow.find(".product-quantity");
      quantityInput.val(parseInt(quantityInput.val()) + 1);
    } else {
      const row = `
                <tr data-barcode="${product.barCode}">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${product.productCode}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${product.name}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <input type="number" class="product-quantity w-full px-2 py-1 border rounded" value="1" min="0">
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <input type="number" class="product-price w-full px-2 py-1 border rounded" value="${product.fee}" min="0">
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <button type="button" class="remove-product text-red-500">Xóa</button>
                    </td>
                </tr>
            `;
      selectedProductsTable.append(row);
    }

    updateTotalPrice();

    // Gắn sự kiện thay đổi số lượng và giá
    selectedProductsTable
      .find(".product-quantity, .product-price")
      .off("input")
      .on("input", function () {
        updateTotalPrice();
        if ($(this).hasClass("product-quantity") && $(this).val() == 0) {
          $(this).closest("tr").remove();
          updateTotalPrice();
        }
      });

    // Gắn sự kiện xóa sản phẩm
    selectedProductsTable
      .find(".remove-product")
      .off("click")
      .on("click", function () {
        $(this).closest("tr").remove();
        updateTotalPrice();
      });
  }

  function searchProductByBarcode(barcode) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/product/${barcode}`,
      "GET",
      function (response) {
        if (response.status === "OK" && response.data) {
          const product = response.data;
          addProductToTable(product);
          $("#barcodeInput").val("");
        } else {
          showNotification("No products found with this barcode !!!", "error");
        }
      },
      function () {
        showNotification("No products found with this barcode !!!", "error");
      },
      null
    );
  }

  function searchSupplier(supplierIdInput) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/userinfo/phonenumberandmailsupplier?citeria=${supplierIdInput}`,
      "GET",
      function (response) {
        if (response) {
          supplierId = response.data.id;
          displaySupplierInfo(response.data);
          $("#supplierInput").val("");
        } else {
          showNotification("No supplier found with this code !!!", "error");
        }
      },
      function () {
        showNotification("No supplier found with this code !!!", "error");
      },
      null
    );
  }

  function displaySupplierInfo(supplier) {
    $("#supplierInfo").removeClass("hidden");
    $("#supplierName").text(`Tên: ${supplier.fullName}`);
    $("#supplierPhone").text(`Số điện thoại: ${supplier.phoneNumber}`);
    $("#supplierEmail").text(`Email: ${supplier.email}`);
    $("#supplierAddress").text(`Địa chỉ: ${supplier.address}`);
  }

  function createInvoice() {
    var paymentMethod = $("#paymentMethod").val();

    // Kiểm tra xem giá trị có rỗng hay không
    if (paymentMethod === null || paymentMethod === "") {
      showNotification("Payment method has not been selected !!!", "error");
      return;
    }
    if (!employeeID || !supplierId) {
      console.log("employeeID: " + employeeID + "supplierId" + supplierId);
      showNotification(
        "Please select a provider and make sure the employee is logged in !!!",
        "error"
      );

      return;
    }

    let barcodeQuantityMap = {};
    let barcodePriceMap = {};

    selectedProductsTable.find("tr").each(function () {
      const barcode = $(this).data("barcode");
      const quantity = $(this).find(".product-quantity").val();
      const price = $(this).find(".product-price").val();
      barcodeQuantityMap[barcode] = quantity;
      barcodePriceMap[barcode] = parseFloat(price);
    });

    // Kiểm tra xem có sản phẩm nào được chọn không
    if (Object.keys(barcodeQuantityMap).length === 0) {
      showNotification("No products selected", "error");
      return;
    }

    const invoiceRequest = {
      invoiceTypeId: 2,
      userId: supplierId,
      employeeId: employeeID,
      payment: paymentMethod,
      note: $("#notes").val(),
      barcodeQuantityMap: barcodeQuantityMap,
    };

    const importInvoiceRequestWrapper = {
      request: invoiceRequest,
      barcodePriceMap: barcodePriceMap,
    };
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/invoice/create-import`,
      "POST",
      function (response) {
        if (response.status === "OK") {
          showNotification("Invoice created successfully !!!", "OK");
          // Xóa các sản phẩm khỏi bảng và đặt lại tổng giá tiền
          selectedProductsTable.empty();
          clearAllInformation();
          updateTotalPrice();
        } else {
          showNotification(
            "Invoice creation failed: " + response.desc,
            "error"
          );
        }
      },
      function () {
        showNotification(
          "Can error occurred while creating the invoice !!!",
          "error"
        );
      },
      importInvoiceRequestWrapper
    );
  }

  function clearAllInformation() {
    // Xóa tất cả sản phẩm khỏi bảng
    $("#selectedProductsTable").empty();

    // Đặt lại tổng giá tiền
    totalPrice = 0;
    $("#totalPrice").text(formatCurrency(totalPrice));

    // Ẩn thông tin nhà cung cấp
    $("#supplierInfo").addClass("hidden");

    // Xóa nội dung các input
    $("#barcodeInput").val("");
    $("#supplierInput").val("");
    $("#notes").val("");
    $("#paymentMethod").val("");
  }
});
