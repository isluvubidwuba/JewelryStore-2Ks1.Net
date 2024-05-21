$(document).ready(function () {
  fetchPromotions();
  loadComponents();
  setupModalToggles();
  fetchVouchers();
  submitForm();
});

function fetchPromotions() {
  const linkPromotion = "http://localhost:8080/promotion";
  $.ajax({
    url: `${linkPromotion}/list`, // API endpoint to get promotions
    method: "GET",
    success: function (response) {
      // Clear existing promotions
      $("#promotion-container").empty();

      // Check if response data is available
      if (response && response.data) {
        response.data.forEach(function (promotion) {
          const statusText = promotion.status
            ? "Đang hoạt động"
            : "Không hoạt động";
          const statusColor = promotion.status
            ? "text-green-500"
            : "text-red-500";

          const promotionCard = `
            <div class="max-w-xs h-67 flex flex-col justify-between bg-white dark:bg-gray-800 rounded-lg border border-gray-400 mb-6 py-5 px-4 mx-4">
              <div>
                <h4 class="focus:outline-none text-gray-800 dark:text-gray-100 font-bold mb-3">
                  ${promotion.name}
                </h4>
                <p class="focus:outline-none text-gray-800 dark:text-gray-100 text-sm">
                  Giá trị: ${promotion.value}%
                </p>
                <img src="${linkPromotion}/files/${promotion.image}" alt="${promotion.name}" class="w-full h-auto mt-3 rounded">
              </div>
              <div>
                <div class="flex items-center justify-between text-gray-800">
                  <p class="focus:outline-none text-sm dark:text-gray-100 ${statusColor}">
                    ${statusText}
                  </p>
                  <div class="w-8 h-8 rounded-full bg-gray-800 text-white flex items-center justify-center">
                    <img src="https://tuk-cdn.s3.amazonaws.com/can-uploader/single_card_with_title_and_description-svg1.svg" alt="icon" />
                  </div>
                </div>
              </div>
            </div>
          `;
          $("#promotion-container").append(promotionCard);
        });
      }
    },
    error: function (error) {
      console.error("Error fetching promotions:", error);
    },
  });
}

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
      if (status == "error") {
        console.error(
          "Error loading the component:",
          xhr.status,
          xhr.statusText
        );
      }
    });
  });
}

function fetchVouchers() {
  $.ajax({
    url: "http://localhost:8080/voucher/list",
    method: "GET",
    success: function (response) {
      // Clear existing options in the dropdown
      $("#idVoucherType").empty();

      // Add default option
      $("#idVoucherType").append("<option selected>Select category</option>");

      // Check if response data is available
      if (response && response.data) {
        // Loop through each voucher and append to the dropdown
        response.data.forEach(function (voucher) {
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

function setupModalToggles() {
  $(document).on("click", "#modalToggle", function () {
    $("#crud-modal").removeClass("hidden");
  });

  $(document).on("click", "#modalClose", function () {
    $("#crud-modal").addClass("hidden");
  });
}

function submitForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();
    var formData = new FormData($("#form-insert")[0]); // Sử dụng id của biểu mẫu
    $.ajax({
      url: "http://localhost:8080/promotion/create",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        console.log(response);
      },
      error: function (xhr, status, error) {
        console.log(xhr.responseText);
      },
    });
  });
}

$(document).ready(function () {
  $("#form-insert").submit(submitForm); // Gán hàm submitForm() cho sự kiện submit của biểu mẫu
});
