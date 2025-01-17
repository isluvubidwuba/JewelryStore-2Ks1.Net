import UserService from "./userService.js";

const userService = new UserService();
let listGemsDetail = null;
let timeout = null;
$(document).ready(function () {
  setupSearch();
  fetchProduct(state.currentServerPage, state.size);
  fetchDropdownData("#Material", fetchMaterial);
  fetchDropdownData("#Category", fetchProductCategory);
  fetchDropdownData("#Counter", fetchCounter);
  toggleModal(
    "#list-gems-modal",
    "#modalClose_listGems",
    "tr[name='list_gem_detail']"
  );
  // Event to create overlay for class card-block on click
  $("#cardHere").on("click", ".card-block", function () {
    // Remove overlay from all card-blocks
    $(".card-block .overlay").fadeOut(function () {
      $(this).remove();
    });

    // Toggle overlay and sidebar policy visibility
    var overlay = $(this).find(".overlay");
    if (overlay.length) {
      toggleSidebarPolicy(false);
      overlay.fadeOut(function () {
        $(this).remove();
      });
    } else {
      toggleSidebarPolicy(true);
      overlay = $("<div class='overlay'></div>");
      $(this).append(overlay);
      overlay.fadeIn();
      getPriceProduct(
        $(this).attr("data-barcode"),
        1,
        "#promoPoli",
        "#priceForSell"
      );
      getPriceProduct(
        $(this).attr("data-barcode"),
        3,
        "#resalePoli",
        "#priceForBuyBack"
      );
      buildTableGemStone($(this).attr("data-id"));
    }
  });
});

var state = {
  querySet: [],
  page: 1,
  rows: 8,
  totalPagesAtClient: 0,
  currentServerPage: 0,
  actualPageIndexAtClient: 1,
  currentPageAtClient: 0,
  previousPageAtClient: 0,
  size: 40,
  search: null,
};

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
        buildTable(state); // Build table after fetching products
      }
    })
    .catch((error) => {
      console.error("Error fetching product:", error);
    });
}

function buildTable(state) {
  $("#cardHere").empty();
  $("#notiBlankProduct").empty();
  const data = pagination(state.querySet, state.page, state.rows);
  const myList = data.querySet;

  myList.forEach(function (product) {
    const productRow = createProductRow(product);
    $("#cardHere").append(productRow);
  });

  createPagination(state.totalPagesAtClient, state.page, data.pages);
}

function createProductRow(product) {
  return `<div data-barcode="${product.barCode}" data-id="${product.id}"
              class="bg-gray-50 card-block max-w-xs max-h-96 rounded-lg overflow-hidden p-3 mr-2 ml-2 mt-2 mb-2 shadow-xl transition ease-in-out hover:-translate-y-1 hover:scale-110 hover:bg-gray-100 cursor-pointer"
            >
              <div
                class="w-full h-52 max-h-56 overflow-hidden rounded-xl shadow-inner hover:shadow-xl"
              >
                <img
                  src="${product.imgPath}"
                  alt="Sunset in the mountains"
                />
              </div>
              <div class="px-6 pt-3 w-full">
                <div
                  class="font-semibold text-l mb-2 text-pretty w-80 overflow-y-auto no-scrollbar"
                  style="max-height: 60px"
                >
                ${product.name}
                </div>
              </div>
              <div
                class="px-6 pt-2 pb-1 flex flex-wrap  justify-start overflow-y-auto w-full no-scrollbar"
                style="max-height: 60px "
              >
                <label class="tags "> ${product.productCategoryDTO.name} </label>
                <label class="tags"> ${product.materialDTO.name}</label>
                <label class="tags "> ${product.counterDTO.name} </label>
              </div>
            </div>`;
}

