$(document).ready(function () {
  fetchProduct();
  setupModalToggles();
  submitForm();
});
var listProduct = [];
var listGemsDetail = null;
// load components

// fetch Product by page
function fetchProduct() {
  const linkProduct = "http://localhost:8080/product/all";
  $.ajax({
    url: linkProduct,
    method: "GET",
    success: function (response) {
      $("#productTableBody").empty(); // Clear existing products

      if (response && response.data) {
        const { content } = response.data;
        listProduct = content;
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
                    <td class="px-6 py-4">${product.materialDTO.name}</td>
                    <td class="px-6 py-4">${
                      product.productCategoryDTO.name
                    }</td>
                    <td class="px-6 py-4">${
                      product.status ? "enable" : "disable"
                    }</td>
                    <td class="px-6 py-4">${product.counterDTO.name}</td>
                    <td class="px-6 py-4">
                    <div class ="relative flex justify-items-center">
                    <button type="button" name="modalToggle_Detail" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" data-id="${
                      product.id
                    }">
                        Detail
                        </button>
                        </div>
                    </td>
                </tr>
              `;
            $("#productTableBody").append(productRow);
          });

          createPagination(data.pages, state.page);
        }

        function createPagination(totalPages, page) {
          if (totalPages == 1) return;
          let liTag = "";
          let active;
          let beforePage = page - 1;
          let afterPage = page + 1;

          if (page > 1) {
            liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${
              page - 1
            }">
                          <span><i class="page rounded-full fas fa-angle-left"></i> Prev</span>
                        </li>`;
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
            if (plength <= 0) {
              continue;
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
            }">
                          <span>Next <i class="page rounded-full"></i></span>
                        </li>`;
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

//open close modal
async function setupModalToggles() {
  toggleModal("#create-modal", "#modalClose_Create", "#modalToggle_Create");
  toggleModal(
    "#detail-modal",
    "#modalClose_Detail",
    'button[name="modalToggle_Detail"]'
  );
  toggleModal(
    "#list-gems-modal",
    "#modalClose_listGems",
    "td>button[name='list_gem_detail']"
  );

  openProductModalDetail();
  const listMaterial = await fetchMaterial();
  listMaterial.forEach(function (item) {
    const option = `<option class="dark:placeholder-gray-400" value="${item.id}" >
    ${item.name}
  </option>`;
    $("#idMaterialC").append(option);
  });

  const listProductCategory = await fetchProductCategory();
  listProductCategory.forEach(function (item) {
    const option = `<option class="dark:placeholder-gray-400" value="${item.id}" >
    ${item.name}
  </option>`;
    $("#idCategoryC").append(option);
  });
}
//submit form create
function submitForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    let allFieldsFilled = true;

    $("#form-insert")
      .find("input[type='text'], input[type='number'], select")
      .each(function () {
        if ($(this).val() === "") {
          allFieldsFilled = false;
          return false; // Break out of the loop if any field is empty
        }
      });

    // If all fields are filled, proceed with AJAX request
    if (allFieldsFilled) {
      let product = {
        name: $("#nameC").val(),
        fee: $("#feeC").val(),
        materialDTO: {
          id: $("#idMaterialC").val(),
        },
        weight: $("#materialWeightC").val(),
        productCategoryDTO: {
          id: $("#idCategoryC").val(),
        },
      };

      $.ajax({
        url: "http://localhost:8080/product/create",
        type: "POST",
        data: JSON.stringify(product),
        processData: false,
        contentType: "application/json; charset=utf-8",
        success: function (response) {
          alert("ok");
          // $("#form-insert")
          //   .find(
          //     "input[type='text'], input[type='number'], input[type='file'], textarea, select"
          //   )
          //   .val(""); // Đặt các trường input, textarea và select thành trống sau khi thành công
          // $("#form-insert").find("select").prop("selectedIndex", 0); // Đặt lại trạng thái của các select
          // $("#crud-modal").addClass("hidden");
          // console.log(response);
          // alert(response.desc);
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

async function fetchProductCategory() {
  const linkProduct = "http://localhost:8080/product/category/all";
  try {
    const response = await $.ajax({
      url: linkProduct,
      method: "GET",
    });
    if (response.status === "OK" && response.data) {
      const { content } = response.data;
      return content;
    } else {
      console.error("Failed to fetch product category");
      return null;
    }
  } catch (error) {
    console.error("Error fetching product category:", error);
    return null;
  }
}

async function fetchMaterial() {
  const linkProduct = "http://localhost:8080/material/all";
  try {
    const response = await $.ajax({
      url: linkProduct,
      method: "GET",
    });
    if (response.status === "OK" && response.data) {
      const { content } = response.data;
      return content;
    } else {
      console.error("Failed to fetch Material");
      return null;
    }
  } catch (error) {
    console.error("Error fetching Material:", error);
    return null;
  }
}

async function fetchCounter() {
  const linkProduct = "http://localhost:8080/counter/all";
  try {
    const response = await $.ajax({
      url: linkProduct,
      method: "GET",
    });
    if (response.status === "OK" && response.data) {
      const { content } = response.data;
      return content;
    } else {
      console.error("Failed to fetch data");
      return null;
    }
  } catch (error) {
    console.error("Error fetching product:", error);
    return null;
  }
}

async function fetchGemStoneOfProduct(productId) {
  const linkProduct = `http://localhost:8080/gemStone/product?id=${productId}`;
  try {
    const response = await $.ajax({
      url: linkProduct,
      method: "GET",
    });
    if (response.status === "OK" && response.data) {
      return response.data;
    } else {
      console.error("Failed to fetch gem stone product data");
      return null;
    }
  } catch (error) {
    console.error("Error fetching gem stone product:", error);
    return null;
  }
}

function toggleModal(idModal, idClose, idOpen) {
  $(document).on("click", idOpen, function () {
    $(idModal).removeClass("hidden").addClass("flex");
  });

  $(document).on("click", idClose, function () {
    $(idModal).addClass("hidden").removeClass("flex");
  });
}

async function detailModal(productId) {
  const product = $.map(listProduct, function (item) {
    if (item.id == productId) {
      return item;
    }
  })[0];
  // Check if the product exists
  if (product) {
    // Populate modal fields with product details
    $("#productCode_detail").text(product.productCode);
    $("#name_detail").text(product.name);
    $("#fee_detail").text(product.fee);
    $("#material_detail").text(product.materialDTO.name);
    $("#status_detail").text(product.status);
    $("#category_detail").text(product.productCategoryDTO.name);
    $("#category_detail").text(product.productCategoryDTO.name);
    JsBarcode("#barcode", product.barCode);
    // Show the modal
  } else {
    alert("Product not found");
  }
  await buildTableGemStone(productId);
}
async function openProductModalDetail() {
  $(document).on(
    "click",
    'button[name="modalToggle_Detail"]',
    async function () {
      // Get the data-id attribute of the clicked button
      const productId = $(this).data("id");

      // Call the detailModal function with the productId
      await detailModal(productId);
    }
  );
}

async function buildTableGemStone(productId) {
  $("#GemStone_detail_table").empty();
  $("#notiBlank").empty();
  let gemList = await fetchGemStoneOfProduct(productId);
  if (gemList.length == 0) {
    $("#notiBlank").text(`There are no gem stones in this product`);
    return;
  }
  let listGemsDetail = await countCategoriesByType(gemList);
  for (const type in listGemsDetail) {
    for (const category in listGemsDetail[type]) {
      const countRow = `
        <tr class="font-bold border-b ">
          <td class="px-6 py-3">
            ${category}
          </td>
          <td class="px-6 py-3">
            ${listGemsDetail[type][category].quantity}
          </td>
          <td class="px-6 py-3">
            ${type}
          </td>
          <td  class="font-bold border-b px-6 py-3">
            <button class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" name="list_gem_detail"  type="button"  data-type="${type}" data-category="${category}">
              Detail
            </button>
          </td>
        </tr>
      `;
      $("#GemStone_detail_table").append(countRow);
    }
  }

  // Attach click event handler to the buttons
  $("button[name='list_gem_detail']").on("click", function () {
    const type = $(this).data("type");
    const category = $(this).data("category");
    const gems = listGemsDetail[type][category].gems;
    displayGemDetails(gems);
  });
}
function displayGemDetails(gems) {
  let gemDetailsHtml = ""; // Initialize the variable

  gems.forEach((gem) => {
    gemDetailsHtml += `<tr class="font-bold border-b">
                        <td class="px-6 py-3">${gem.color}</td>
                        <td class="px-6 py-3">${gem.clarity}</td>
                        <td class="px-6 py-3">${gem.carat}</td>
                        <td class="px-6 py-3">${gem.price}</td>
                        <td class="px-6 py-3">${gem.quantity}</td>
                      </tr>`;
  });

  $("#Gems_detail_table").append(gemDetailsHtml);
}
function countCategoriesByType(gemList) {
  const typeCategoryCount = {};

  gemList.forEach((gem) => {
    const typeName = gem.gemstoneType.name;
    const categoryName = gem.gemstoneCategory.name;

    if (!typeCategoryCount[typeName]) {
      typeCategoryCount[typeName] = {};
    }

    if (!typeCategoryCount[typeName][categoryName]) {
      typeCategoryCount[typeName][categoryName] = {
        quantity: 0,
        gems: [],
      };
    }

    typeCategoryCount[typeName][categoryName].quantity += gem.quantity;
    typeCategoryCount[typeName][categoryName].gems.push(gem);
  });

  return typeCategoryCount;
}
