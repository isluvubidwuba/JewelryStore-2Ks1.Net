$(document).ready(function () {
  setupEventListeners();
  fetchProduct();
  loadComponents();
  setupModalToggles();
  //submitForm();
  //submitUpdateForm(); // Call the function to handle update form submission
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

// fetch Product by page
function fetchProduct() {
  const linkProduct = "http://localhost:8080/product/all";
  $.ajax({
    url: linkProduct,
    method: "GET",
    success: function (response) {
      $("#productTableBody").empty(); // Clear existing products

      if (response && response.data) {
        console.log(response.data);
        const { content } = response.data;

        var state = {
          querySet: content,
          page: 1,
          rows: 10,
          window: 7,
        };

        function buildTable() {
          $("#productTableBody").empty();

          var data = pagination(state.querySet, state.page, state.rows);
          var myList = data.querySet;

          myList.forEach(function (product) {
            const productRow = `
                <tr class = "font-bold border-b ">
                    <td class=" px-6 py-4">${product.id}</td>
                    <td class="px-6 py-4">${product.name}</td>
                    <td class="px-6 py-4">${
                      product.materialOfProductDTO.materialDTO.name
                    }</td>
                    <td class="px-6 py-4">${
                      product.productCategoryDTO.name
                    }</td>
                    <td class="px-6 py-4">${
                      product.status ? "enable" : "disable"
                    }</td>
                    <td class="px-6 py-4">${product.counterDTO.name}</td>
                    <td class="px-6 py-4">
                    <div class ="relative flex justify-items-center">
                    <button id="detailProduct" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110">
                        Detail
                        </button>
                        <button id="editProduct" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110">
                        Edit
                        </button></div>
                    </td>
                </tr>
              `;
            $("#productTableBody").append(productRow);
          });

          createPagination(data.pages, state.page);
        }

        function createPagination(totalPages, page) {
          let liTag = "";
          let active;
          let beforePage = page - 1;
          let afterPage = page + 1;

          if (page > 1) {
            liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${
              page - 1
            }"><span><i class="page rounded-full fas fa-angle-left"></i> Prev</span></li>`;
          }

          if (page > 2) {
            liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="1"><span>1</span></li>`;
            if (page > 3) {
              liTag += `<li class="relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer"><span>...</span></li>`;
            }
          }

          if (page == totalPages) {
            beforePage = beforePage - 2;
          } else if (page == totalPages - 1) {
            beforePage = beforePage - 1;
          }

          if (page == 1) {
            afterPage = afterPage + 2;
          } else if (page == 2) {
            afterPage = afterPage + 1;
          }

          for (var plength = beforePage; plength <= afterPage; plength++) {
            if (plength > totalPages) {
              continue;
            }
            if (plength == 0) {
              plength = plength + 1;
            }
            if (page == plength) {
              active = "bg-blue-500 text-white";
            } else {
              active = "bg-white text-gray-700 hover:bg-gray-50";
            }
            liTag += `<li class="page relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium ${active} cursor-pointer" data-page="${plength}"><span>${plength}</span></li>`;
          }

          if (page < totalPages - 1) {
            if (page < totalPages - 2) {
              liTag += `<li class="relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer"><span>...</span></li>`;
            }
            liTag += `<li class="page relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${totalPages}"><span>${totalPages}</span></li>`;
          }

          if (page < totalPages) {
            liTag += `<li class="page relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${
              page + 1
            }"><span>Next <i class="page rounded-full "></i></span></li>`;
          }

          $("#pagination-wrapper").html(liTag);
          $(".page").on("click", function () {
            state.page = Number($(this).data("page"));
            buildTable();
          });
        }

        buildTable();
      }
    },
    error: function (error) {
      console.error("Error fetching product:", error);
    },
  });
}

function pagination(querySet, page, rows) {
  var trimStart = (page - 1) * rows;
  var trimEnd = trimStart + rows;

  var trimmedData = querySet.slice(trimStart, trimEnd);

  var pages = Math.ceil(querySet.length / rows);

  return {
    querySet: trimmedData,
    pages: pages,
  };
}

function loadCategoryProduct() {}
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
  const statusColorClass = promotion.status ? "text-green-500" : "text-red-500";
  const promotionStatusElement = promotionCard.find(".promotion-status");

  promotionStatusElement.text(statusText);

  // Remove all existing classes and add the necessary classes
  promotionStatusElement.attr(
    "class",
    `promotion-status focus:outline-none text-sm dark:text-gray-100 ${statusColorClass}`
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
      .find("input[type='text'], input[type='number'], textarea, select")
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