function pagination(querySet, page, rows) {
  $("#pagination-wrapper").empty();
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
      if (state.search == null) fetchProduct(serverPage, state.size);
      else searchProducts(state.search, serverPage, state.size);
    } else {
      buildTable(state);
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

function toggleSidebarPolicy(show) {
  if (show) {
    $("#sidbar-Policy").addClass("flex").removeClass("hidden");
  } else {
    $("#sidbar-Policy").addClass("hidden").removeClass("flex");
  }
}

function getPriceProduct(barcode, idInvocie, idPromo, idPrice) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/invoice/create-detail`,
      "POST",
      {
        barcode: barcode,
        quantity: 1,
        invoiceTypeId: idInvocie,
      }
    )
    .then((response) => {
      displayProductPromo(response.data.listPromotion, idPromo);
      $(idPrice).text(formatCurrency(response.data.totalPrice));
    })
    .catch((xhr, status, error) => {
      $(idPromo).empty();
      $(idPrice).text("Not available!");
      showNotification("Failed to send data: " + error, "error");
    });
}

function displayProductPromo(data, idPromo) {
  var detailsHtml = formatPromotions(data);
  $(idPromo).html(detailsHtml);
}

function formatPromotions(promotions) {
  if (!promotions.length) return "<p>None</p>";
  return promotions
    .map(function (promo) {
      return `
            <div class=" mt-1 flex flex-row mb-2 ">
                <div class="w-4/12 p-2">
                    <img src="${promo.image}" alt="Promotion Image" class="max-w-full h-auto rounded">
                </div>
                <div class="w-8/12 p-2">
                    <p class="text-lg font-semibold">${promo.name} (${promo.value}% off)</p>
                </div>
            </div>
        `;
    })
    .join("");
}

function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
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

  $("tr[name='list_gem_detail']").on("click", function () {
    const type = $(this).data("type");
    const category = $(this).data("category");
    const gems = listGemsDetail[type][category].gems;
    displayGemDetails(gems);
  });
}

async function fetchGemStoneOfProduct(productId) {
  return await fetchData(
    `http://${userService.getApiUrl()}/api/gemStone/product?id=${productId}`
  );
}

