// $(document).ready(function () {
//   fetchProduct(state.currentServerPage, state.size); // Fetch products on page load
//   setupModalToggles();
//   submitForm();
//   searchProduct();
// });

// var listProduct = [];
// var listGemsDetail = null;
// var state = {
//   querySet: [],
//   page: 1,
//   rows: 10,
//   totalPagesAtClient: 0,
//   currentServerPage: 0,
//   actualPageIndexAtClient: 1,
//   currentPageAtClient: 0,
//   previousPageAtClient: 0,
//   size: 50,
// };
// var timeout = null;
// // Fetch Product by page
// function fetchProduct(page, size) {
//   const linkProduct = `http://localhost:8080/product/all?page=${page}&size=${size}`;
//   $.ajax({
//     url: linkProduct,
//     method: "GET",
//     success: function (response) {
//       $("#productTableBody").empty(); // Clear existing products

//       if (response && response.data) {
//         const { content, totalElements } = response.data;
//         state.querySet = content; // Replace with new records
//         state.totalPagesAtClient = Math.ceil(totalElements / state.rows);
//         state.currentServerPage = page;
//         buildTable(state); // Build table after fetching products
//       }
//     },
//     error: function (error) {
//       console.error("Error fetching product:", error);
//     },
//   });
// }
// function searchSuggestion(listProduct) {
//   $("#productTableBody").empty();
//   $("#pagination-wrapper").empty();

//   listProduct.forEach(function (product) {
//     const productRow = `
//         <tr class="font-bold border-b">
//             <td class="px-6 py-4">${product.id}</td>
//             <td class="px-6 py-4">${product.name}</td>
//             <td class="px-6 py-4">${product.materialDTO.name}</td>
//             <td class="px-6 py-4">${product.productCategoryDTO.name}</td>
//             <td class="px-6 py-4">${product.status ? "enable" : "disable"}</td>
//             <td class="px-6 py-4">${product.counterDTO.name}</td>
//             <td class="px-6 py-4">
//                 <div class="relative flex justify-items-center">
//                     <button type="button" name="modalToggle_Detail" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" data-id="${
//                       product.id
//                     }">
//                         Detail
//                     </button>
//                 </div>
//             </td>
//         </tr>
//       `;
//     $("#productTableBody").append(productRow);
//   });
// }
// function buildTable(state) {
//   $("#productTableBody").empty();
//   var data = pagination(state.querySet, state.page, state.rows);
//   var myList = data.querySet;

//   myList.forEach(function (product) {
//     const productRow = `
//         <tr class="font-bold border-b">
//             <td class="px-6 py-4">${product.id}</td>
//             <td class="px-6 py-4">${product.name}</td>
//             <td class="px-6 py-4">${product.materialDTO.name}</td>
//             <td class="px-6 py-4">${product.productCategoryDTO.name}</td>
//             <td class="px-6 py-4">${product.status ? "enable" : "disable"}</td>
//             <td class="px-6 py-4">${product.counterDTO.name}</td>
//             <td class="px-6 py-4">
//                 <div class="relative flex justify-items-center">
//                     <button type="button" name="modalToggle_Detail" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" data-id="${
//                       product.id
//                     }">
//                         Detail
//                     </button>
//                 </div>
//             </td>
//         </tr>
//       `;
//     $("#productTableBody").append(productRow);
//   });
//   createPagination(state.totalPagesAtClient, state.page, data.pages);
// }

// function createPagination(totalPages, page, actualTotalPagesAtClient) {
//   if (totalPages <= 1) return;
//   let liTag = "";
//   let active;
//   let beforePage = page - 1;
//   let afterPage = page + 1;

//   if (page > 1) {
//     liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${
//       page - 1
//     }">
//                 <span class="flex items-center"><i class="fas fa-angle-left"></i> Prev</span></li>`;
//   }

