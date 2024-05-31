$(document).ready(function () {
  // Sự kiện click cho nút Detail
  $("#modalToggle_Detail_Apply").on("click", function () {
    const promotionId = $(this).attr("data-promotion-id");
    const promotionName = $(this).attr("data-promotion-name");
    fetchProductsByPromotion(promotionId, promotionName);
    $("#detail-modal_ProductApply").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal
  $("#modalClose_ProductApply").on("click", function () {
    $("#detail-modal_ProductApply").addClass("hidden");
  });

  // Sự kiện click cho nút Add Products
  $("#add-products").on("click", function () {
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    fetchProductsNotInPromotion(promotionId);
    $("#modalTitle").text("Add Products to Promotion");
    $("#combinedModal").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal Combined
  $("#closeCombinedModal, #cancelSelectItems").on("click", function () {
    $("#combinedModal").addClass("hidden");
  });

  // Sự kiện click cho nút Add Selected Items
  $("#submitAddItems").on("click", function () {
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    addSelectedProductsToPromotion(promotionId);
  });

  // Sự kiện click cho nút xóa (một lần duy nhất)
  $("#delete-selected-products").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Detail_Apply").attr(
      "data-promotion-id"
    );
    deleteSelectedProducts(promotionId);
  });

  setupFilters();
});

function setupFilters() {
  $("#categoryFilter, #categoryFilterApply").on("change", function () {
    filterItems();
  });

  $("#materialFilter, #materialFilterApply").on("change", function () {
    filterItems();
  });
}

function populateCategoryFilter(categories) {
  const categoryFilter = $("#categoryFilter");
  categoryFilter.empty();
  categoryFilter.append(
    $("<option>", {
      value: "all",
      text: "All Categories",
    })
  );
  categories.forEach((category) => {
    const option = $("<option>", {
      value: category,
      text: category,
    });
    categoryFilter.append(option);
  });

  const categoryFilterApply = $("#categoryFilterApply");
  categoryFilterApply.empty();
  categoryFilterApply.append(
    $("<option>", {
      value: "all",
      text: "All Categories",
    })
  );
  categories.forEach((category) => {
    const option = $("<option>", {
      value: category,
      text: category,
    });
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
    const itemCategory = row.find("td").eq(3).text();
    const itemMaterial = row.find("td").eq(2).text();

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
  $.ajax({
    url: `http://localhost:8080/promotion-for-product/promotion/${promotionId}`,
    type: "GET",
    success: function (response) {
      var products = response.data;
      $("#promotion-name-listapply").text(promotionName);
      if (products.length > 0 && response.status === "OK") {
        products.forEach(function (product) {
          const row = `
              <tr>
                <td class="px-6 py-3">${product.id}</td>
                <td class="px-6 py-3">${product.name}</td>
                <td class="px-6 py-3">${product.materialDTO.name}</td>
                <td class="px-6 py-3">${product.productCategoryDTO.name}</td>
                <td class="px-6 py-3">${
                  product.status ? "Active" : "Inactive"
                }</td>
                <td class="px-6 py-3">${product.counterDTO.name}</td>
                <td class="px-6 py-3">
                  <input type="checkbox" class="product-checkbox" value="${
                    product.id
                  }">
                </td>
              </tr>
            `;
          productTableBody.append(row);
        });
        populateCategoryFilter([
          ...new Set(
            products.map((product) => product.productCategoryDTO.name)
          ),
        ]);
        populateMaterialFilter([
          ...new Set(products.map((product) => product.materialDTO.name)),
        ]);
      } else if (response.status === "NOT_FOUND") {
        alert("No products found for this promotion.");
      }
    },
    error: function (error) {
      console.error("Error fetching products by promotion:", error);
    },
  });
}

// Xóa các sản phẩm đã chọn khỏi promotion
function deleteSelectedProducts(promotionId) {
  var selectedProductIds = [];
  $(".product-checkbox:checked").each(function () {
    selectedProductIds.push($(this).val());
  });

  if (selectedProductIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/promotion-for-product/remove-promotion",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        productIds: selectedProductIds,
      }),
      success: function (response) {
        // Sau khi xóa thành công, tải lại danh sách sản phẩm
        if (response.data === "OK") {
          fetchProductsByPromotion(
            promotionId,
            $("#promotion-name-listapply").text()
          );
          alert("Remove successful");
        }
      },
      error: function (error) {
        console.error("Error deleting selected products:", error);
      },
    });
  } else {
    alert("Please select at least one product to delete.");
  }
}

// Fetch products not in promotion
function fetchProductsNotInPromotion(promotionId) {
  var itemTableBody = $("#itemTableBody");
  itemTableBody.empty(); // Clear existing rows
  $.ajax({
    url: `http://localhost:8080/promotion-for-product/not-in-promotion/${promotionId}`,
    type: "GET",
    success: function (response) {
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
    error: function (error) {
      console.error("Error fetching products not in promotion:", error);
    },
  });
}

// Thêm các sản phẩm đã chọn vào promotion
function addSelectedProductsToPromotion(promotionId) {
  var selectedProductIds = [];
  $("#itemTableBody .item-checkbox:checked").each(function () {
    selectedProductIds.push($(this).val());
  });

  if (selectedProductIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/promotion-for-product/apply-promotion",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        productIds: selectedProductIds,
      }),
      success: function (response) {
        // Sau khi thêm thành công, tải lại danh sách sản phẩm
        fetchProductsByPromotion(
          promotionId,
          $("#promotion-name-listapply").text()
        );
        $("#combinedModal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error adding selected products:", error);
      },
    });
  } else {
    alert("Please select at least one product to add.");
  }
}