async function fetchData(url) {
  return await userService
    .sendAjaxWithAuthen(url, "GET", null)
    .then((response) => {
      if (response.status === "OK" && response.data) {
        return response.data.content;
      } else {
        console.error(`Failed to fetch data from ${url}`);
        return [];
      }
    })
    .catch((error) => {
      console.error(`Error fetching data from ${url}:`, error);
      return [];
    });
}
async function fetchData2(url) {
  return await userService
    .sendAjaxWithAuthen(url, "GET", null)
    .then((response) => {
      if (response.status === "OK" && response.data) {
        return response.data;
      } else {
        console.error(`Failed to fetch data from ${url}`);
        return [];
      }
    })
    .catch((error) => {
      console.error(`Error fetching data from ${url}:`, error);
      return [];
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
function createGemStoneRow(type, category, quantity) {
  return `
    <tr data-type="${type}" data-category="${category}" name="list_gem_detail" class="font-bold border-b transition ease-in-out hover:-translate-y-1 hover:scale-110 hover:bg-gray-100 cursor-pointer">
      <td class="px-6 py-3">${category}</td>
      <td class="px-6 py-3">${quantity}</td>
      <td class="px-6 py-3">${type}</td>
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
function toggleModal(idModal, idClose, idOpen) {
  $(document).on("click", idOpen, function () {
    $(idModal).removeClass("hidden").addClass("flex");
  });

  $(document).on("click", idClose, function () {
    $(idModal).addClass("hidden").removeClass("flex");
  });
}

function setupSearch() {
  $("#search-input").on("keyup", function () {
    $("#notiBlankProduct").empty();
    clearTimeout(timeout);
    const text = this.value.trim();
    state.search = text;
    if (text === "") {
      $("#clearSearch").addClass("hidden");
      $("#search-input").val("");
      $("#Material").val("");
      $("#Category").val("");
      $("#Counter").val("");
      state.search = null;
      fetchProduct(state.currentServerPage, state.size);
    } else {
      timeout = setTimeout(() => {
        state.currentPageAtClient = 0;
        state.currentServerPage = 0;
        state.previousPageAtClient = 0;
        state.actualPageIndexAtClient = 1;
        state.page = 1;
        searchProducts(state.search, 0, state.size);
      }, 500);
    }
  });
  $("#Category,#Material,#Counter").change(function () {
    if ($("#Material").val() || $("#Category").val() || $("#Counter").val())
      state.search = $("#search-input").val();
    state.currentPageAtClient = 0;
    state.currentServerPage = 0;
    state.previousPageAtClient = 0;
    state.actualPageIndexAtClient = 1;
    state.page = 1;
    searchProducts(state.search, 0, state.size);
  });

  $("#clearSearch").click(() => {
    $("#clearSearch").addClass("hidden");
    $("#sidbar-Policy").addClass("hidden");
    $("#search-input").val("");
    $("#Material").val("");
    $("#Category").val("");
    $("#Counter").val("");
    state.search = null;
    fetchProduct(state.currentServerPage, state.size);
  });
}
function searchProducts(query, pageS, sizeS) {
  $("#clearSearch").removeClass("hidden");
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/product/search`,
      "POST",
      $.param({
        search: query,
        id_material: $("#Material").val(),
        id_product_category: $("#Category").val(),
        id_counter: $("#Counter").val(),
        page: pageS,
        size: sizeS,
      })
    )
    .then((response) => {
      if (response && response.data) {
        showNotification("Search product successfully.", "OK");
        const { content, totalElements } = response.data;
        state.querySet = content;
        state.totalPagesAtClient = Math.ceil(totalElements / state.rows);
        state.currentServerPage = pageS;
        buildTable(state, "There are no product!"); // Build table after fetching products
      }
    })
    .catch((xhr, status, error) => {
      if (xhr.responseJSON) {
        showNotification(xhr.responseJSON.desc, "error");
        $("#cardHere").empty();
        $("#pagination-wrapper").empty();
        $("#notiBlankProduct").text(
          "There are no product suitable for your search!"
        );
      } else
        showNotification(
          "An error occurred while submitting the form.",
          "error"
        );
      console.log(xhr.responseText);
    });
}

async function fetchDropdownData(elementId, fetchDataFunc) {
  const dataList = await fetchDataFunc();
  dataList.forEach(function (item) {
    const option = `<option  value="${item.id}">${item.name}</option>`;
    $(elementId).append(option);
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
async function fetchCounter() {
  return await fetchData2(
    `http://${userService.getApiUrl()}/api/counter/allactivecounter`
  );
}

function parseIntWithPrefix(str, prefix) {
  // Check if the string starts with the prefix
  if (!str.startsWith(prefix)) return false;

  // Attempt to parse the string to an integer
  const parsed = parseInt(str, 10);

  // Check if the result is a valid integer
  if (!Number.isInteger(parsed)) return false;

  return true;
}

class BarcodeScaner {
  constructor() {
    this.timeoutHandler = 0;
    this.inputString = "";
  }

  initialize = () => {
    document.addEventListener("keydown", this.keydown);
    if (this.timeoutHandler) {
      clearTimeout(this.timeoutHandler);
    }
    this.timeoutHandler = setTimeout(() => {
      this.inputString = "";
    }, 10);
  };

  close = () => {
    document.removeEventListener("keydown", this.keydown);
  };

  keydown = (e) => {
    if (this.timeoutHandler) {
      clearTimeout(this.timeoutHandler);
      this.inputString += String.fromCharCode(e.keyCode);
    }

    this.timeoutHandler = setTimeout(() => {
      if (this.inputString.length <= 3) {
        this.inputString = "";
        return;
      }
      if (parseIntWithPrefix(this.inputString, "893171831")) {
        state.search = this.inputString;
        state.currentPageAtClient = 0;
        state.currentServerPage = 0;
        state.previousPageAtClient = 0;
        state.actualPageIndexAtClient = 1;
        state.page = 1;
        searchProducts(state.search, 0, state.size); // Call the addProductByBarcode function
      }
      this.inputString = "";
    }, 100);
  };
}

const barcodeScanner = new BarcodeScaner();
barcodeScanner.initialize();