//   if (page > 2) {
//     liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="1"><span>1</span></li>`;
//     if (page > 3) {
//       liTag += `<li class="relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer"><span>...</span></li>`;
//     }
//   }

//   if (page == totalPages) {
//     beforePage = beforePage - 2;
//   } else if (page == totalPages - 1) {
//     beforePage = beforePage - 1;
//   }

//   if (page == 1) {
//     afterPage = afterPage + 2;
//   } else if (page == 2) {
//     afterPage = afterPage + 1;
//   }

//   for (var plength = beforePage; plength <= afterPage; plength++) {
//     if (plength > totalPages) {
//       continue;
//     }
//     if (plength <= 0) {
//       continue;
//     }
//     if (page == plength) {
//       active = "bg-blue-500 text-white";
//     } else {
//       active = "bg-white text-gray-700 hover:bg-gray-50";
//     }
//     liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium ${active} cursor-pointer" data-page="${plength}"><span>${plength}</span></li>`;
//   }

//   if (page < totalPages - 1) {
//     if (page < totalPages - 2) {
//       liTag += `<li class="relative rounded-full inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer"><span>...</span></li>`;
//     }
//     liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${totalPages}"><span>${totalPages}</span></li>`;
//   }

//   if (page < totalPages) {
//     liTag += `<li class="page rounded-full relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 cursor-pointer" data-page="${
//       page + 1
//     }">
//                 <span>Next <i class="fas fa-angle-right"></i></span></li>`;
//   }
//   $("#pagination-wrapper").html(liTag);
//   $(".page").on("click", function () {
//     state.previousPageAtClient = state.page;
//     state.page = Number($(this).data("page"));
//     state.currentPageAtClient = state.page;
//     let serverPage = Math.floor((state.page - 1) / actualTotalPagesAtClient);
//     if (serverPage != state.currentServerPage) {
//       fetchProduct(serverPage, state.size); // Fetch new records for every new server page
//     } else {
//       buildTable(state);
//     }
//   });
// }

// function pagination(querySet, page, rows) {
//   var pages = Math.ceil(state.size / rows);
//   state.actualPageIndexAtClient = Math.floor((page - 1) / pages) * pages + 1;
//   var trimStart = (page - state.actualPageIndexAtClient) * rows;
//   var trimEnd = trimStart + rows;

//   var trimmedData = querySet.slice(trimStart, trimEnd);

//   return {
//     querySet: trimmedData,
//     pages: pages,
//   };
// }

// function submitUpdateForm() {
//   $(document).on("click", "#submit-update", function (event) {
//     event.preventDefault();

//     // Flag to track if all fields are filled
//     let allFieldsFilled = true;

//     // Validate all required fields
//     $("#form-update")
//       .find("input[type='text'], input[type='number'], textarea, select")
//       .each(function () {
//         if ($(this).val() === "") {
//           allFieldsFilled = false;
//           return false; // Break out of the loop if any field is empty
//         }
//       });

//     // If all fields are filled, proceed with AJAX request
//     if (allFieldsFilled) {
//       var formData = new FormData($("#form-update")[0]); // Sử dụng id của biểu mẫu

//       $.ajax({
//         url: "http://localhost:8080/promotion/update",
//         type: "POST",
//         data: formData,
//         processData: false,
//         contentType: false,
//         success: function (response) {
//           $("#form-update")
//             .find(
//               "input[type='text'], input[type='number'], input[type='file'], textarea, select"
//             )
//             .val(""); // Đặt các trường input, textarea và select thành trống sau khi thành công
//           $("#form-update").find("select").prop("selectedIndex", 0); // Đặt lại trạng thái của các select
//           $("#crud-update-modal").addClass("hidden");
//           handleUpdatePromotionResponse(response);
//           alert(response.desc);
//         },
//         error: function (xhr, status, error) {
//           alert("An error occurred while submitting the form.");
//           console.log(xhr.responseText);
//         },
//       });
//     } else {
//       alert("You must fill all fields.");
//     }
//   });
// }

