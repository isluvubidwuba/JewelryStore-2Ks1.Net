import UserService from "./userService.js";

const userService = new UserService();
$(document).ready(function () {
  init();
});

function init() {
  fetchProduct(state.currentServerPage, state.size); // Fetch products on page load
  setupModalToggles();
  setupFormSubmissions();
  setupSearch();
  setUpOnChange();
}
const state = {
  querySet: [],
  page: 1,
  rows: 10,
  totalPagesAtClient: 0,
  currentServerPage: 0,
  actualPageIndexAtClient: 1,
  currentPageAtClient: 0,
  previousPageAtClient: 0,
  size: 50,
};

let listGemsDetail = null;
let timeout = null;

function fetchProduct(page, size) {
  const linkProduct = `http://${userService.getApiUrl()}/api/product/all?page=${page}&size=${size}`;
  userService
    .sendAjaxWithAuthen(linkProduct, "GET", null)
    .then((response) => {
      if (response && response.data) {
        const { content, totalElements } = response.data;
        state.querySet = content; // Replace with new records
        state.totalPagesAtClient = Math.ceil(totalElements / state.rows);
        state.currentServerPage = page;
        buildTable(state, "There are no  product"); // Build table after fetching products
      }
    })
    .catch((error) => {
      showNotification("Error fetching product.", "Error");

      console.error("Error fetching product:", error);
    });
}

function buildTable(state, noti) {
  $("#productTableBody").empty();
  if (!state.querySet || state.querySet.length === 0) {
    $("#notiBlankProduct").text(noti);
    return;
  }
  const data = pagination(state.querySet, state.page, state.rows);
  const myList = data.querySet;

  myList.forEach(function (product) {
    const productRow = createProductRow(product);
    $("#productTableBody").append(productRow);
  });

  createPagination(state.totalPagesAtClient, state.page, data.pages);
}

function createProductRow(product) {
  return `
    <tr class="font-bold border-b " data-id-product="${product.id}" >
      <td class="px-6 py-4">${product.id}</td>
      <td class="px-6 py-4">${product.name}</td>
      <td class="px-6 py-4">${product.materialDTO.name}</td>
      <td class="px-6 py-4">${product.productCategoryDTO.name}</td>
      <td class="px-6 py-4">${product.status ? "Active" : "Inactive"}</td>
      <td class="px-6 py-4">${product.counterDTO.name}</td>
      <td class="px-6 py-4">
        <div class="relative flex justify-items-center">
          <button type="button" name="modalToggle_Detail" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" data-id="${
            product.id
          }">
            Detail
          </button>
        </div>
      </td>
    </tr>
  `;
}

function pagination(querySet, page, rows) {
  const pages = Math.ceil(state.size / rows);
  state.actualPageIndexAtClient = Math.floor((page - 1) / pages) * pages + 1;
  const trimStart = (page - state.actualPageIndexAtClient) * rows;
  const trimEnd = trimStart + rows;

  return {
    querySet: querySet.slice(trimStart, trimEnd),
    pages: pages,
  };
}

function createPagination(totalPages, page, actualTotalPagesAtClient) {
  if (totalPages <= 1) return;
  let liTag = "";
  let active;
  let beforePage = page - 1;
  let afterPage = page + 1;

  if (page > 1) {
    liTag += createPageItem(page - 1, "Prev", "fas fa-angle-left");
  }

  if (page > 2) {
    liTag += createPageItem(1, "1");
    if (page > 3) {
      liTag += createEllipsis();
    }
  }

  adjustPageRange(page, totalPages, beforePage, afterPage);

  for (let plength = beforePage; plength <= afterPage; plength++) {
    if (plength > totalPages || plength <= 0) continue;
    active =
      page == plength
        ? "bg-blue-500 text-white"
        : "bg-white text-gray-700 hover:bg-gray-50";
    liTag += createPageItem(plength, plength, null, active);
  }

  if (page < totalPages - 1) {
    if (page < totalPages - 2) {
      liTag += createEllipsis();
    }
    liTag += createPageItem(totalPages, totalPages);
  }

  if (page < totalPages) {
    liTag += createPageItem(page + 1, "Next", "fas fa-angle-right");
  }

  $("#pagination-wrapper").html(liTag);
  $(".page").on("click", function () {
    state.previousPageAtClient = state.page;
    state.page = Number($(this).data("page"));
    state.currentPageAtClient = state.page;
    const serverPage = Math.floor((state.page - 1) / actualTotalPagesAtClient);
    if (serverPage != state.currentServerPage) {
      fetchProduct(serverPage, state.size); // Fetch new records for every new server page
    } else {
      buildTable(state, "There are no  product");
    }
  });
}

