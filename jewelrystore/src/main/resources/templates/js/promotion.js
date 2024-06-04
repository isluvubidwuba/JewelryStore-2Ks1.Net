$(document).ready(function () {
  setupEventListeners();
  fetchPromotions(0);
  loadComponents2();
  fetchVouchersForCreate();
  setupModalToggles();
  submitInsertForm();
  submitUpdateForm(); // Call the function to handle update form submission
});
const token = localStorage.getItem("token");

// load components
function loadComponents2() {
  const components = [
    {
      id: "pagination-placeholder",
      url: "components/pagination-promotion.html",
    },
    { id: "modal-placeholder", url: "components/modal-insert-promotion.html" },
  ];

  components.forEach((component) => {
    $("#" + component.id).load(component.url, function (response, status, xhr) {
      if (status === "error") {
        console.error(
          "Error loading the component:",
          xhr.status,
          xhr.statusText
        );
      }
    });
  });
}

// fetch promotion by page
function fetchPromotions(page = 0) {
  const linkPromotion = "http://localhost:8080/promotion";
  $.ajax({
    url: `http://localhost:8080/promotion/getHomePagePromotion?page=${page}`,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      $("#promotion-container").empty(); // Clear existing promotions

      if (response && response.data) {
        // Check if response data is available
        const { promotions, totalPages, currentPage } = response.data;

        promotions.forEach(function (promotion) {
          const statusText = promotion.status
            ? "Đang hoạt động"
            : "Không hoạt động";
          const statusColor = promotion.status
            ? "text-green-500"
            : "text-red-500";

          const promotionCard = `
            <div id="promotion-card-${
              promotion.id
            }" class="bg-white rounded-lg shadow-lg w-full mb-3">
              <div class="p-4">
                <div class="flex space-x-4">
                  <div class="w-1/3">
                    <img
                      src="${linkPromotion}/files/${promotion.image}"
                      alt="${promotion.name}"
                      class="w-auto h-64 object-cover mx-auto promotion-image"
                    />
                  </div>
                  <div class="w-2/3">
                    <input type="hidden" name="id" value="${promotion.id}" />
                    <div class="mb-4 flex space-x-4">
                      <div class="w-full">
                        <label class="block text-gray-700">Name</label>
                        <input
                          type="text"
                          value="${promotion.name}"
                          class="w-full px-3 py-2 outline-none promotion-name"
                          readonly
                        />
                      </div>
                      <div class="w-full">
                        <label class="block text-gray-700">Type Promotion</label>
                        <input
                          type="text"
                          value="${promotion.voucherTypeDTO.type}"
                          class="w-full px-3 py-2 outline-none promotion-type-promotion"
                          readonly
                        />
                      </div>
                    </div>
                    <div class="mb-4 flex space-x-4">
                      <div class="w-full">
                        <label class="block text-gray-700">Value</label>
                        <input
                          type="number"
                          value="${promotion.value}"
                          class="w-full px-3 py-2 outline-none promotion-value"
                          readonly
                        />
                      </div>
                      <div class="w-full">
                        <label class="block">Status</label>
                        <input
                          type="text"
                          value="${
                            promotion.status
                              ? "Đang hoạt động"
                              : "Không hoạt động"
                          }"
                          class="w-full outline-none px-3 py-2 promotion-status ${
                            promotion.status ? "text-green-600" : "text-red-700"
                          }"
                          readonly
                        />
                      </div>
                    </div>
                    <div class="flex justify-end space-x-4 relative">
                    ${
                      promotion.status
                        ? `
                    <button
                      id="bttn-delete-promotion"
                      type="button"
                      data-id="${promotion.id}"
                      class="bg-red-500 text-white px-4 py-2 rounded"
                    >
                      Delete
                    </button>
                  `
                        : ""
                    }
                      <button
                        type="button"
                        class="bg-blue-500 text-white px-4 py-2 rounded promotion-update-btn"
                        data-id="${promotion.id}"
                      >
                        Update
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          `;

          $("#promotion-container").append(promotionCard);
        });

        updatePagination(totalPages, currentPage); // Update pagination
      }
    },
    error: function (error) {
      console.error("Error fetching promotions:", error);
    },
  });
}