// //open close modal
// async function setupModalToggles() {
//   toggleModal("#create-modal", "#modalClose_Create", "#modalToggle_Create");
//   toggleModal(
//     "#detail-modal",
//     "#modalClose_Detail",
//     'button[name="modalToggle_Detail"]'
//   );
//   toggleModal(
//     "#list-gems-modal",
//     "#modalClose_listGems",
//     "td>button[name='list_gem_detail']"
//   );

//   openProductModalDetail();
//   const listMaterial = await fetchMaterial();
//   listMaterial.forEach(function (item) {
//     const option = `<option class="dark:placeholder-gray-400" value="${item.id}" >
//     ${item.name}
//   </option>`;
//     $("#idMaterialC").append(option);
//   });

//   const listProductCategory = await fetchProductCategory();
//   listProductCategory.forEach(function (item) {
//     const option = `<option class="dark:placeholder-gray-400" value="${item.id}" >
//     ${item.name}
//   </option>`;
//     $("#idCategoryC").append(option);
//   });
// }
// //submit form create
// function submitForm() {
//   $(document).on("click", "#submit-insert", function (event) {
//     event.preventDefault();

//     let allFieldsFilled = true;

//     $("#form-insert")
//       .find("input[type='text'], input[type='number'], select")
//       .each(function () {
//         if ($(this).val() === "") {
//           allFieldsFilled = false;
//           return false; // Break out of the loop if any field is empty
//         }
//       });

//     // If all fields are filled, proceed with AJAX request
//     if (allFieldsFilled) {
//       let product = {
//         name: $("#nameC").val(),
//         fee: $("#feeC").val(),
//         materialDTO: {
//           id: $("#idMaterialC").val(),
//         },
//         weight: $("#materialWeightC").val(),
//         productCategoryDTO: {
//           id: $("#idCategoryC").val(),
//         },
//       };

//       $.ajax({
//         url: "http://localhost:8080/product/create",
//         type: "POST",
//         data: JSON.stringify(product),
//         processData: false,
//         contentType: "application/json; charset=utf-8",
//         success: function (response) {
//           alert("ok");
//           // $("#form-insert")
//           //   .find(
//           //     "input[type='text'], input[type='number'], input[type='file'], textarea, select"
//           //   )
//           //   .val(""); // Đặt các trường input, textarea và select thành trống sau khi thành công
//           // $("#form-insert").find("select").prop("selectedIndex", 0); // Đặt lại trạng thái của các select
//           // $("#crud-modal").addClass("hidden");
//           // console.log(response);
//           // alert(response.desc);
//         },
//         error: function (xhr, status, error) {
//           alert("An error occurred while submitting the form.");
//           console.log(xhr.responseText);
//         },
//       });
//     } else {
//       alert("You must fill all fields.");
//     }
//   });
// }

// async function fetchProductCategory() {
//   const linkProduct = "http://localhost:8080/product/category/all";
//   try {
//     const response = await $.ajax({
//       url: linkProduct,
//       method: "GET",
//     });
//     if (response.status === "OK" && response.data) {
//       const { content } = response.data;
//       return content;
//     } else {
//       console.error("Failed to fetch product category");
//       return null;
//     }
//   } catch (error) {
//     console.error("Error fetching product category:", error);
//     return null;
//   }
// }

// async function fetchMaterial() {
//   const linkProduct = "http://localhost:8080/material/all";
//   try {
//     const response = await $.ajax({
//       url: linkProduct,
//       method: "GET",
//     });
//     if (response.status === "OK" && response.data) {
//       const { content } = response.data;
//       return content;
//     } else {
//       console.error("Failed to fetch Material");
//       return null;
//     }
//   } catch (error) {
//     console.error("Error fetching Material:", error);
//     return null;
//   }
// }