function adjustPageRange(page, totalPages, beforePage, afterPage) {
  if (page == totalPages) {
    beforePage -= 2;
  } else if (page == totalPages - 1) {
    beforePage -= 1;
  }

  if (page == 1) {
    afterPage += 2;
  } else if (page == 2) {
    afterPage += 1;
  }
}

function createPageItem(
  page,
  text,
  icon = null,
  active = "bg-white text-gray-700 hover:bg-gray-50"
) {
  return `
    <li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium ${active} cursor-pointer" data-page="${page}">
      ${
        icon
          ? `<span class="flex items-center"><i class="${icon}"></i> ${text}</span>`
          : `<span>${text}</span>`
      }
    </li>
  `;
}

function createEllipsis() {
  return `<li class="relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer"><span>...</span></li>`;
}

async function setupModalToggles() {
  toggleModal("#create-modal", "#modalClose_Create", "#modalToggle_Create");
  toggleModal(
    "#detail-modal",
    "#modalClose_Detail",
    "button[name='modalToggle_Detail']"
  );
  toggleModal(
    "#createGemStone-modal",
    "#modalClose_CreateGem",
    "button[name='addGemStone']"
  );
  toggleModal(
    "#list-gems-modal",
    "#modalClose_listGems",
    "td>button[name='list_gem_detail']"
  );

  setUpFormUpdateModal();

  await fetchDropdownData("#update-material", fetchMaterial);
  await fetchDropdownData("#update-category", fetchProductCategory);
  openProductModalDetail();

  await fetchDropdownData("#idMaterialC", fetchMaterial);
  await fetchDropdownData("#idCategoryC", fetchProductCategory);

  await fetchDropdownData("#idCategoryGemC", fetchGemStoneCategory);
  await fetchDropdownData("#idTypeC", fetchGemStoneType);

  await fetchDropdownData("#Material", fetchMaterial);
  await fetchDropdownData("#Category", fetchProductCategory);
  await fetchDropdownData("#Counter", fetchCounter);
}
async function fetchCounter() {
  return fetchData2(
    `http://${userService.getApiUrl()}/api/counter/allactivecounter`
  );
}
async function fetchDropdownData(elementId, fetchDataFunc) {
  const dataList = await fetchDataFunc();
  dataList.forEach(function (item) {
    const option = `<option class="dark:placeholder-gray-400" value="${item.id}">${item.name}</option>`;
    $(elementId).append(option);
  });
}

function setUpFormUpdateModal() {
  const detailsMapping = [
    { updateBtn: "#update-name", detail: "#name_detail" },
    { updateBtn: "#update-fee", detail: "#fee_detail" },
    { updateBtn: "#update-weight", detail: "#weight_detail" },
    { updateBtn: "#update-material", detail: "#material_detail" },
    { updateBtn: "#update-status", detail: "#status_detail" },
    { updateBtn: "#update-category", detail: "#category_detail" },
  ];
  detailsMapping.forEach(({ updateBtn, detail }) => {
    setUpClickUpdateModal(
      updateBtn,
      "#cancel-update",
      detail,
      "#buttonSubmitUpdate"
    );
    setUpClickUpdateModal(detail, detail, "#cancel-update");
    setUpClickUpdateModal("#cancel-update", null, "#productImgUpdateBTn");
    setUpClickUpdateModal("#buttonSubmitUpdate", null, "#productImgUpdateBTn");
    setUpClickUpdateModal(
      "#buttonSubmitUpdate",
      "#cancel-update",
      null,
      "#buttonSubmitUpdate"
    );
    setUpClickUpdateModal("#imgDetail", null, "#cancel-update", null);
    setUpClickUpdateModal("#cancel-update", "#cancel-update", null, null);

    $("#cancel-update").on("click", function () {
      $("#updateImagePlaceHolder").empty();
      $("#productImgUpdate").val(null);
    });
  });
  setUpClickUpdateModal("#buttonSubmitUpdate", "#cancel-update");
}

function setUpClickUpdateModal(idModal, idClose, idOpen, idBTN) {
  $(idOpen).on("click", function () {
    $(idModal).removeClass("hidden").addClass("flex");
    $(idModal).focus();
    $(idBTN).removeClass("hidden").addClass("flex");
  });

  $(idClose).on("click", function () {
    $(idModal).addClass("hidden").removeClass("flex");
    $(idBTN).removeClass("hidden").addClass("flex");
  });
}
function toggleModal(idModal, idClose, idOpen) {
  $(document).on("click", idOpen, function () {
    $(idModal).removeClass("hidden").addClass("flex");
  });

  $(document).on("click", idClose, function () {
    $(idModal).addClass("hidden").removeClass("flex");
  });
}

