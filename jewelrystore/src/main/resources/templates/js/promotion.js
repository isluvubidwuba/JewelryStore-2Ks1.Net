$(document).ready(function () {
  setupEventListeners();
  fetchPromotions(0);
  loadComponents2();
  setupModalToggles();
  submitInsertForm();
  submitUpdateForm(); // Call the function to handle update form submission
});

const token = localStorage.getItem("token");
let currentPage = 0; // Biến toàn cục lưu trữ trang hiện tại

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
  currentPage = page; // Cập nhật trang hiện tại
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
                      <div class="w-full">
                        <label class="block text-gray-700">Promotion for</label>
                        <input
                          type="text"
                          value="${promotion.promotionType}"
                          class="w-full px-3 py-2 outline-none promotion-type"
                          readonly
                        />
                      </div>
                      
                    </div>
                    <div class="mb-4 flex space-x-4">
                      <div class="w-full">
                      <label class="block text-gray-700">Start Date</label>
                      <input
                        type="text"
                        value="${promotion.startDate}"
                        class="w-full px-3 py-2 outline-none promotion-start-date"
                        readonly
                      />
                      </div>
                      <div class="w-full">
                        <label class="block text-gray-700">End Date</label>
                        <input
                          type="text"
                          value="${promotion.endDate}"
                          class="w-full px-3 py-2 outline-none promotion-end-date"
                          readonly
                        />
                      </div>
                      <div class="w-full">
                        <label class="block text-gray-700">Last Modified</label>
                        <input
                          type="text"
                          value="${promotion.lastModified}"
                          class="w-full px-3 py-2 outline-none promotion-last-modified"
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
                      Disable
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

      $("#update-id").val(promotion.id);
      $("#update-name").val(promotion.name);
      $("#update-value").val(promotion.value);
      $("#update-start-date").val(promotion.startDate);
      $("#update-end-date").val(promotion.endDate);
      $("#update-status").val(promotion.status == true ? 1 : 0);

      // Hiển thị giá trị cho promotionType mà không cho chỉnh sửa
      $("#display-promotionType").text(promotion.promotionType);

      // Hiển thị các nút tương ứng với loại promotionType
      let buttonHtml = "";
      if (promotion.promotionType === "product") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Detail_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"

            style="width: 100%"
          >
            View products applied
          </button>
        `;
      } else if (promotion.promotionType === "category") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Category_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"
            style="width: 100%"
          >
            View categories applied
          </button>
        `;
      } else if (promotion.promotionType === "customer") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Customer_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"

            style="width: 100%"
          >
             View type customers applied
          </button>
        `;
      }

      $("#button-container").html(buttonHtml);

      $("#crud-update-modal").removeClass("hidden").addClass("flex");
    },
    error: function (error) {
      console.error("Error fetching promotion:", error);
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
      fetchPromotions(currentPage); // Tải lại danh sách promotion với trang hiện tại
    },
    error: function (error) {
      $("#deleteModal").addClass("hidden");
      console.error("Error deleting promotion:", error);
      alert("An error occurred while deleting the promotion.");
    },
  });
}

//update promotion

function submitUpdateForm() {
  $(document).on("click", "#submit-update", function (event) {
    event.preventDefault();

    let { allFieldsFilled, numberFieldValid, datesValid } = validateForm(
      "#form-update",
      true
    );

    if (allFieldsFilled && numberFieldValid && datesValid) {
      var formData = new FormData($("#form-update")[0]);

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
          clearForm("#form-update");
          $("#crud-update-modal").addClass("hidden");
          alert(response.desc);
          fetchPromotions(currentPage); // Tải lại danh sách promotion với trang hiện tại
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
      } else if (!datesValid) {
        alert("End date must be after start date.");
      }
    }
  });
}

//close modal update
function closeModal() {
  $("#crud-update-modal").removeClass("flex").addClass("hidden");
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

function validateForm(formId, isUpdate = false) {
  let allFieldsFilled = true;
  let numberFieldValid = true;
  let datesValid = true;

  let formElement = $(formId);
  let startDate = formElement.find("input[name='startDate']").val();
  let endDate = formElement.find("input[name='endDate']").val();
  let currentDate = new Date().toISOString().split("T")[0];

  formElement
    .find(
      "input[type='text'], input[type='number'], input[type='date'], textarea, select"
    )
    .each(function () {
      if ($(this).val() === "") {
        allFieldsFilled = false;
        return false;
      }

      if ($(this).attr("type") === "number") {
        let numberValue = parseFloat($(this).val());
        if (isNaN(numberValue) || numberValue <= 0 || numberValue >= 100) {
          numberFieldValid = false;
          return false;
        }
      }

      if ($(this).attr("type") === "date") {
        if (startDate && endDate) {
          if (new Date(startDate) >= new Date(endDate)) {
            datesValid = false;
            return false;
          }
          if (
            !isUpdate &&
            (new Date(startDate) < new Date(currentDate) ||
              new Date(endDate) < new Date(currentDate))
          ) {
            datesValid = false;
            return false;
          }
        }
      }
    });

  return { allFieldsFilled, numberFieldValid, datesValid };
}
// submit form create

function submitInsertForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    let { allFieldsFilled, numberFieldValid, datesValid } =
      validateForm("#form-insert");

    if (allFieldsFilled && numberFieldValid && datesValid) {
      var formData = new FormData($("#form-insert")[0]);

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
          clearForm("#form-insert");
          $("#crud-modal").addClass("hidden");
          alert(response.desc);
          fetchPromotions(currentPage); // Tải lại danh sách promotion với trang hiện tại
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
      } else if (!datesValid) {
        alert(
          "Start date and end date must be greater than the current date and end date must be after start date."
        );
      }
    }
  });
}

function clearForm(formId) {
  $(formId)
    .find(
      "input[type='text'], input[type='number'], input[type='date'], input[type='file'], textarea, select"
    )
    .val("");
  $(formId).find("select").prop("selectedIndex", 0);
}