// async function fetchCounter() {
//   const linkProduct = "http://localhost:8080/counter/all";
//   try {
//     const response = await $.ajax({
//       url: linkProduct,
//       method: "GET",
//     });
//     if (response.status === "OK" && response.data) {
//       const { content } = response.data;
//       return content;
//     } else {
//       console.error("Failed to fetch data");
//       return null;
//     }
//   } catch (error) {
//     console.error("Error fetching product:", error);
//     return null;
//   }
// }

// async function fetchGemStoneOfProduct(productId) {
//   const linkProduct = `http://localhost:8080/gemStone/product?id=${productId}`;
//   try {
//     const response = await $.ajax({
//       url: linkProduct,
//       method: "GET",
//     });
//     if (response.status === "OK" && response.data) {
//       return response.data;
//     } else {
//       console.error("Failed to fetch gem stone product data");
//       return null;
//     }
//   } catch (error) {
//     console.error("Error fetching gem stone product:", error);
//     return null;
//   }
// }

// function toggleModal(idModal, idClose, idOpen) {
//   $(document).on("click", idOpen, function () {
//     $(idModal).removeClass("hidden").addClass("flex");
//   });

//   $(document).on("click", idClose, function () {
//     $(idModal).addClass("hidden").removeClass("flex");
//   });
// }

// async function detailModal(productId) {
//   const product = $.map(listProduct, function (item) {
//     if (item.id == productId) {
//       return item;
//     }
//   })[0];
//   // Check if the product exists
//   if (product) {
//     // Populate modal fields with product details
//     $("#productCode_detail").text(product.productCode);
//     $("#name_detail").text(product.name);
//     $("#fee_detail").text(product.fee);
//     $("#material_detail").text(product.materialDTO.name);
//     $("#status_detail").text(product.status);
//     $("#category_detail").text(product.productCategoryDTO.name);
//     $("#category_detail").text(product.productCategoryDTO.name);
//     JsBarcode("#barcode", product.barCode);
//     // Show the modal
//   }
//   await buildTableGemStone(productId);
// }
// async function openProductModalDetail() {
//   $(document).on(
//     "click",
//     'button[name="modalToggle_Detail"]',
//     async function () {
//       // Get the data-id attribute of the clicked button
//       const productId = $(this).data("id");

//       // Call the detailModal function with the productId
//       await detailModal(productId);
//     }
//   );
// }

// async function buildTableGemStone(productId) {
//   $("#GemStone_detail_table").empty();
//   $("#notiBlank").empty();
//   let gemList = await fetchGemStoneOfProduct(productId);
//   if (gemList.length == 0) {
//     $("#notiBlank").text(`There are no gem stones in this product`);
//     return;
//   }
//   let listGemsDetail = await countCategoriesByType(gemList);
//   for (const type in listGemsDetail) {
//     for (const category in listGemsDetail[type]) {
//       const countRow = `
//         <tr class="font-bold border-b ">
//           <td class="px-6 py-3">
//             ${category}
//           </td>
//           <td class="px-6 py-3">
//             ${listGemsDetail[type][category].quantity}
//           </td>
//           <td class="px-6 py-3">
//             ${type}
//           </td>
//           <td  class="font-bold border-b px-6 py-3">
//             <button class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" name="list_gem_detail"  type="button"  data-type="${type}" data-category="${category}">
//               Detail
//             </button>
//           </td>
//         </tr>
//       `;
//       $("#GemStone_detail_table").append(countRow);
//     }
//   }

//   // Attach click event handler to the buttons
//   $("button[name='list_gem_detail']").on("click", function () {
//     const type = $(this).data("type");
//     const category = $(this).data("category");
//     const gems = listGemsDetail[type][category].gems;
//     displayGemDetails(gems);
//   });
// }

// function displayGemDetails(gems) {
//   let gemDetailsHtml = ""; // Initialize the variable