function updatePagination(totalPages, currentPage) {
  let pagination = $(".pagination");
  pagination.empty();

  if (currentPage > 0) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          aria-label="backward"
          role="button"
          class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 p-1 flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 focus:outline-none mr-1 sm:mr-3"
          onclick="fetchPromotions(${currentPage - 1})"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z"/>
            <polyline points="15 6 9 12 15 18" />
          </svg>
        </span>
      </li>
    `);
  }

  for (let i = 0; i < totalPages; i++) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          class="focus:outline-none focus:bg-indigo-700 focus:text-white flex text-indigo-700 dark:text-indigo-400 ${
            i === currentPage
              ? "bg-gray-400 text-white"
              : "bg-white dark:bg-gray-700 hover:bg-indigo-600 hover:text-white"
          } ${
      i < currentPage - 1 || i > currentPage + 1 ? "hidden" : " "
    } text-base leading-tight font-bold cursor-pointer shadow transition duration-150 ease-in-out mx-2 sm:mx-4 rounded px-3 py-2"
          onclick="fetchPromotions(${i})"
        >
          ${i + 1}
        </span>
      </li>
    `);
  }

  if (currentPage < totalPages - 1) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          aria-label="forward"
          role="button"
          class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 focus:outline-none flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 p-1 focus:outline-none ml-1 sm:ml-3"
          onclick="fetchPromotions(${currentPage + 1})"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z"/>
            <polyline points="9 6 15 12 9 18" />
          </svg>
        </span>
      </li>
    `);
  }
}

// start update
// get in4 about promotion detal
function fetchPromotionDetails(promotionId) {
  $.ajax({
    url: "http://localhost:8080/promotion/getById",
    type: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    contentType: "application/x-www-form-urlencoded",
    data: { id: promotionId },
    success: function (response) {
      var promotion = response.data;

      $("#update-id").val(promotion.id); // Điền thông tin vào các trường của modal
      $("#update-name").val(promotion.name); // Điền thông tin vào các trường của modal
      $("#update-value").val(promotion.value);
      // Cập nhật giá trị data-promotion-id cho nút Detail
      $("#modalToggle_Detail_Apply").attr("data-promotion-id", promotion.id);
      $("#modalToggle_Detail_Apply").attr(
        "data-promotion-name",
        promotion.name
      );
      // Cập nhật giá trị data-promotion-id cho nút Detail
      $("#modalToggle_Category_Apply").attr(
        "data-voucher-id",
        promotion.voucherTypeDTO.id
      );
      $("#modalToggle_Category_Apply").attr(
        "data-voucher-name",
        promotion.voucherTypeDTO.type
      );

      fetchVouchers(promotion.voucherTypeDTO.id).then(function () {
        // Gọi hàm fetchVouchers và đợi hàm này hoàn tất trước khi tiếp tục xử lý
        $("#update-status").val(promotion.status == true ? 1 : 0);
        // Đặt giá trị cho #update-status sau khi các tùy chọn đã được tải

        $("#crud-update-modal").removeClass("hidden").addClass("flex"); // Hiển thị modal sau khi các thông tin đã được điền
      });
    },
    error: function (error) {
      console.error("Error fetching promotion:", error);
    },
  });
}
//load voucher choiced
function fetchVouchers(selectedVoucherType) {
  return $.ajax({
    url: "http://localhost:8080/voucher/list",
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      $("#update-idVoucherType").empty(); // Clear existing options in the dropdown
      $("#update-idVoucherType").append(
        "<option selected>Select category</option>"
      ); // Add default option

      if (response && response.data) {
        // Check if response data is available
        response.data.forEach(function (voucher) {
          // Loop through each voucher and append to the dropdown
          const option = `<option value="${voucher.id}">${voucher.type}</option>`;
          $("#update-idVoucherType").append(option);
        });

        if (selectedVoucherType) {
          $("#update-idVoucherType").val(selectedVoucherType); // Set the selected voucher type if provided
        }
      }
    },
    error: function (error) {
      console.error("Error fetching vouchers:", error);
    },
  });
}

//open modal
function setupEventListeners() {
  // Open update promotion modal
  $(document).on("click", ".promotion-update-btn", function () {
    const promotionId = $(this).data("id");
    fetchPromotionDetails(promotionId);
  });

  $(document).on("click", "#updateModalClose", function () {
    closeModal();
  });

  // Show/hide apply-for options on hover
  $(document).on(
    "mouseenter",
    ".apply-for-btn, .apply-for-options",
    function () {
      $(this).closest(".relative").find(".apply-for-options").show();
    }
  );

  $(document).on(
    "mouseleave",
    ".apply-for-btn, .apply-for-options",
    function () {
      $(this).closest(".relative").find(".apply-for-options").hide();
    }
  );

  // Sự kiện click cho nút Delete
  $(document).on("click", "#bttn-delete-promotion", function () {
    const promotionId = $(this).data("id");
    $("#confirmDelete").data("promotion-id", promotionId);
    $("#deleteModal").removeClass("hidden");
  });

  // Sự kiện click cho nút Cancel trong modal
  $(document).on("click", "#cancelDelete, #closeDelete", function () {
    $("#deleteModal").addClass("hidden");
  });

  // Sự kiện click cho nút Confirm Delete trong modal
  $(document).on("click", "#confirmDelete", function () {
    const promotionId = $(this).data("promotion-id");
    deletePromotion(promotionId);
  });
}
function deletePromotion(promotionId) {
  $.ajax({
    url: `http://localhost:8080/promotion/delete/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      alert(response.desc); // Hiển thị thông báo trả về từ API
      $("#deleteModal").addClass("hidden");
      fetchPromotions(0); // Tải lại danh sách promotion
    },
    error: function (error) {
      $("#deleteModal").addClass("hidden");
      console.error("Error deleting promotion:", error);
      alert("An error occurred while deleting the promotion.");
    },
  });
}
//update promotion