async function openProductModalDetail() {
  $(document).on(
    "click",
    'button[name="modalToggle_Detail"]',
    async function () {
      const productId = $(this).data("id");
      await detailModal(productId);
    }
  );
}

async function detailModal(productId) {
  const product = state.querySet.find((item) => item.id == productId);
  if (product) {
    populateModalFields(product);
    JsBarcode("#barcode", product.barCode);
  }
  await buildTableGemStone(productId);
}

async function buildTableGemStone(productId) {
  $("#GemStone_detail_table").empty();
  $("#notiBlank").empty();
  const gemList = await fetchGemStoneOfProduct(productId);
  if (!gemList || gemList.length === 0) {
    $("#notiBlank").text(`There are no gem stones in this product`);
    return;
  }
  listGemsDetail = countCategoriesByType(gemList); // Update global variable
  Object.entries(listGemsDetail).forEach(([type, categories]) => {
    Object.entries(categories).forEach(([category, { quantity, gems }]) => {
      const countRow = createGemStoneRow(type, category, quantity);
      $("#GemStone_detail_table").append(countRow);
    });
  });

  $("button[name='list_gem_detail']").on("click", function () {
    const type = $(this).data("type");
    const category = $(this).data("category");
    const gems = listGemsDetail[type][category].gems;
    displayGemDetails(gems);
  });
}

function populateModalFields(product) {
  $('button[name="addGemStone"]').attr("data-product-id", product.id);

  $("#productCode_detail").text(product.productCode);
  $("#productCode_detailInput").val(product.productCode);

  $("#productBarcode").val(product.barcode);

  $("#name_detail").text(product.name);
  $("#update-name").val(product.name);

  $("#fee_detail").text(product.fee);
  $("#update-fee").val(product.fee);

  $("#weight_detail").text(product.weight);
  $("#update-weight").val(product.weight);

  $("#material_detail").text(product.materialDTO.name);
  $("#update-material").val(product.materialDTO.id).change();

  $("#status_detail").text(product.status ? "Active" : "Inactive");
  $("#update-status")
    .val(product.status ? "true" : "false")
    .change();

  $("#category_detail").text(product.productCategoryDTO.name);
  $("#update-category").val(product.productCategoryDTO.id).change();

  $("#imgDetail").prop("src", product.imgPath);
  $("#imgDetailInput").val(extractFilename(product.imgPath));

  $("#submit-update").attr("data-product-id", product.id);
}
function extractFilename(url) {
  const parsedUrl = new URL(url);
  const pathSegments = parsedUrl.pathname.split("/");
  return pathSegments.pop();
}

function createGemStoneRow(type, category, quantity) {
  return `
    <tr class="font-bold border-b">
      <td class="px-6 py-3">${category}</td>
      <td class="px-6 py-3">${quantity}</td>
      <td class="px-6 py-3">${type}</td>
      <td class="font-bold border-b px-6 py-3">
        <button class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" name="list_gem_detail" type="button" data-type="${type}" data-category="${category}">
          Detail
        </button>
      </td>
    </tr>
  `;
}

function displayGemDetails(gems) {
  $("#Gems_detail_table").empty();
  gems.forEach((gem) => {
    const gemDetailsHtml = `
      <tr class="font-bold border-b">
        <td class="px-6 py-3">${gem.color}</td>
        <td class="px-6 py-3">${gem.clarity}</td>
        <td class="px-6 py-3">${gem.carat}</td>
        <td class="px-6 py-3">${gem.price}</td>
        <td class="px-6 py-3">${gem.quantity}</td>
      </tr>
    `;
    $("#Gems_detail_table").append(gemDetailsHtml);
  });
}

function countCategoriesByType(gemList) {
  return gemList.reduce((acc, gem) => {
    const { name: typeName } = gem.gemstoneType;
    const { name: categoryName } = gem.gemstoneCategory;
    if (!acc[typeName]) {
      acc[typeName] = {};
    }
    if (!acc[typeName][categoryName]) {
      acc[typeName][categoryName] = { quantity: 0, gems: [] };
    }
    acc[typeName][categoryName].quantity += gem.quantity;
    acc[typeName][categoryName].gems.push(gem);
    return acc;
  }, {});
}