//   gems.forEach((gem) => {
//     gemDetailsHtml += `<tr class="font-bold border-b">
//                         <td class="px-6 py-3">${gem.color}</td>
//                         <td class="px-6 py-3">${gem.clarity}</td>
//                         <td class="px-6 py-3">${gem.carat}</td>
//                         <td class="px-6 py-3">${gem.price}</td>
//                         <td class="px-6 py-3">${gem.quantity}</td>
//                       </tr>`;
//   });

//   $("#Gems_detail_table").append(gemDetailsHtml);
// }
// function countCategoriesByType(gemList) {
//   const typeCategoryCount = {};

//   gemList.forEach((gem) => {
//     const typeName = gem.gemstoneType.name;
//     const categoryName = gem.gemstoneCategory.name;

//     if (!typeCategoryCount[typeName]) {
//       typeCategoryCount[typeName] = {};
//     }

//     if (!typeCategoryCount[typeName][categoryName]) {
//       typeCategoryCount[typeName][categoryName] = {
//         quantity: 0,
//         gems: [],
//       };
//     }

//     typeCategoryCount[typeName][categoryName].quantity += gem.quantity;
//     typeCategoryCount[typeName][categoryName].gems.push(gem);
//   });

//   return typeCategoryCount;
// }
// function searchProduct() {
//   var timeout = null;
//   $("#search-input").on("keyup", function () {
//     var text = this.value;
//     clearTimeout(timeout);
//     if (text.trim() === "") {
//       fetchProduct(state.currentServerPage, state.size);
//     } else {
//       timeout = setTimeout(function () {
//         $.ajax({
//           url: "http://localhost:8080/product/search",
//           type: "POST",
//           data: $.param({
//             search: text,
//             id_material: "",
//             id_product_category: "",
//             id_counter: "",
//           }), // Adjust the data format as needed
//           contentType: "application/x-www-form-urlencoded",
//           success: function (response) {
//             searchSuggestion(response.data);
//           },
//           error: function (xhr, status, error) {
//             alert("An error occurred while submitting the form.");
//             console.log(xhr.responseText);
//           },
//         });
//       }, 500);
//     }
//   });

//   $("#search-input").on("change", function () {
//     if ($(this).val().trim() === "0") {
//       fetchProduct(state.currentServerPage, state.size);
//     }
//   });
// }
// function searchSuggestion(listProduct) {
//   $("#productTableBody").empty();
//   $("#pagination-wrapper").empty();

//   listProduct.forEach(function (product) {
//     const productRow = `
//       <tr class="font-bold border-b">
//         <td class="px-6 py-4">${product.id}</td>
//         <td class="px-6 py-4">${product.name}</td>
//         <td class="px-6 py-4">${product.materialDTO.name}</td>
//         <td class="px-6 py-4">${product.productCategoryDTO.name}</td>
//         <td class="px-6 py-4">${product.status ? "enable" : "disable"}</td>
//         <td class="px-6 py-4">${product.counterDTO.name}</td>
//         <td class="px-6 py-4">
//           <div class="relative flex justify-items-center">
//             <button type="button" name="modalToggle_Detail" class="flex items-center w-fit h-fit gap-1 py-2 px-4 rounded-md hover:bg-gray-300 transition duration-300 ease-in-out transform hover:scale-110" data-id="${
//               product.id
//             }">
//               Detail
//             </button>
//           </div>
//         </td>
//       </tr>
//     `;
//     $("#productTableBody").append(productRow);
//   });
// }
$(document).ready(function () {
  init();
});