function updatePromotionDetails(promotion) {
  const promotionCard = $(`#promotion-card-${promotion.id}`);

  // Update name
  promotionCard.find(".promotion-name").val(promotion.name);

  // Update value
  promotionCard.find(".promotion-value").val(promotion.value);
  // Update promotion-type-promotion
  promotionCard
    .find(".promotion-type-promotion")
    .val(promotion.voucherTypeDTO.type);

  // Update status
  const statusText = promotion.status ? "Đang hoạt động" : "Không hoạt động";
  const statusColorClass = promotion.status
    ? "w-full outline-none px-3 py-2 promotion-status text-green-500"
    : "w-full outline-none px-3 py-2 promotion-status text-red-500";
  const promotionStatusElement = promotionCard.find(".promotion-status");

  promotionStatusElement.val(statusText);

  // Remove all existing classes and add the necessary classes
  promotionStatusElement.attr(
    "class",
    `promotion-status focus:outline-none text-sm dark:text-gray-100 ${statusColorClass}`
  );

  // Update image
  const newImageUrl = `http://localhost:8080/promotion/files/${promotion.image}`;
  promotionCard.find(".promotion-image").attr("src", newImageUrl);

  // Update delete button visibility based on status
  const deleteButton = promotionCard.find("#bttn-delete-promotion");
  if (promotion.status) {
    if (deleteButton.length === 0) {
      // If delete button doesn't exist, add it
      const deleteButtonHtml = `
        <button
          id="bttn-delete-promotion"
          type="button"
          data-id="${promotion.id}"
          class="bg-red-500 text-white px-4 py-2 rounded"
        >
          Delete
        </button>
      `;
      promotionCard.find(".relative").prepend(deleteButtonHtml);
    }
  } else {
    deleteButton.remove();
  }
}

//call this function when receive the response after updating the promotion
function handleUpdatePromotionResponse(response) {
  if (response && response.data) {
    updatePromotionDetails(response.data);
  }
}

