$(document).ready(function () {
  // Sự kiện click cho nút Category
  $("#modalToggle_Category_Apply").on("click", function () {
    const voucherId = $(this).attr("data-voucher-id");
    fetchCategoriesByVoucherType(voucherId);
    $("#detail-modal_CategoryApply").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal
  $("#modalClose_CategoryApply").on("click", function () {
    $("#detail-modal_CategoryApply").addClass("hidden");
  });

  // Sự kiện click cho nút Add
  $("#add-categories").on("click", function () {
    const voucherId = $("#modalToggle_Category_Apply").attr("data-voucher-id");
    fetchCategoriesNotInVoucherType(voucherId);
    $("#add-categories-modal").removeClass("hidden").addClass("flex");
  });

  // Sự kiện click cho nút Close modal Add Categories
  $("#modalClose_AddCategories").on("click", function () {
    $("#add-categories-modal").addClass("hidden");
  });

  // Sự kiện click cho nút Add Selected Categories
  $("#add-selected-categories").on("click", function () {
    const voucherId = $("#modalToggle_Category_Apply").attr("data-voucher-id");
    addSelectedCategoriesToVoucherType(voucherId);
  });

  // Gán sự kiện click cho nút xóa (một lần duy nhất)
  $("#delete-selected-categories").on("click", function (event) {
    event.preventDefault();
    const voucherId = $("#modalToggle_Category_Apply").attr("data-voucher-id");
    deleteSelectedCategories(voucherId);
  });
});

// Fetch categories by voucher type
function fetchCategoriesByVoucherType(voucherId) {
  var categoryTableBody = $("#category-apply-promotion");
  categoryTableBody.empty(); // Clear existing rows
  $.ajax({
    url: `http://localhost:8080/voucher/${voucherId}/categories`,
    type: "GET",
    success: function (response) {
      var categories = response.data;
      if (categories.length > 0 && response.status === "OK") {
        $("#notiBlankCategory").text("");
        categories.forEach(function (category) {
          const row = `
                <tr>
                  <td class="px-6 py-3">${category.id}</td>
                  <td class="px-6 py-3">${category.name}</td>
                  <td class="px-6 py-3">
                    <input type="checkbox" class="category-checkbox" value="${category.id}">
                  </td>
                </tr>
              `;
          categoryTableBody.append(row);
        });
      } else if (response.status === "NOT_FOUND") {
        alert("No categories found for this voucher type.");
      } else {
        $("#notiBlankCategory").text(
          "No categories found in this voucher type."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching categories by voucher type:", error);
    },
  });
}

// Xóa các categories đã chọn khỏi voucher type
function deleteSelectedCategories(voucherId) {
  var selectedCategoryIds = [];
  $(".category-checkbox:checked").each(function () {
    selectedCategoryIds.push($(this).val());
  });

  if (selectedCategoryIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/voucher/remove-categories",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: voucherId,
        productIds: selectedCategoryIds,
      }),
      success: function (response) {
        // Sau khi xóa thành công, tải lại danh sách categories
        if (response.status === "OK") {
          fetchCategoriesByVoucherType(voucherId);
          alert("Remove successful");
        }
      },
      error: function (error) {
        console.error("Error deleting selected categories:", error);
      },
    });
  } else {
    alert("Please select at least one category to delete.");
  }
}

// Fetch categories not in voucher type
function fetchCategoriesNotInVoucherType(voucherId) {
  var categoryTableBody = $("#category-not-apply-voucher");
  categoryTableBody.empty(); // Clear existing rows
  $.ajax({
    url: `http://localhost:8080/voucher/${voucherId}/categories/not-in`,
    type: "GET",
    success: function (response) {
      var categories = response.data;
      if (categories.length > 0) {
        $("#notiBlankCategoryNotInVoucher").text("");
        categories.forEach(function (category) {
          const row = `
                <tr>
                  <td class="px-6 py-3">${category.id}</td>
                  <td class="px-6 py-3">${category.name}</td>
                  <td class="px-6 py-3">
                    <input type="checkbox" class="category2-checkbox" value="${category.id}">
                  </td>
                </tr>
              `;
          categoryTableBody.append(row);
        });
      } else {
        $("#notiBlankCategoryNotInVoucher").text(
          "No categories found not in this voucher type."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching categories not in voucher type:", error);
    },
  });
}

// Thêm các categories đã chọn vào voucher type
function addSelectedCategoriesToVoucherType(voucherId) {
  var selectedCategoryIds = [];
  $("#category-not-apply-voucher .category2-checkbox:checked").each(
    function () {
      selectedCategoryIds.push($(this).val());
    }
  );

  if (selectedCategoryIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/voucher/apply-categories",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: voucherId,
        productIds: selectedCategoryIds,
      }),
      success: function (response) {
        // Sau khi thêm thành công, tải lại danh sách categories
        fetchCategoriesByVoucherType(voucherId);
        $("#add-categories-modal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error adding selected categories:", error);
      },
    });
  } else {
    alert("Please select at least one category to add.");
  }
}
