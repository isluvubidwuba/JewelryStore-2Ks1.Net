$(document).ready(function () {
  setupEventListeners();
  fetchPromotions(0);
  loadComponents();
  fetchVouchersForCreate();
  setupModalToggles();
  submitForm();
  submitUpdateForm(); // Call the function to handle update form submission
});
// load components
function loadComponents() {
  const components = [
    { id: "sidebar-placeholder", url: "components/sidebar.html" },
    { id: "header-placeholder", url: "components/header.html" },
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
            <div id="promotion-card-${promotion.id}" class="max-w-xs h-67 flex flex-col justify-between bg-white dark:bg-gray-800 rounded-lg border border-gray-400 mb-6 py-5 px-4 mx-4">
              <div>
                <h4 class="promotion-name focus:outline-none text-gray-800 dark:text-gray-100 font-bold mb-3">${promotion.name}</h4>
                <p class="promotion-value focus:outline-none text-gray-800 dark:text-gray-100 text-sm">Giá trị: ${promotion.value}%</p>
                <img id="promotion-image" src="${linkPromotion}/files/${promotion.image}" alt="${promotion.name}" class="promotion-image w-full h-auto mt-3 rounded">
              </div>
              <div>
                <div class="flex items-center justify-between text-gray-800">
                  <p class="promotion-status focus:outline-none text-sm dark:text-gray-100 ${statusColor}">${statusText}</p>
                  <div class="promotion-click w-8 h-8 rounded-full bg-gray-800 text-white flex items-center justify-center" data-id="${promotion.id}">
                    <img src="https://tuk-cdn.s3.amazonaws.com/can-uploader/single_card_with_title_and_description-svg1.svg" alt="icon" />
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
    contentType: "application/x-www-form-urlencoded",
    data: { id: promotionId },
    success: function (response) {
      var promotion = response.data;

      $("#update-id").val(promotion.id); // Điền thông tin vào các trường của modal
      $("#update-name").val(promotion.name); // Điền thông tin vào các trường của modal
      $("#update-value").val(promotion.value);
      fetchVouchers(promotion.idVoucherType).then(function () {
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
  $(document).on("click", ".promotion-click", function () {
    var promotionId = $(this).data("id");
    fetchPromotionDetails(promotionId);
  });

  $(document).on("click", "#updateModalClose", function () {
    closeModal();
  });
}
//update promotion

function updatePromotionDetails(promotion) {
  const promotionCard = $(`#promotion-card-${promotion.id}`);

  // Update name
  promotionCard.find(".promotion-name").text(promotion.name);

  // Update value
  promotionCard.find(".promotion-value").text(`Giá trị: ${promotion.value}%`);

  // Update status
  const statusText = promotion.status ? "Đang hoạt động" : "Không hoạt động";
  const statusColor = promotion.status ? "text-green-500" : "text-red-500";
  promotionCard
    .find(".promotion-status")
    .text(statusText)
    .attr(
      "class",
      `focus:outline-none text-sm dark:text-gray-100 ${statusColor}`
    );

  // Update image
  const newImageUrl = `http://localhost:8080/promotion/files/${promotion.image}`;
  promotionCard.find(".promotion-image").attr("src", newImageUrl);
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

    // Flag to track if all fields are filled
    let allFieldsFilled = true;

    // Validate all required fields
    $("#form-update")
      .find(
        "input[type='text'], input[type='number'], input[type='file'], textarea, select"
      )
      .each(function () {
        if ($(this).val() === "") {
          allFieldsFilled = false;
          return false; // Break out of the loop if any field is empty
        }
      });

    // If all fields are filled, proceed with AJAX request
    if (allFieldsFilled) {
      var formData = new FormData($("#form-update")[0]); // Sử dụng id của biểu mẫu

      $.ajax({
        url: "http://localhost:8080/promotion/update",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          $("#form-update")
            .find(
              "input[type='text'], input[type='number'], input[type='file'], textarea, select"
            )
            .val(""); // Đặt các trường input, textarea và select thành trống sau khi thành công
          $("#form-update").find("select").prop("selectedIndex", 0); // Đặt lại trạng thái của các select
          $("#crud-update-modal").addClass("hidden");
          handleUpdatePromotionResponse(response);
          alert(response.desc);
        },
        error: function (xhr, status, error) {
          alert("An error occurred while submitting the form.");
          console.log(xhr.responseText);
        },
      });
    } else {
      alert("You must fill all fields.");
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
function submitForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    // Flag to track if all fields are filled
    let allFieldsFilled = true;

    // Validate all required fields
    $("#form-insert")
      .find(
        "input[type='text'], input[type='number'], input[type='file'], textarea, select"
      )
      .each(function () {
        if ($(this).val() === "") {
          allFieldsFilled = false;
          return false; // Break out of the loop if any field is empty
        }
      });

    // If all fields are filled, proceed with AJAX request
    if (allFieldsFilled) {
      var formData = new FormData($("#form-insert")[0]); // Sử dụng id của biểu mẫu

      $.ajax({
        url: "http://localhost:8080/promotion/create",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          $("#form-insert")
            .find(
              "input[type='text'], input[type='number'], input[type='file'], textarea, select"
            )
            .val(""); // Đặt các trường input, textarea và select thành trống sau khi thành công
          $("#form-insert").find("select").prop("selectedIndex", 0); // Đặt lại trạng thái của các select
          $("#crud-modal").addClass("hidden");
          console.log(response);
          alert(response.desc);
        },
        error: function (xhr, status, error) {
          alert("An error occurred while submitting the form.");
          console.log(xhr.responseText);
        },
      });
    } else {
      alert("You must fill all fields.");
    }
  });
}