function submitUpdateForm() {
  $(document).on("click", "#submit-update", function (event) {
    event.preventDefault();

    // Validate the form
    let { allFieldsFilled, numberFieldValid } = validateForm("#form-update");

    // If all fields are filled and number fields are valid, proceed with AJAX request
    if (allFieldsFilled && numberFieldValid) {
      var formData = new FormData($("#form-update")[0]); // Use form ID

      $.ajax({
        url: "http://localhost:8080/promotion/update",
        type: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          clearForm("#form-update"); // Clear the form after success
          $("#crud-update-modal").addClass("hidden");
          alert(response.desc);
          handleUpdatePromotionResponse(response);
        },
        error: function (xhr, status, error) {
          alert("An error occurred while submitting the form.");
          console.log(xhr.responseText);
        },
      });
    } else {
      if (!allFieldsFilled) {
        alert("You must fill all fields.");
      } else if (!numberFieldValid) {
        alert("Number must be greater than 0 and less than 100.");
      }
    }
  });
}

//close modal update
function closeModal() {
  $("#crud-update-modal").removeClass("flex").addClass("hidden");
}

//related create promotion
//load all voucher
function fetchVouchersForCreate() {
  $.ajax({
    url: "http://localhost:8080/voucher/list",
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      $("#idVoucherType").empty(); // Clear existing options in the dropdown
      $("#idVoucherType").append("<option selected>Select category</option>"); // Add default option

      if (response && response.data) {
        // Check if response data is available
        response.data.forEach(function (voucher) {
          // Loop through each voucher and append to the dropdown
          const option = `<option value="${voucher.id}">${voucher.type}</option>`;
          $("#idVoucherType").append(option);
        });
      }
    },
    error: function (error) {
      console.error("Error fetching vouchers:", error);
    },
  });
}
//open close modal
function setupModalToggles() {
  $(document).on("click", "#modalToggle", function () {
    $("#crud-modal").removeClass("hidden");
  });

  $(document).on("click", "#modalClose", function () {
    $("#crud-modal").addClass("hidden");
  });
}
//submit form create
function submitInsertForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    // Validate the form
    let { allFieldsFilled, numberFieldValid } = validateForm("#form-insert");

    // If all fields are filled and number fields are valid, proceed with AJAX request
    if (allFieldsFilled && numberFieldValid) {
      var formData = new FormData($("#form-insert")[0]); // Use form ID

      $.ajax({
        url: "http://localhost:8080/promotion/create",
        type: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          clearForm("#form-insert"); // Clear the form after success
          $("#crud-modal").addClass("hidden");
          alert(response.desc);
          fetchPromotions(); // Fetch and display updated promotions
        },
        error: function (xhr, status, error) {
          alert("An error occurred while submitting the form.");
          console.log(xhr.responseText);
        },
      });
    } else {
      if (!allFieldsFilled) {
        alert("You must fill all fields.");
      } else if (!numberFieldValid) {
        alert("Number must be greater than 0 and less than 100.");
      }
    }
  });
}

function validateForm(formId) {
  let allFieldsFilled = true;
  let numberFieldValid = true;

  // Validate all required fields
  $(formId)
    .find("input[type='text'], input[type='number'], textarea, select")
    .each(function () {
      if ($(this).val() === "") {
        allFieldsFilled = false;
        return false; // Break out of the loop if any field is empty
      }

      // Additional validation for input type number
      if ($(this).attr("type") === "number") {
        let numberValue = parseFloat($(this).val());
        if (isNaN(numberValue) || numberValue <= 0 || numberValue >= 100) {
          numberFieldValid = false;
          return false; // Break out of the loop if number is invalid
        }
      }
    });

  return { allFieldsFilled, numberFieldValid };
}
function clearForm(formId) {
  $(formId)
    .find(
      "input[type='text'], input[type='number'], input[type='file'], textarea, select"
    )
    .val(""); // Clear input fields
  $(formId).find("select").prop("selectedIndex", 0); // Reset select fields
}
