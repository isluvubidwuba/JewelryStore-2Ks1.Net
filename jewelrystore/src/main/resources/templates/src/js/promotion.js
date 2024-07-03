$(document).ready(function () {
  setupEventListeners();
  fetchPromotions();
  loadComponents2();
  setupModalToggles();
  submitInsertForm();
  submitUpdateForm(); // Call the function to handle update form submission

  // Add search event listener
  $("#keyword").on("input", function () {
    const keyword = $(this).val().toLowerCase();
    fetchPromotions(keyword); // Gọi lại hàm fetchPromotions với từ khóa tìm kiếm
  });
});

const token = localStorage.getItem("token");
let currentPage = 0;
const itemsPerPage = 2; // Số lượng mục trên mỗi trang
let promotions = []; // Lưu trữ danh sách promotions đã tải về

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

// fetch all promotions
function fetchPromotions(keyword = "") {
  const linkPromotion = "http://localhost:8080/promotion";
  const deferred = $.Deferred();

  $.ajax({
    url: linkPromotion,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response && response.data) {
        promotions = response.data;
        if (keyword) {
          const filteredPromotions = promotions.filter((promotion) => {
            const invoiceTypeName = promotion.invoiceTypeDTO
              ? promotion.invoiceTypeDTO.name
              : "";
            return (
              promotion.name.toLowerCase().includes(keyword) ||
              promotion.startDate.toLowerCase().includes(keyword) ||
              promotion.endDate.toLowerCase().includes(keyword) ||
              promotion.promotionType.toLowerCase().includes(keyword) ||
              invoiceTypeName.toLowerCase().includes(keyword)
            );
          });
          renderPromotions(0, filteredPromotions);
          updatePagination(filteredPromotions);
        } else {
          renderPromotions(currentPage, promotions);
          updatePagination(promotions);
        }
        deferred.resolve();
      } else {
        deferred.reject("No data found");
      }
    },
    error: function (error) {
      console.error("Error fetching promotions:", error);
      deferred.reject(error);
    },
  });

  return deferred.promise();
}

// Render promotions theo trang
function renderPromotions(page, promotionsToRender) {
  $("#promotion-container").empty(); // Clear existing promotions
  const start = page * itemsPerPage;
  const end = start + itemsPerPage;
  const promotionsToShow = promotionsToRender.slice(start, end);

  promotionsToShow.forEach(function (promotion) {
    const statusText = promotion.status ? "Đang hoạt động" : "Không hoạt động";
    const statusColor = promotion.status ? "text-green-500" : "text-red-500";

    const promotionCard = `
      <div id="promotion-card-${
        promotion.id
      }" class="bg-white rounded-lg shadow-lg w-full mb-3">
        <div class="p-4">
          <div class="flex space-x-4">
            <div class="w-1/3">
              <img
                src="${promotion.image}"
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
                  <label class="block text-gray-700">Applied for Invoice:</label>
                  <input
                    type="text"
                    value="${promotion.invoiceTypeDTO.name}"
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
                    value="${statusText}"
                    class="w-full outline-none px-3 py-2 promotion-status ${statusColor}"
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
                </button>`
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

  // Cập nhật thông tin số lượng mục
  const entriesInfo = `Showing ${start + 1} to ${Math.min(
    end,
    promotionsToRender.length
  )} of ${promotionsToRender.length} entries`;
  $("#entries-info").text(entriesInfo);
}

function updatePagination(promotionsToRender) {
  let pagination = $(".pagination");
  pagination.empty();

  const totalPages = Math.ceil(promotionsToRender.length / itemsPerPage);

  if (currentPage > 0) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          aria-label="backward"
          role="button"
          class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 p-1 flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 focus:outline-none mr-1 sm:mr-3"
          onclick="changePage(${currentPage - 1})"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z"/>
            <polyline points="15 6 9 12 15 18" />
          </svg>
        </span>
      </li>
    `);
  }

  let startPage = Math.max(0, currentPage - 1);
  let endPage = Math.min(totalPages, currentPage + 2);

  if (currentPage === 0) {
    endPage = Math.min(totalPages, currentPage + 2);
  } else if (currentPage === totalPages - 1) {
    startPage = Math.max(0, totalPages - 3);
  } else {
    startPage = Math.max(0, currentPage - 1);
    endPage = Math.min(totalPages, currentPage + 2);
  }

  for (let i = startPage; i < endPage; i++) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          class="focus:outline-none focus:bg-indigo-700 focus:text-white flex text-indigo-700  ${
            i === currentPage
              ? "bg-gray-400 text-white"
              : "bg-white  hover:bg-indigo-600 hover:text-white"
          } text-base leading-tight font-bold cursor-pointer shadow transition duration-150 ease-in-out mx-2 sm:mx-4 rounded px-3 py-2"
          onclick="changePage(${i})"
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
          onclick="changePage(${currentPage + 1})"
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

function changePage(page) {
  currentPage = page;
  const keyword = $("#keyword").val().toLowerCase();
  const promotionsToRender = keyword
    ? promotions.filter((promotion) => {
        const invoiceTypeName = promotion.invoiceTypeDTO
          ? promotion.invoiceTypeDTO.name
          : "";
        return (
          promotion.name.toLowerCase().includes(keyword) ||
          promotion.startDate.toLowerCase().includes(keyword) ||
          promotion.endDate.toLowerCase().includes(keyword) ||
          promotion.promotionType.toLowerCase().includes(keyword) ||
          invoiceTypeName.toLowerCase().includes(keyword)
        );
      })
    : promotions;
  renderPromotions(page, promotionsToRender);
  updatePagination(promotionsToRender);
}
function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}
// Các hàm khác không thay đổi
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

      // Hiển thị giá trị cho promotionType và  mà không cho chỉnh sửa
      $("#display-promotionType").text(
        capitalizeFirstLetter(promotion.promotionType)
      );
      $("#display-invoiceType").text(promotion.invoiceTypeDTO.name);

      // Hiển thị các nút tương ứng với loại promotionType
      let buttonHtml = "";
      if (promotion.promotionType === "product") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Detail_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
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
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center "
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
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center "
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"

            style="width: 100%"
          >
             View type customers applied
          </button>
        `;
      } else if (promotion.promotionType === "gemstone") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Gemstone_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center "
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"

            style="width: 100%"
          >
             View type gemstone applied
          </button>
        `;
      } else if (promotion.promotionType === "material") {
        buttonHtml = `
          <button
            type="button"
            id="modalToggle_Material_Apply"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center "
            data-promotion-id="${promotion.id}"
            data-promotion-name = "${promotion.name}"

            style="width: 100%"
          >
             View type material applied
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

// Các hàm khác giữ nguyên như trước
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
      showNotification(response.desc, "OK");
      $("#deleteModal").addClass("hidden");

      fetchPromotions(0); // Tải lại danh sách promotion
    },
    error: function (error) {
      $("#deleteModal").addClass("hidden");
      showNotification(
        "An error occurred while deleting the promotion.",
        "Error"
      );
    },
  });
}