function setupSearch() {
  $("#search-input").on("keyup", function () {
    clearTimeout(timeout);
    const text = this.value.trim();
    if (text === "") {
      fetchProduct(state.currentServerPage, state.size);
    } else {
      timeout = setTimeout(() => {
        searchProducts(text);
      }, 500);
    }
  });
  $("#Category,#Material,#Counter").change(function () {
    searchProducts($("#search-input").val());
  });
  $("#search-input").on("change", function () {
    if (this.value.trim() === "0") {
      fetchProduct(state.currentServerPage, state.size);
    }
  });
}

function searchProducts(query) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/product/search`,
      "POST",
      $.param({
        search: query,
        id_material: $("#Material").val(),
        id_product_category: $("#Category").val(),
        id_counter: $("#Counter").val(),
      })
    )
    .then((response) => {
      if (response && response.data) {
        searchSuggestion(response.data.content);
      }
    })
    .catch((xhr, status, error) => {
      showNotification("An error occurred while submitting the form.", "error");
      console.log(xhr.responseText);
    });
}

function searchSuggestion(listProduct) {
  $("#productTableBody").empty();
  $("#pagination-wrapper").empty();
  listProduct.forEach((product) => {
    const productRow = createProductRow(product);
    $("#productTableBody").append(productRow);
  });
}

async function fetchProductCategory() {
  return await fetchData(
    `http://${userService.getApiUrl()}/api/product/category/all`
  );
}

async function fetchMaterial() {
  return await fetchData(`http://${userService.getApiUrl()}/api/material/all`);
}
async function fetchGemStoneCategory() {
  return await fetchData(
    `http://${userService.getApiUrl()}/api/gemStone/category/all`
  );
}
async function fetchGemStoneType() {
  return await fetchData(
    `http://${userService.getApiUrl()}/api/gemStone/type/all`
  );
}

async function fetchGemStoneOfProduct(productId) {
  return await fetchData(
    `http://${userService.getApiUrl()}/api/gemStone/product?id=${productId}`
  );
}

async function fetchData(url) {
  try {
    const response = await userService.sendAjaxWithAuthen(url, "GET", null);
    if (response.status === "OK" && response.data) {
      return response.data.content;
    } else {
      console.error(`Failed to fetch data from ${url}`);
      return [];
    }
  } catch (error) {
    console.error(`Error fetching data from ${url}:`, error);
    return [];
  }
}

async function fetchData2(url) {
  try {
    const response = await userService.sendAjaxWithAuthen(url, "GET", null);

    if (response.status === "OK" && response.data) {
      return response.data;
    } else {
      console.error(`Failed to fetch2 data from ${url}`);
      return [];
    }
  } catch (error) {
    console.error(`Error fetching 2 data from ${url}:`, error);
    return [];
  }
}
function setupFormSubmissions() {
  setupFormSubmission(
    "#submit-insert",
    "#form-insert",
    `http://${userService.getApiUrl()}/api/product/create`,
    handleFormInsertResponse,
    insertFormToProduct
  );
  setupFormSubmission(
    "#submit-update",
    "#form-update",
    `http://${userService.getApiUrl()}/api/product/update`,
    handleFormUpdateResponse,
    updateFormToProduct
  );
  setupFormSubmission(
    "#submit-insert-gemStone",
    "#form-insert-gemStone",
    `http://${userService.getApiUrl()}/api/gemStone/product`,
    handleFormUpdateGemResponse,
    updateFormToGemStone
  );
}
async function handleFormUpdateGemResponse(response) {
  $("#modalClose_CreateGem").click();
  const product = response.data.product.id;
  await detailModal(product);
}

function setupFormSubmission(
  buttonSelector,
  formSelector,
  url,
  successCallback,
  formToObject
) {
  $(document).on("click", buttonSelector, async function (event) {
    event.preventDefault();
    if (validateFormFields(formSelector)) {
      const formData = await formToObject();
      console.log(formData);
      userService.sendAjaxWithAuthen(
        url,
        "POST",
        successCallback,
        function (xhr, status, error) {
          showNotification(
            "An error occurred while submitting the form.",
            "Error"
          );

          console.log(xhr.responseText);
        },
        formData
      );
    } else {
      showNotification("You must fill all fields.", "Error");
    }
  });
}

function validateFormFields(formSelector) {
  let allFieldsFilled = true;
  $(formSelector)
    .find("input[type='text'], input[type='number'], textarea, select")
    .each(function () {
      if ($(this).val() === "") {
        allFieldsFilled = false;
        return false; // Break out of the loop if any field is empty
      }
    });
  return allFieldsFilled;
}

