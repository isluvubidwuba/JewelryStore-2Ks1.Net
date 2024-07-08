const apiurl = process.env.API_URL;
var keybuffer = [];

$(document).ready(function () {
  //phần xuất hiện
  let productSoldDiv = $("#product-sold");
  let selectedProductsContainer = $("#selected-products");
  let totalPriceRaw = $("#totalPriceRaw");
  let discountPrice = $("#discountPrice");
  let subtotal = $("#subtotal");
  let token = localStorage.getItem("token");
  let employeeID = localStorage.getItem("userId"); // ID của nhân viên từ token

  let productMap = {};
  let selectedUserId = null;
  let selectedUserName = null;
  let userPromotion = null; // Biến để lưu khuyến mãi của người dùng
  let createdInvoiceId = null; // Biến để lưu ID của hóa đơn vừa được tạo

  //function
  setupInsertModalToggle();

  //==================================== Phần này là scaning barcode để thanh toán ==========================================================
  $(document).on("keypress", press);

  function press(event) {
    if (event.which === 13) {
      addProductByBarcode(keybuffer.join(""));
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
  $("#add-barcode-button").click(function () {
    const barcode = $("#barcode-input").val().trim();
    if (barcode) {
      addProductByBarcode(barcode);
      $("#barcode-input").val("");
    }
  });

  // ============================Phần này thực hiện add customer mới vào system =========================================================
  function setupInsertModalToggle() {
    // Function to display the image preview
    function readURL(input) {
      if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
          $("#insertEmployeeImagePreview").attr("src", e.target.result).show();
        };

        reader.readAsDataURL(input.files[0]); // Convert the file to a Data URL
      } else {
        $("#insertEmployeeImagePreview").attr("src", "#").hide();
      }
    }

    // Event listener for image file input change
    $("#insert-file").change(function (e) {
      readURL(this);
    });

    // Form validation functions
    function validateForm(form) {
      const email = form.find('input[name="email"]').val();
      const phoneNumber = form.find('input[name="phoneNumber"]').val();

      if (!isValidEmail(email)) {
        showNotification("Invalid email address.", "error");
        return false;
      }

      if (!isValidPhoneNumber(phoneNumber)) {
        showNotification(
          "Invalid phone number. It should contain only digits and be between 10 to 12 digits long.",
          "error"
        );

        return false;
      }

      return true;
    }

    function isValidEmail(email) {
      const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      return emailPattern.test(email);
    }

    function isValidPhoneNumber(phoneNumber) {
      const phonePattern = /^\d{10,12}$/;
      return phonePattern.test(phoneNumber);
    }

    // Form submission
    $("#insert-user-form").on("submit", function (e) {
      e.preventDefault();

      var form = $(this);

      if (!validateForm(form)) {
        return;
      }

      var formData = new FormData(form[0]);

      // Log the form data for debugging
      for (var pair of formData.entries()) {
        console.log(pair[0] + ": " + pair[1]);
      }

      $.ajax({
        url: `http://${apiurl}/userinfo/insert`,
        method: "POST",
        processData: false,
        contentType: false,
        data: formData,
        headers: {
          Authorization: `Bearer ${token}`,
        },
        success: function (response) {
          showNotification(response.desc || "User inserted successfully", "OK");
          $("#insertUserModal").addClass("hidden");
          clearInsertForm(); // Clear form fields
          console.log(response.data.id);
          getUserById(response.data.id);
        },
        error: function (jqXHR, textStatus, errorThrown) {
          var response = jqXHR.responseJSON;
          showNotification(
            response.desc || "Error inserting user: " + errorThrown,
            "error"
          );
        },
      });
    });

    // Show modal
    $("#insert-customer-button").click(function () {
      $("#insertUserModal").removeClass("hidden");
    });

    // Hide modal
    $("#close-insert-modal").click(function () {
      $("#insertUserModal").addClass("hidden");
      clearInsertForm(); // Clear form data when the modal is closed
    });
  }

  // Function to clear the form data
  function clearInsertForm() {
    $("#insert-user-form")[0].reset();
    $("#insertEmployeeImagePreview").attr("src", "#").hide();
  }

  //======================== Phần này thực hiện việc add search user bằng Phone và email =======================================================
  // Gắn sự kiện click vào nút clear-contact-button
  $("#clear-contact-button").on("click", clearUserIdInput);
  // Định nghĩa hàm clearUserIdInput
  function clearUserIdInput() {
    $("#search-contact-customer").val("");
    $("#clear-contact-button").hide();
  }

  // Sự kiện khi nhập vào ô input
  $("#search-contact-customer").on("input", function () {
    var inputValue = $(this).val();
    if (inputValue) {
      $("#clear-contact-button").show();
    } else {
      $("#clear-contact-button").hide();
    }
  });

  // Hàm kiểm tra số điện thoại hợp lệ
  function isValidPhone(phone) {
    var phonePattern = /^[0-9]{10}$/;
    return phonePattern.test(phone);
  }

  // Hàm kiểm tra email hợp lệ
  function isValidEmail(email) {
    var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }

  // Sự kiện khi bấm vào nút clear
  $("#clear-contact-button").click(function () {
    $("#search-contact-customer").val("");
    $(this).hide();
  });

  // Sự kiện khi bấm vào nút search
  $("#search-contact-button").click(function () {
    var input = $("#search-contact-customer").val();

    // Kiểm tra input là số điện thoại hay email hợp lệ
    if (!isValidPhone(input) && !isValidEmail(input)) {
      showNotification(
        "Please enter a valid phone number or email address",
        "Error"
      );
      return;
    }

    var phone = input;

    $.ajax({
      url: `http://${apiurl}/userinfo/phonenumberandmailcustomer?phone=${phone}`,
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK" && response.data) {
          getUserById(response.data.id);
        } else {
          showNotification(response.desc, "error");
        }
      },
      error: function (xhr, status, error) {
        if (xhr.responseJSON && xhr.responseJSON.desc) {
          showNotification(xhr.responseJSON.desc, "error");
        } else {
          console.error("Error fetching user info:", error);
          showNotification(
            "An error occurred while searching for a user",
            "error"
          );
        }
      },
    });
  });

  // Phần này thực hiện phần tính toán tạo hoá đơn của vnpay ===================================================================
  $("#confirm-user-selection").click(function () {
    const selectedUserElement = $("#user-table-body tr.selected");
    const userId = selectedUserElement.data("id");
    const userName = selectedUserElement.data("name");
    selectUser(userId, userName);
  });

  $("#create-invoice-button").click(function () {
    if (!checkUserSelection()) {
      showNotification(
        "Vui lòng chọn người dùng trước khi tạo hóa đơn.",
        "error"
      );
      return;
    }
    storeValuesInSession(); // Lưu giá trị vào session trước khi hiển thị modal
    showConfirmModal("COD", null); // Thanh toán tại quầy
  });

  $("#open-payment-modal").click(function () {
    $("#customer-info").text(`Khách hàng: ${selectedUserName}`);
    $("#amount-info").text(`Số tiền: ${subtotal.text()} VND`);
    $("#payment-modal").removeClass("hidden");
  });

  $("#close-payment-modal").click(function () {
    $("#payment-modal").addClass("hidden");
  });

  $("#redirect-to-bank").click(function () {
    let amount = subtotal.text().trim();
    amount = amount.replace(/[.,]/g, ""); // Loại bỏ tất cả các dấu . và ,

    const bankCode = $("#bank-select").val();

    storeValuesInSession(); // Lưu giá trị vào session trước khi hiển thị modal
    showConfirmModal("VNPAY", bankCode, amount); // Hiển thị modal xác nhận trước khi chuyển hướng
  });

  $(".close-view-invoice-modal-btn").click(function () {
    closeViewInvoiceModal();
    $("#view-invoice-modal").addClass("hidden");
    clearAllData(); // Xóa thông tin sau khi xem hóa đơn
    clearUserIdInput(); // Xóa thông tin sau khi xem hóa đơn
  });

  function storeValuesInSession() {
    sessionStorage.setItem("selectedUserId", selectedUserId);
    sessionStorage.setItem("selectedUserName", selectedUserName);
    sessionStorage.setItem("userPromotion", JSON.stringify(userPromotion));
    sessionStorage.setItem("productMap", JSON.stringify(productMap));
    sessionStorage.setItem("employeeID", employeeID);
  }

  function showConfirmModal(paymentMethod, bankCode, amount = null) {
    const userId = sessionStorage.getItem("selectedUserId");
    const userName = sessionStorage.getItem("selectedUserName");
    const userPromotion = JSON.parse(sessionStorage.getItem("userPromotion"));
    const productMap = JSON.parse(sessionStorage.getItem("productMap"));
    const employeeID = sessionStorage.getItem("employeeID");

    console.log("showConfirmModal called with:");
    console.log("userId:", userId);
    console.log("userName:", userName);
    console.log("userPromotion:", userPromotion);
    console.log("productMap:", productMap);
    console.log("employeeID:", employeeID);

    if (!userId || !userName || !productMap || !employeeID) {
      showNotification(
        "Information is incomplete. Please check again !!!",
        "error"
      );

      return;
    }

    let modalContent = `
            <p>Client: ${userName}</p>
            <p>ID Client: ${userId}</p>
            <p>Promotion: ${
              userPromotion
                ? userPromotion.name + " - " + userPromotion.value + "%"
                : "Do not have !!!"
            }</p>
            <p>Employee: ${employeeID}</p>
            <p>Total number of products: ${Object.keys(productMap).length}</p>
        `;

    $("#confirm-modal-content").html(modalContent);
    $("#confirm-modal").removeClass("hidden");

    $("#confirm-modal-yes")
      .off("click")
      .on("click", function () {
        $("#confirm-modal").addClass("hidden");
        if (paymentMethod === "COD") {
          createInvoice("COD", null);
        } else if (paymentMethod === "VNPAY") {
          initiatePayment(amount, bankCode);
        }
      });

    $("#confirm-modal-no")
      .off("click")
      .on("click", function () {
        $("#confirm-modal").addClass("hidden");
      });
  }

  // ==============================Phần này thực hiện add sản phẩm vào trước khi thanh toán ======================================================
  CheckClearBarcodeInput();

  function CheckClearBarcodeInput() {
    $("#barcode-input").on("input", function () {
      if ($(this).val().length > 0) {
        $("#clear-barcode-input").removeClass("hidden");
      } else {
        $("#clear-barcode-input").addClass("hidden");
      }
    });
    $("#clear-barcode-input").click(function () {
      clearBarcodeInput();
    });
  }

  function clearBarcodeInput() {
    $("#barcode-input").val("");
    $("#clear-barcode-input").addClass("hidden");
  }

  function addProductByBarcode(barcode) {
    if (productMap[barcode]) {
      const newQuantity = productMap[barcode].quantity + 1;
      if (newQuantity <= productMap[barcode].inventory) {
        updateProductQuantity(barcode, newQuantity);
      } else {
        showNotification(
          "Quantity exceeds inventory quantity. Available: " +
            productMap[barcode].inventory,
          "error"
        );
      }
      return;
    }

    $.ajax({
      url: `http://${apiurl}/invoice/create-detail`,
      method: "POST",
      data: JSON.stringify({
        barcode: barcode,
        quantity: 1,
        invoiceTypeId: 1,
      }),
      contentType: "application/json",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK") {
          const productData = response.data;

          productMap[barcode] = {
            product: productData.productDTO,
            quantity: 1,
            totalPrice: productData.totalPrice, // Giá tổng ban đầu từ API
            inventory: productData.productDTO.inventoryDTO.quantity,
          };

          renderProductCard(barcode);
          renderSidebarProduct(barcode);
          updateTotalPrice();
          // Hiển thị thông báo thành công
          showNotification("Added product successfully", "OK");
        } else {
          // Hiển thị thông báo lỗi từ phản hồi của API
          showNotification(
            response.desc || "An error occurred, please try again !!!",
            "error"
          );
        }
      },
      error: function (error) {
        const response = error.responseJSON;
        console.log("Error fetching product data:", error);
        // Hiển thị thông báo lỗi nếu yêu cầu AJAX gặp lỗi
        showNotification(response.desc || "Product Out Of Stock", "error");
      },
    });
  }

  function renderProductCard(barcode) {
    const productData = productMap[barcode];
    const productCard = $(`
        <div id="product-${barcode}" class="product-card border p-4 mb-4 rounded-md shadow-md grid grid-cols-12 gap-4">
            <div class="col-span-4">
                <img src="${productData.product.imgPath}" alt="${
      productData.product.name
    }" class="w-full h-auto rounded-md">
            </div>
            <div class="col-span-8">
                <h3 class="text-xl font-semibold mb-2">${
                  productData.product.name
                }</h3>
                <p class="text-sm text-gray-600 mb-1"><strong>Product code: </strong> ${
                  productData.product.productCode
                }</p>
                <p class="text-sm text-gray-600 mb-1"><strong>Material: </strong> ${
                  productData.product.materialDTO.name
                }</p>
                <p class="text-sm text-gray-600 mb-1"><strong>Category: </strong> ${
                  productData.product.productCategoryDTO.name
                }</p>
                <p class="text-sm text-gray-600 mb-1"><strong>Barcode: </strong> ${
                  productData.product.barCode
                }</p>
                <p class="text-sm text-gray-600 mb-1"><strong>Toltal price: </strong> ${new Intl.NumberFormat(
                  "vi-VN",
                  { style: "currency", currency: "VND" }
                ).format(productData.totalPrice)}</p>
                <p class="text-sm text-gray-600 mb-1"><strong>Quantity: </strong> <span id="quantity-${barcode}">${
      productData.quantity
    }</span></p>
            </div>
        </div>
    `);
    productSoldDiv.append(productCard);
  }

  function renderSidebarProduct(barcode) {
    const productData = productMap[barcode];
    const productRow = $(`
            <tr id="sidebar-product-${barcode}" data-id="${barcode}">
                <td class="px-4 py-2">${productData.product.name}</td>
                <td class="px-4 py-2">${productData.product.productCode}</td>
                <td class="px-4 py-2 total-price">${new Intl.NumberFormat(
                  "vi-VN",
                  { style: "currency", currency: "VND" }
                ).format(productData.totalPrice)}</td>
                <td class="px-4 py-2">
                    <input type="number" id="sidebar-quantity-${barcode}" class="quantity-input border p-1" value="${
      productData.quantity
    }" min="1" max="${productData.inventory}">
                </td>
                <td class="px-4 py-2">
                    <button class="remove-product-btn bg-red-500 text-white p-1" data-barcode="${barcode}">Delete</button>
                </td>
            </tr>
        `);
    selectedProductsContainer.append(productRow);

    $(`#sidebar-quantity-${barcode}`).on("change", function () {
      const newQuantity = parseInt($(this).val());
      if (newQuantity > 0 && newQuantity <= productData.inventory) {
        updateProductQuantity(barcode, newQuantity);
      } else {
        showNotification(
          "Quantity exceeds inventory quantity. Avaiable: " +
            productData.inventory,
          "error"
        );
        $(this).val(productData.quantity);
      }
    });

    $(`.remove-product-btn[data-barcode="${barcode}"]`).click(function () {
      removeProduct(barcode);
    });
  }

  // ===========================================Phần này là tính tiền cho sản phẩm =========================================================

  function updateProductQuantity(barcode, newQuantity) {
    const productData = productMap[barcode];

    productData.totalPrice =
      (productData.totalPrice / productData.quantity) * newQuantity; // Tính lại giá tổng dựa trên số lượng mới
    productData.quantity = newQuantity;

    $(`#quantity-${barcode}`).text(newQuantity);
    $(`#sidebar-quantity-${barcode}`).val(newQuantity);
    $(`#sidebar-product-${barcode} .total-price`).text(
      new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(productData.totalPrice)
    );
    $(`#total-price-${barcode}`).text(
      new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(productData.totalPrice)
    );

    updateTotalPrice();

    if (newQuantity === 0) {
      removeProduct(barcode);
    }
  }

  function removeProduct(barcode) {
    delete productMap[barcode];
    $(`#product-${barcode}`).remove();
    $(`#sidebar-product-${barcode}`).remove();
    showNotification("Removed product.", "OK");
    updateTotalPrice();
  }

  function updateTotalPrice() {
    let totalPriceBeforeDiscount = 0;
    let discountTotal = 0;

    for (const barcode in productMap) {
      totalPriceBeforeDiscount += productMap[barcode].totalPrice;
    }

    if (selectedUserId && userPromotion) {
      discountTotal = (totalPriceBeforeDiscount * userPromotion.value) / 100;
    }

    const subtotalPrice = totalPriceBeforeDiscount - discountTotal;

    totalPriceRaw.text(
      new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(totalPriceBeforeDiscount)
    );
    discountPrice.text(
      new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(discountTotal)
    );
    subtotal.text(
      new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(subtotalPrice)
    );
  }

  $("#add-user-button").click(function () {
    const userId = $("#user-id-input").val().trim();
    if (userId) {
      getUserById(userId);
      clearUserIdInput();
    } else {
      showNotification("Please enter user ID !!!", "error");
    }
  });

  //========================== Phần này khi lấy được thông tin của user id có thể được tạo hoặc được tìm ===============================
  function getUserById(userId) {
    $.ajax({
      url: `http://${apiurl}/userinfo/getcustomer/${userId}`,
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (data) {
        const user = data; // Không cần data.data nếu data đã là đối tượng người dùng
        if (user) {
          $("#selected-user-info").removeClass("hidden");
          $("#user-details").html(`
            <p><strong>Name: </strong> ${user.fullName}</p>
            <p><strong>ID: </strong> ${user.id}</p>
            <p><strong>Phone: </strong> ${user.phoneNumber.trim()}</p>
            <p><strong>Email: </strong> ${user.email}</p>
          `);
          selectedUserId = user.id; // Gán giá trị cho biến selectedUserId
          selectedUserName = user.fullName; // Gán giá trị cho biến selectedUserName
          sessionStorage.setItem("selectedUserId", user.id); // Lưu ID người dùng vào sessionStorage
          sessionStorage.setItem("selectedUserName", user.fullName); // Lưu tên người dùng vào sessionStorage
          fetchUserPromotions(user.id); // Gọi hàm để lấy khuyến mãi của người dùng
        } else {
          showNotification("Unable to load user data !!!", "error");
        }
      },
      error: function (error) {
        console.error("Error loading user data !!!", error); // Log lỗi nếu có
        showNotification("Error loading user data !!!", "error");
      },
    });
  }

  //=====================================Khi lấy được user id thì đi tìm promotion cho user đó =========================================
  function fetchUserPromotions(userId) {
    $.ajax({
      url: `http://${apiurl}/promotion/by-user`,
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: { userId: userId }, // Truyền ID người dùng vào đây
      success: function (response) {
        if (response.status === "OK") {
          userPromotion = response.data;
          const promotionDetails = $("#promotion-details");
          promotionDetails.empty(); // Xóa dữ liệu cũ

          if (userPromotion) {
            const promotionInfo = `
                            <div class="p-2 border-b border-gray-200">
                                <p><strong>Name: </strong> ${userPromotion.name}</p>
                                <p><strong>Value: </strong> ${userPromotion.value}%</p>
                                <p><strong>Start day: </strong> ${userPromotion.startDate}</p>
                                <p><strong>End date: </strong> ${userPromotion.endDate}</p>
                            </div>
                        `;
            promotionDetails.append(promotionInfo);
          }

          updateTotalPrice(); // Cập nhật giá trị sau khi có khuyến mãi
        } else {
          showNotification("Can Not Load Information User", "error");
        }
      },
      error: function (error) {
        console.error("Error While Loading Information User:", error);
        showNotification("Error While Loading Information User!", "error");
      },
    });
  }

  //===============Phần này quan trọng nhất về việc lấy các biến đã lưu của sản phẩm employee và user để gửi về tạo hoá đơn ==========================
  function createInvoice(paymentMethod, note) {
    if (!selectedUserId) {
      showNotification(
        "Please select a user before creating an invoice !!!",
        "error"
      );
      return;
    }

    if (Object.keys(productMap).length === 0) {
      showNotification(
        "There are no products in the invoice. Please add products before creating an invoice !!!",
        "error"
      );

      return;
    }

    const invoiceDetails = {
      barcodeQuantityMap: {},
      invoiceTypeId: 1,
      userId: selectedUserId,
      employeeId: employeeID,
      payment: paymentMethod,
      note: note,
    };

    for (const barcode in productMap) {
      invoiceDetails.barcodeQuantityMap[barcode] =
        productMap[barcode].quantity.toString();
    }

    console.log("Sending invoice details:", invoiceDetails);

    $.ajax({
      url: `http://${apiurl}/invoice/create-invoice`,
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify(invoiceDetails),
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK") {
          showNotification(
            "The invoice has been created successfully !!!",
            "OK"
          );

          createdInvoiceId = response.data; // Lưu ID của hóa đơn vừa tạo
          viewInvoice(createdInvoiceId); // Gọi hàm để hiển thị hóa đơn
        } else {
          showNotification(
            "Unable to create invoice. Please try again !!!",
            "error"
          );
        }
      },
      error: function (error) {
        console.error("Error when creating invoice: ", error);
        showNotification("Error when creating invoice !!!", "error");
      },
    });
  }

  function clearAllData() {
    // Xóa thông tin từ sessionStorage
    sessionStorage.removeItem("selectedUserId");
    sessionStorage.removeItem("selectedUserName");
    sessionStorage.removeItem("userPromotion");
    sessionStorage.removeItem("productMap");

    // Reset các biến
    productMap = {};
    selectedUserId = null;
    selectedUserName = null;
    userPromotion = null;

    // Xóa các thông tin trên giao diện
    $("#selected-user-info").addClass("hidden");
    $("#user-details").empty();
    $("#promotion-details").empty();
    $("#selected-products").empty();
    $("#product-sold").empty(); // Xóa tất cả các phần tử con của #product-sold
    updateTotalPrice();
  }

  // ================================Sau khi tạo hoá đơn thành công thì sẽ hiện ra hoá đơn cho khách hàng ============================================
  function viewInvoice(invoiceId) {
    $.ajax({
      url: `http://${apiurl}/invoice/view-invoice`,
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
          invoiceDetails.data("invoice-id", invoiceData.id);
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
                                    <div class="text-gray-700 mb-2"><strong>Phone number: </strong> ${
                                      userInfo.phoneNumber
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
                                    <th class="text-gray-700 font-bold uppercase py-2">Product</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Product Code</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Quantity</th>
                                    <th class="text-gray-700 font-bold uppercase py-2">Total Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${orderDetails
                                  .map(
                                    (order) => `
                                <tr>
                                    <td class="py-4 text-gray-700">${
                                      order.productDTO.name
                                    }</td>
                                    <td class="py-4 text-gray-700">${
                                      order.productDTO.productCode
                                    }</td>
                                    <td class="py-4 text-gray-700">${
                                      order.quantity
                                    }</td>
                                    <td class="py-4 text-gray-700">${new Intl.NumberFormat(
                                      "vi-VN",
                                      { style: "currency", currency: "VND" }
                                    ).format(order.totalPrice)}</td>
                                </tr>
                                `
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
                            <div class="text-gray-700 font-bold text-xl">Total price: </div>
                            <div class="text-gray-700 font-bold text-xl text-right">${new Intl.NumberFormat(
                              "vi-VN",
                              { style: "currency", currency: "VND" }
                            ).format(invoiceData.totalPrice)}</div>
                        </div>
                        
                    </div>
                    `);

          $("#view-invoice-modal").removeClass("hidden");
        } else {
          showNotification("Unable to load invoice details", "error");
        }
      },
      error: function (error) {
        console.error("Unable to load invoice details", error);
        showNotification("Unable to load invoice details !!!", "error");
      },
    });
  }

  function closeViewInvoiceModal() {
    $("#view-invoice-modal").addClass("hidden");
    clearAllData(); // Xóa thông tin sau khi xem hóa đơn

    // Xóa các phần tử sản phẩm khỏi giao diện, nhưng giữ lại thanh thêm sản phẩm
    $("#product-sold").children(":not(:first)").remove();
    $("#selected-products").empty();
  }

  // ==============Phần này xử lý về việc nếu như không hàng muốn thay đổi hay lỗi của employee gì đó thì sẽ cancel cho hoá đơn vừa được tạo===============
  // Open cancel invoice modal
  $("#cancel-invoice-btn").on("click", function () {
    $("#view-invoice-modal").addClass("hidden");
    $("#cancel-invoice-modal").removeClass("hidden");
  });

  // Close cancel invoice modal
  $(".close-cancel-invoice-modal-btn").on("click", function () {
    $("#cancel-invoice-modal").addClass("hidden");
    $("#view-invoice-modal").removeClass("hidden");
  });

  // Confirm cancel
  $("#confirm-cancel-btn").on("click", function () {
    const invoiceId = $("#invoice-details").data("invoice-id");
    console.log(
      "Check thong tin gui ve back end cua cancel invoice : " + invoiceId
    );
    const cancelNote = $("#cancel-note").val();

    $.ajax({
      url: `http://${apiurl}/invoice/cancel`,
      method: "POST",
      data: {
        invoiceId: invoiceId,
        note: cancelNote,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK") {
          showNotification("Invoice cancelled successfully", "OK");

          $("#cancel-invoice-modal").addClass("hidden");
          clearAllData(); // Xóa thông tin sau khi xem hóa đơn
        } else {
          showNotification("Unable to cancel invoice", "Error");
        }
      },
      error: function (error) {
        console.error("Unable to cancel invoice", error);
        showNotification("Unable to cancel invoice", "Error");
      },
    });
  });

  //============================= Phần này xử lý việc chuyển sang trang vnpay để thanh toán hoá đơn=================================
  function initiatePayment(amount, bankCode) {
    if (!selectedUserId) {
      showNotification(
        "Please select a user before creating an invoice !!!",
        "error"
      );
      return;
    }

    if (Object.keys(productMap).length === 0) {
      showNotification(
        "There are no products in the invoice. Please add products before creating an invoice !!!",
        "error"
      );

      return;
    }

    amount = parseInt(amount.replace(/[.,\s₫]/g, ""), 10);
    console.log("Initiating payment with:");
    console.log("amount:", amount);
    console.log("bankCode:", bankCode);

    $.ajax({
      url: `http://${apiurl}/payment/vn-pay?amount=${amount}&bankCode=${bankCode}`,
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (
          response.code === 200 &&
          response.data &&
          response.data.paymentUrl
        ) {
          window.location.href = response.data.paymentUrl;
        } else {
          showNotification(
            "Can error occurred while initiating payment. Please try again !!!",
            "error"
          );
        }
      },
      error: function (error) {
        console.error("Error initiating payment:", error);
        showNotification("Error initiating payment.", "error");
      },
    });
  }

  //============================Phần này quan trọng !! khi thanh toán từ vnpay thành công thì vnpay sẽ trả về trang này cùng url có  paymentSuccess
  function handleVnPayCallback() {
    const urlParams = new URLSearchParams(window.location.search);
    const vnpResponseCode = urlParams.get("vnp_ResponseCode");
    console.log("Checking vnpResponseCode: " + vnpResponseCode);

    if (vnpResponseCode !== null) {
      fetch(
        "http://localhost:8080/payment/vn-pay-callback?vnp_ResponseCode=" +
          vnpResponseCode
      )
        .then((response) => response.json())
        .then((data) => {
          const message = data.desc;
          const status = data.status;

          if (status === "OK") {
            showNotification(message, "OK");
            // Lấy các thông tin đã lưu trữ từ sessionStorage
            selectedUserId = sessionStorage.getItem("selectedUserId");
            selectedUserName = sessionStorage.getItem("selectedUserName");
            userPromotion = JSON.parse(sessionStorage.getItem("userPromotion"));
            productMap = JSON.parse(sessionStorage.getItem("productMap"));
            employeeID = sessionStorage.getItem("employeeID");

            // Tạo hóa đơn với thông tin thanh toán qua VNPAY và sử dụng các biến đã khai báo
            createInvoice("VNPAY", null);

            // Xóa các thông tin đã lưu trữ sau khi xử lý
            sessionStorage.removeItem("selectedUserId");
            sessionStorage.removeItem("selectedUserName");
            sessionStorage.removeItem("userPromotion");
            sessionStorage.removeItem("productMap");
            sessionStorage.removeItem("employeeID");
          } else {
            showNotification(message, "Error");
          }
          // Xóa các tham số URL sau khi xử lý
          console.log("Check việc đã xoá các tham số trên URL từ VNPAY trả về");
          const url = new URL(window.location);
          url.search = ""; // Xóa tất cả các tham số
          window.history.replaceState({}, document.title, url);
          console.log("URL parameters cleared");
        })
        .catch((error) => console.error("Error:", error));
    }
  }

  // Thêm hàm kiểm tra user đã chọn chưa
  function checkUserSelection() {
    return selectedUserId !== null;
  }

  $("#reset-order-button").click(function () {
    resetOrder();
  });

  function resetOrder() {
    // Xóa thông tin từ sessionStorage
    sessionStorage.removeItem("selectedUserId");
    sessionStorage.removeItem("selectedUserName");
    sessionStorage.removeItem("userPromotion");
    sessionStorage.removeItem("productMap");

    // Reset các biến
    productMap = {};
    selectedUserId = null;
    selectedUserName = null;
    userPromotion = null;

    // Xóa các thông tin trên giao diện
    $("#selected-user-info").addClass("hidden");
    $("#user-details").empty();
    $("#promotion-details").empty();
    $("#selected-products").empty();
    $("#product-sold").empty();
    updateTotalPrice();
    clearUserIdInput();
    clearBarcodeInput();
    showNotification("Clear All Information Successfully", "OK");
  }

  // Xử lý callback từ VNPAY khi trang được tải
  handleVnPayCallback();

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