function init() {
  fetchProduct(state.currentServerPage, state.size); // Fetch products on page load
  setupModalToggles();
  setupFormSubmissions();
  setupSearch();
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

let listProduct = [];
let listGemsDetail = null;
let timeout = null;

function fetchProduct(page, size) {
  const linkProduct = `http://localhost:8080/product/all?page=${page}&size=${size}`;
  $.ajax({
    url: linkProduct,
    method: "GET",
    success: function (response) {
      if (response && response.data) {
        const { content, totalElements } = response.data;
        state.querySet = content; // Replace with new records
        state.totalPagesAtClient = Math.ceil(totalElements / state.rows);
        state.currentServerPage = page;
        buildTable(state); // Build table after fetching products
      }
    },
    error: function (error) {
      console.error("Error fetching product:", error);
    },
  });
}

function buildTable(state) {
  $("#productTableBody").empty();
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
    <tr class="font-bold border-b">
      <td class="px-6 py-4">${product.id}</td>
      <td class="px-6 py-4">${product.name}</td>
      <td class="px-6 py-4">${product.materialDTO.name}</td>
      <td class="px-6 py-4">${product.productCategoryDTO.name}</td>
      <td class="px-6 py-4">${product.status ? "enable" : "disable"}</td>
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

function setupModalToggles() {
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
  fetchDropdownData("#idMaterialC", fetchMaterial);
  fetchDropdownData("#idCategoryC", fetchProductCategory);
}

async function fetchDropdownData(elementId, fetchDataFunc) {
  const dataList = await fetchDataFunc();
  dataList.forEach(function (item) {
    const option = `<option class="dark:placeholder-gray-400" value="${item.id}">${item.name}</option>`;
    $(elementId).append(option);
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
  const product = listProduct.find((item) => item.id == productId);
  if (product) {
    populateModalFields(product);
    JsBarcode("#barcode", product.barCode);
  }
  await buildTableGemStone(productId);
}

function populateModalFields(product) {
  $("#productCode_detail").text(product.productCode);
  $("#name_detail").text(product.name);
  $("#fee_detail").text(product.fee);
  $("#material_detail").text(product.materialDTO.name);
  $("#status_detail").text(product.status);
  $("#category_detail").text(product.productCategoryDTO.name);
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

  $("#search-input").on("change", function () {
    if (this.value.trim() === "0") {
      fetchProduct(state.currentServerPage, state.size);
    }
  });
}

function searchProducts(query) {
  $.ajax({
    url: "http://localhost:8080/product/search",
    type: "POST",
    data: $.param({
      search: query,
      id_material: "",
      id_product_category: "",
      id_counter: "",
    }),
    contentType: "application/x-www-form-urlencoded",
    success: function (response) {
      if (response && response.data) {
        searchSuggestion(response.data);
      }
    },
    error: function (xhr, status, error) {
      alert("An error occurred while submitting the form.");
      console.log(xhr.responseText);
    },
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
  return fetchData("http://localhost:8080/product/category/all");
}

async function fetchMaterial() {
  return fetchData("http://localhost:8080/material/all");
}

async function fetchGemStoneOfProduct(productId) {
  return fetchData(`http://localhost:8080/gemStone/product?id=${productId}`);
}

async function fetchData(url) {
  try {
    const response = await $.ajax({ url, method: "GET" });
    if (response.status === "OK" && response.data) {
      return response.data;
    } else {
      console.error(`Failed to fetch data from ${url}`);
      return [];
    }
  } catch (error) {
    console.error(`Error fetching data from ${url}:`, error);
    return [];
  }
}

function setupFormSubmissions() {
  setupFormSubmission(
    "#submit-insert",
    "#form-insert",
    "http://localhost:8080/product/create",
    handleFormInsertResponse
  );
  setupFormSubmission(
    "#submit-update",
    "#form-update",
    "http://localhost:8080/promotion/update",
    handleFormUpdateResponse
  );
}

function setupFormSubmission(
  buttonSelector,
  formSelector,
  url,
  successCallback
) {
  $(document).on("click", buttonSelector, function (event) {
    event.preventDefault();

    if (validateFormFields(formSelector)) {
      const formData = new FormData($(formSelector)[0]);
      $.ajax({
        url,
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: successCallback,
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
  alert("Form submitted successfully.");
  // Clear the form and hide the modal, etc.
}

function handleFormUpdateResponse(response) {
  alert(response.desc);
  // Clear the form and hide the modal, etc.
}