// update promotion
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
          showNotification(response.desc, "OK");
          fetchPromotions(0); // Tải lại danh sách promotion
        },
        error: function (xhr, status, error) {
          showNotification(
            "An error occurred while submitting the form.",
            "ERROR"
          );

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

// close modal update
function closeModal() {
  $("#crud-update-modal").removeClass("flex").addClass("hidden");
}

// open close modal
function setupModalToggles() {
  $(document).on("click", "#modalToggle", function () {
    fetchInvoiceType();
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
        if (isNaN(numberValue) || numberValue <= 0 || numberValue > 100) {
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
          showNotification(response.desc, "OK");
          // Fetch promotions và tính toán trang cuối cùng
          fetchPromotions().then(() => {
            const totalPages = Math.ceil(promotions.length / itemsPerPage);
            currentPage = totalPages - 1; // Chuyển đến trang cuối cùng
            renderPromotions(currentPage, promotions); // Render promotions cho trang cuối cùng
            updatePagination(promotions); // Cập nhật phân trang
          });
        },
        error: function (xhr, status, error) {
          showNotification(
            "An error occurred while submitting the form.",
            "Error"
          );
          console.log(xhr.responseText);
        },
      });
    } else {
      if (!allFieldsFilled) {
        alert("You must fill all fields.");
      } else if (!numberFieldValid) {
        alert("Number must be greater than 0 và less than 100.");
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

function fetchInvoiceType() {
  $.ajax({
    url: "http://localhost:8080/invoice-type",
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      let invoiceTypeSelect = $("#invoiceType");
      invoiceTypeSelect.empty();
      invoiceTypeSelect.append(
        '<option value="" selected>Select invoice type</option>'
      );
      if (response.status === "OK") {
        $.each(response.data, function (index, invoiceType) {
          invoiceTypeSelect.append(
            `<option value="${invoiceType.id}">${invoiceType.name}</option>`
          );
        });
      } else {
        alert("Failed to fetch invoice types.");
      }
    },
    error: function (xhr, status, error) {
      alert("An error occurred while loading invoice types.");
      console.log(xhr.responseText);
    },
  });
}
