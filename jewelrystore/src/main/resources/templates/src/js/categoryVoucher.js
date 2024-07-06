$(document).ready(function () {
  $(document).on("click", "#modalToggle_Category_Apply", function () {
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    $("#detail-modal_CategoryApply").removeClass("hidden").addClass("flex");
    fetchCategoriesByPromotion(promotionId);
  });
  $("#modalClose_CategoryApply").on("click", function () {
    $("#detail-modal_CategoryApply").addClass("hidden");
  });

  $("#add-categories").on("click", function () {
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    fetchCategoriesNotInPromotion(promotionId);
    $("#add-categories-modal").removeClass("hidden").addClass("flex");
  });

  $("#modalClose_AddCategories").on("click", function () {
    $("#add-categories-modal").addClass("hidden");
  });

  $("#add-selected-categories").on("click", function () {
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    applyPromotionToSelectedCategories(promotionId);
  });

  $("#delete-selected-categories").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    removePromotionFromSelectedCategories(promotionId);
  });

  $("#actived-selected-categories").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    activeSelectedCategories(promotionId);
  });

  // Common checkbox click handler
  $(document).on("change", ".common-category-checkbox", function () {
    const promotionId = $("#modalToggle_Category_Apply").attr(
      "data-promotion-id"
    );
    const categoryId = $(this).val();
    const checkbox = $(this);
    if (this.checked) {
      checkCategoryInOtherPromotions(categoryId, promotionId, checkbox);
    }
  });

  // // Confirm modal button handlers
  // $("#confirmYes").on("click", function () {
  //   $("#confirmModal").addClass("hidden");
  // });

  // $("#cancelConfirm, #closeConfirmModal").on("click", function () {
  //   $("#confirmModal").addClass("hidden");
  //   $(".common-category-checkbox:checked").prop("checked", false);
  // });
});

function activeSelectedCategories(promotionId) {
  var selectedCategoryIds = [];
  $(".category-checkbox:checked").each(function () {
    selectedCategoryIds.push($(this).val());
  });

  if (selectedCategoryIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedCategoryIds,
        entityType: "CATEGORY",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchCategoriesByPromotion(promotionId);
          showNotification("Active successful", "OK");
        }
      },
      error: function (error) {
        console.error("Error activating selected categories:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one category to activate.",
      "Error"
    );
  }
}

function fetchCategoriesByPromotion(promotionId) {
  var categoryTableBody = $("#category-apply-promotion");
  categoryTableBody.empty(); // Clear existing rows
  $.ajax({
    url: `http://${apiurl}/promotion-generic/in-promotion/CATEGORY/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var categories = response.data;
      if (categories.length > 0 && response.status === "OK") {
        $("#notiBlankCategory").text("");
        categories.forEach(function (category) {
          const row = `
            <tr>
              <td class="px-6 py-3">${category.categoryDTO.id}</td>
              <td class="px-6 py-3">${category.categoryDTO.name}</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="category-checkbox common-category-checkbox" value="${
                  category.categoryDTO.id
                }">
              </td>
              <td class="px-6 py-3">${
                category.status ? "Active" : "Inactive"
              }</td>
            </tr>
          `;
          categoryTableBody.append(row);
        });
      } else if (response.status === "NOT_FOUND") {
        showNotification("No categories found for this promotion.", "Error");
      } else {
        $("#notiBlankCategory").text("No categories found for this promotion.");
      }
    },
    error: function (error) {
      console.error("Error fetching categories by promotion:", error);
    },
  });
}

function removePromotionFromSelectedCategories(promotionId) {
  var selectedCategoryIds = [];
  $(".category-checkbox:checked").each(function () {
    selectedCategoryIds.push($(this).val());
  });

  if (selectedCategoryIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/remove`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedCategoryIds,
        entityType: "CATEGORY",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchCategoriesByPromotion(promotionId);
          showNotification("Remove successful.", "OK");
        }
      },
      error: function (error) {
        console.error("Error removing selected categories:", error);
      },
    });
  } else {
    showNotification("Please select at least one category to delete.", "error");
  }
}

function fetchCategoriesNotInPromotion(promotionId) {
  var categoryTableBody = $("#category-not-apply-promotion");
  categoryTableBody.empty(); // Clear existing rows
  $.ajax({
    url: `http://${apiurl}/promotion-generic/not-in-promotion/CATEGORY/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var categories = response.data;
      if (categories.length > 0) {
        $("#notiBlankCategoryNotInPromotion").text("");
        categories.forEach(function (category) {
          const row = `
            <tr>
              <td class="px-6 py-3">${category.id}</td>
              <td class="px-6 py-3">${category.name}</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="category2-checkbox common-category-checkbox" value="${category.id}">
              </td>
            </tr>
          `;
          categoryTableBody.append(row);
        });
      } else {
        $("#notiBlankCategoryNotInPromotion").text(
          "No categories found not in this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching categories not in promotion:", error);
    },
  });
}

function applyPromotionToSelectedCategories(promotionId) {
  var selectedCategoryIds = [];
  $("#category-not-apply-promotion .category2-checkbox:checked").each(
    function () {
      selectedCategoryIds.push($(this).val());
    }
  );

  if (selectedCategoryIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedCategoryIds,
        entityType: "CATEGORY",
      }),
      success: function (response) {
        fetchCategoriesByPromotion(promotionId);
        showNotification(response.desc, "OK");
        $("#add-categories-modal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error applying selected categories:", error);
      },
    });
  } else {
    showNotification("Please select at least one category to add.", "error");
  }
}

function checkCategoryInOtherPromotions(categoryId, promotionId, checkbox) {
  $.ajax({
    url: `http://${apiurl}/promotion-generic/check/CATEGORY/${categoryId}/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "CONFLICT") {
        displayConflictModal(response.data, response.desc, checkbox);
      }
    },
    error: function (error) {
      console.error("Error checking category in other promotions:", error);
    },
  });
}