function handleFormInsertResponse(response) {
  const product = response.data;
  const productId = product.id;
  const index = state.querySet.findIndex((item) => item.id == productId);
  if (index !== -1) {
    state.querySet[index] = productFromResponse;
  } else {
    state.querySet.push(productFromResponse);
  }
  showNotification("Form submitted successfully.", "OK");
}

function handleFormUpdateResponse(response) {
  const product = response.data;
  const productId = product.id;
  const index = state.querySet.findIndex((item) => item.id == productId);
  if (index !== -1) {
    // Replace the existing product with the new product
    state.querySet[index] = product;
  } else {
    // If the product does not exist, you might want to add it to the array
    state.querySet.push(product);
  }
  //alert(response.desc);
  showNotification(response.desc, "OK");

  $("#cancel-update").click();
  const $row = $(`tr[data-id-product="${product.id}"]`);

  if ($row.length) {
    $row.find("td:nth-child(1)").text(product.id);
    $row.find("td:nth-child(2)").text(product.name);
    $row.find("td:nth-child(3)").text(product.materialDTO.name);
    $row.find("td:nth-child(4)").text(product.productCategoryDTO.name);
    $row.find("td:nth-child(5)").text(product.status ? "enable" : "disable");
    $row.find("td:nth-child(6)").text(product.counterDTO.name);
  }
  populateModalFields(product);
}
function handleFormUpload(response) {
  return response.data;
}

function updateFormToGemStone() {
  let gem = {
    color: $("#colorC").val(),
    clarity: $("#gemstone-clarity").val(),
    carat: $("#caratC").val(),
    price: $("#priceGemC").val(),
    gemstoneType: {
      id: $("#idTypeC").val(),
    },
    gemstoneCategory: {
      id: $("#idCategoryGemC").val(),
    },
    product: {
      id: $('button[name="addGemStone"]').attr("data-product-id"),
    },
    quantity: $("#quantityC").val(),
  };
  return gem;
}

async function updateFormToProduct() {
  var imgPathUp = await uploadImage($("#productImgUpdate").prop("files")[0]);
  let product = {
    id: $("#submit-update").attr("data-product-id"),
    name: $("#update-name").val(),
    fee: $("#update-fee").val(),
    materialDTO: {
      id: $("#update-material").val(),
    },
    weight: $("#update-weight").val(),
    productCategoryDTO: {
      id: $("#update-category").val(),
    },
    imgPath: $("#imgDetailInput").val(),
    productCode: $("#productCode_detailInput").val(),
    barcode: $("#productBarcode").val(),
    weight: $("#update-weight").val(),
    status: $("#update-status").val(),
    imgPath: imgPathUp === "none" ? null : imgPathUp,
  };
  console.log(product);
  return product;
}
async function insertFormToProduct() {
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
    imgPath: await uploadImage($("#productImgC").prop("files")[0]),
  };
  console.log(product);
  return product;
}
async function uploadImage(file) {
  if (file == null) {
    console.log(file);
    return "none";
  }
  var formData = new FormData();
  formData.append("file", file);
  try {
    const response = await userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/product/upload`,
        "POST",
        formData
      )
      .then((response) => {
        return response.data;
      })
      .catch((xhr, status, error) => {
        showNotification(
          "An error occurred while submitting the form.",
          "error"
        );
        console.log(xhr.responseText);
      });
    if (response) return response.data;
  } catch (error) {
    showNotification("An error occurred while uploading the image.", "error");
    console.error(error);
    return "none";
  }
}

function setUpOnChange() {
  $("#productImgC").change(function () {
    previewImage(
      "#ImgcLabel",
      $(this).prop("files")[0],
      "#ImgcLabelPlaceHolder"
    );
  });
  $("#productImgUpdate").change(function () {
    previewImage(
      "#updateImagePlaceHolder",
      $(this).prop("files")[0],
      "#imgDetail"
    );
  });
}

function previewImage(idDisplay, file, placeholder) {
  if (file) {
    $(idDisplay).empty();
    const reader = new FileReader();
    reader.onload = function (e) {
      const img = document.createElement("img");
      img.src = e.target.result;
      img.className =
        "flex flex-col items-center justify-center w-full h-3/6 border-2";
      $(idDisplay).append(img);
      $(placeholder).addClass("hidden").removeClass("flex");
    };
    reader.readAsDataURL(file);
  }
}
