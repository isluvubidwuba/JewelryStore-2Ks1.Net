import UserService from "./userService.js";

const userService = new UserService();
$(document).ready(function () {
  // Sự kiện click cho nút Active select
  $(document).on("click", "#active-selected-products", function () {
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    activeSelectedProducts(promotionId);
  });
  // Sự kiện click cho nút Detail
  $(document).on("click", "#modalToggle_Detail_Apply", function () {
    const promotionId = $(this).attr("data-promotion-id");
    const promotionName = $(this).attr("data-promotion-name");
    fetchProductsByPromotion(promotionId, promotionName);
    $("#detail-modal_ProductApply").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal
  $(document).on("click", "#modalClose_ProductApply", function () {
    $("#detail-modal_ProductApply").addClass("hidden");
  });

  // Sự kiện click cho nút Add Products
  $(document).on("click", "#add-products", function () {
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    fetchProductsNotInPromotion(promotionId);
    $("#modalTitle").text("Add Products to Promotion");
    $("#combinedModal").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal Combined
  $(document).on(
    "click",
    "#closeCombinedModal, #cancelSelectItems",
    function () {
      $("#combinedModal").addClass("hidden");
    }
  );

  // Sự kiện click cho nút Add Selected Items
  $(document).on("click", "#submitAddItems", function () {
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    addSelectedProductsToPromotion(promotionId);
  });

  // Sự kiện click cho nút xóa (một lần duy nhất)
  $(document).on("click", "#delete-selected-products", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    deleteSelectedProducts(promotionId);
  });

  setupFilters();
  // Sự kiện click cho checkbox
  $(document).on("change", ".item-checkbox", function () {
    const productId = $(this).val();
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    if (this.checked) {
      checkProductInOtherActivePromotions(productId, promotionId, $(this));
    }
  });

  // Sự kiện click cho nút close và cancel trong modal xác nhận
  $(document).on("click", "#closeConfirmModal, #cancelConfirm", function () {
    $("#confirmModal").addClass("hidden");
    // Bỏ tick checkbox
    const checkbox = $("#confirmYes").data("checkbox");
    if (checkbox) {
      checkbox.prop("checked", false);
    }
  });

  // Sự kiện click cho nút Yes trong modal xác nhận
  $(document).on("click", "#confirmYes", function () {
    $("#confirmModal").addClass("hidden");
    // Select sản phẩm
    $(this).data("checkbox").prop("checked", true);
  });
});
function checkProductInOtherActivePromotions(productId, promotionId, checkbox) {
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/promotion-generic/check/PRODUCT/${productId}/${promotionId}`,
    "GET",
    function (response) {
      if (response.status === "CONFLICT") {
        displayConflictModal(response.data, response.desc, checkbox);
      }
    },
    function (error) {
      console.error("Error checking product in other promotions:", error);
    },
    null
  );
}

export function displayConflictModal(conflictPromotions, desc, checkbox) {
  $("#conflictPromotions").empty();
  const descInfo = `<p class="mb-4">${desc}</p>`;
  $("#conflictPromotions").append(descInfo);
  conflictPromotions.forEach(function (promotion) {
    const promoInfo = `
          <div class="mb-4 p-4 border rounded shadow-sm">
              <p><strong>ID:</strong> ${promotion.id}</p>
              <p><strong>Name:</strong> ${promotion.name}</p>
              <p><strong>Promotion for invoice:</strong> ${
                promotion.invoiceTypeDTO.name
              }</p>
              <p><strong>Value:</strong> ${promotion.value}</p>
              <p><strong>Status:</strong> ${
                promotion.status ? "Active" : "Inactive"
              }</p>
              <p><strong>Start Date:</strong> ${promotion.startDate}</p>
              <p><strong>End Date:</strong> ${promotion.endDate}</p>
              <p><strong>Last Modified:</strong> ${promotion.lastModified}</p>
              <p><strong>Promotion Type:</strong> ${promotion.promotionType}</p>
          </div>
      `;
    $("#conflictPromotions").append(promoInfo);
  });

  const confirmMessage = `<p>Are you sure you want to add it to promotion? It will be disabled in other active promotions.</p>`;
  $("#confirmMessage").html(confirmMessage);

  $("#confirmYes").data("checkbox", checkbox);
  $("#confirmModal").removeClass("hidden");
}

function activeSelectedProducts(promotionId) {
  var selectedProductIds = [];
  $(".product-checkbox:checked").each(function () {
    selectedProductIds.push($(this).val());
  });
  var entity = "PRODUCT";
  if (selectedProductIds.length > 0) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
      "POST",
      function (response) {
        // Sau khi thực hiện thành công, tải lại danh sách sản phẩm
        fetchProductsByPromotion(
          promotionId,
          $("#promotion-name-listapply").text()
        );
        showNotification("Active successful", "OK");
      },
      function (error) {
        console.error("Error inactivating selected products:", error);
      },
      {
        promotionId: promotionId,
        entityIds: selectedProductIds,
        entityType: entity,
      }
    );
  } else {
    showNotification(
      "Please select at least one product to inactive.",
      "Error"
    );
  }
}

function setupFilters() {
  // Sự kiện thay đổi cho bộ lọc category và material
  $("#categoryFilter, #categoryFilterApply").on("change", filterItems);
  $("#materialFilter, #materialFilterApply").on("change", filterItems);
}

function populateCategoryFilter(categories) {
  const categoryFilter = $("#categoryFilter");
  categoryFilter.empty();
  categoryFilter.append(
    $("<option>", { value: "all", text: "All Categories" })
  );
  categories.forEach((category) => {
    const option = $("<option>", { value: category, text: category });
    categoryFilter.append(option);
  });

  const categoryFilterApply = $("#categoryFilterApply");
  categoryFilterApply.empty();
  categoryFilterApply.append(
    $("<option>", { value: "all", text: "All Categories" })
  );
  categories.forEach((category) => {
    const option = $("<option>", { value: category, text: category });
    categoryFilterApply.append(option);
  });
}

function populateMaterialFilter(materials) {
  const materialFilter = $("#materialFilter");
  materialFilter.empty();
  materialFilter.append($("<option>", { value: "all", text: "All Materials" }));
  materials.forEach((material) => {
    materialFilter.append($("<option>", { value: material, text: material }));
  });

  const materialFilterApply = $("#materialFilterApply");
  materialFilterApply.empty();
  materialFilterApply.append(
    $("<option>", { value: "all", text: "All Materials" })
  );
  materials.forEach((material) => {
    materialFilterApply.append(
      $("<option>", { value: material, text: material })
    );
  });
}

function filterItems() {
  const selectedCategory = $("#categoryFilter").val();
  const selectedMaterial = $("#materialFilter").val();
  const selectedCategoryApply = $("#categoryFilterApply").val();
  const selectedMaterialApply = $("#materialFilterApply").val();
  const rowsCombined = $("#itemTableBody tr");
  const rowsApply = $("#product-apply-promotion tr");

  rowsCombined.each(function () {
    const row = $(this);
    const itemCategory = row.find("td").eq(2).text();
    const itemMaterial = row.find("td").eq(3).text();

    if (
      (selectedCategory === "all" || itemCategory === selectedCategory) &&
      (selectedMaterial === "all" || itemMaterial === selectedMaterial)
    ) {
      row.show();
    } else {
      row.hide();
    }
  });

  rowsApply.each(function () {
    const row = $(this);
    const itemCategory = row.find("td").eq(5).text();
    const itemMaterial = row.find("td").eq(4).text();

    if (
      (selectedCategoryApply === "all" ||
        itemCategory === selectedCategoryApply) &&
      (selectedMaterialApply === "all" ||
        itemMaterial === selectedMaterialApply)
    ) {
      row.show();
    } else {
      row.hide();
    }
  });
}

// Fetch products by promotion
function fetchProductsByPromotion(promotionId, promotionName) {
  var productTableBody = $("#product-apply-promotion");
  productTableBody.empty(); // Clear existing rows
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/promotion-generic/in-promotion/PRODUCT/${promotionId}`,
    "GET",
    function (response) {
      var products = response.data;
      $("#promotion-name-listapply").text(promotionName);
      if (products.length > 0 && response.status === "OK") {
        var categories = [];
        var materials = [];
        products.forEach(function (forProduct) {
          const product = forProduct.productDTO;
          const statusText = forProduct.status ? "Active" : "Inactive";
          const row = `
                    <tr>
                        <td class="px-6 py-3">${product.id}</td>
                        <td class="px-6 py-3">${product.productCode}</td>
                        <td class="px-6 py-3">${product.barCode}</td>
                        <td class="px-6 py-3">${product.name}</td>
                        <td class="px-6 py-3">${product.materialDTO.name}</td>
                        <td class="px-6 py-3">${product.productCategoryDTO.name}</td>
                        <td class="px-6 py-3">${statusText}</td>
                        <td class="px-6 py-3">
                            <input type="checkbox" class="product-checkbox item-checkbox" value="${product.id}">
                        </td>
                    </tr>
                `;
          productTableBody.append(row);
          categories.push(product.productCategoryDTO.name);
          materials.push(product.materialDTO.name);
        });

        populateCategoryFilter([...new Set(categories)]);
        populateMaterialFilter([...new Set(materials)]);
      } else {
        showNotification(
          "Không tìm thấy sản phẩm nào cho promotion này.",
          "Error"
        );
      }
    },
    function (error) {
      console.error("Lỗi khi tải sản phẩm từ promotion:", error);
    },
    null
  );
}

// Xóa các sản phẩm đã chọn khỏi promotion
function deleteSelectedProducts(promotionId) {
  var selectedProductIds = [];
  $(".product-checkbox:checked").each(function () {
    selectedProductIds.push($(this).val());
  });
  if (selectedProductIds.length > 0) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/remove`,
      "POST",
      function (response) {
        // Sau khi xóa thành công, tải lại danh sách sản phẩm
        if (response.status === "OK") {
          fetchProductsByPromotion(
            promotionId,
            $("#promotion-name-listapply").text()
          );
          showNotification(response.desc, "OK");
        }
      },
      function (error) {
        showNotification(response.desc, "error");
        console.error("Error deleting selected products:", error);
      },
      {
        promotionId: promotionId,
        entityIds: selectedProductIds,
        entityType: "PRODUCT",
      }
    );
  } else {
    showNotification("Please select at least one product to delete.", "Error");
  }
}

// Fetch products not in promotion
function fetchProductsNotInPromotion(promotionId) {
  var itemTableBody = $("#itemTableBody");
  itemTableBody.empty(); // Clear existing rows
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/promotion-generic/not-in-promotion/PRODUCT/${promotionId}`,
    "GET",
    function (response) {
      var products = response.data;
      if (products.length > 0) {
        products.forEach(function (product) {
          const row = `
            <tr>
              <td class="px-6 py-3">${product.id}</td>
              <td class="px-6 py-3">${product.name}</td>
              <td class="px-6 py-3">${product.productCategoryDTO.name}</td>
              <td class="px-6 py-3">${product.materialDTO.name}</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="item-checkbox" value="${product.id}">
              </td>
            </tr>
          `;
          itemTableBody.append(row);
        });
        populateCategoryFilter([
          ...new Set(
            products.map((product) => product.productCategoryDTO.name)
          ),
        ]);
        populateMaterialFilter([
          ...new Set(products.map((product) => product.materialDTO.name)),
        ]);
      } else {
        $("#notiBlankItems").text("No products found not in this promotion.");
      }
    },
    function (error) {
      console.error("Error fetching products not in promotion:", error);
    },
    null
  );
}

// Thêm các sản phẩm đã chọn vào promotion
function addSelectedProductsToPromotion(promotionId) {
  var selectedProductIds = [];
  $("#itemTableBody .item-checkbox:checked").each(function () {
    selectedProductIds.push($(this).val());
  });
  var entity = "PRODUCT";
  if (selectedProductIds.length > 0) {
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
      "POST",
      function (response) {
        showNotification(response.desc, "OK");

        // Sau khi thêm thành công, tải lại danh sách sản phẩm
        fetchProductsByPromotion(
          promotionId,
          $("#promotion-name-listapply").text()
        );
        $("#combinedModal").addClass("hidden");
      },
      function (error) {
        console.error("Error adding selected products:", error);
      },
      {
        promotionId: promotionId,
        entityIds: selectedProductIds,
        entityType: entity,
      }
    );
  } else {
    showNotification(response.desc, "Error");
  }
}
