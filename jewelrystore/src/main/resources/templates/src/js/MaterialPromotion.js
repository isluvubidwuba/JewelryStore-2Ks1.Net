import UserService from "./userService.js";

const userService = new UserService();
$(document).ready(function () {
  $(document).on("click", "#modalToggle_Material_Apply", function () {
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    $("#detail-modal_MaterialApply").removeClass("hidden").addClass("flex");
    fetchMaterialsByPromotion(promotionId);
  });

  $("#modalClose_MaterialApply").on("click", function () {
    $("#detail-modal_MaterialApply").addClass("hidden");
  });

  $("#add-materials").on("click", function () {
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    fetchMaterialsNotInPromotion(promotionId);
    $("#add-materials-modal").removeClass("hidden").addClass("flex");
  });

  $("#modalClose_AddMaterials").on("click", function () {
    $("#add-materials-modal").addClass("hidden");
  });

  $("#add-selected-materials").on("click", function () {
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    applyPromotionToSelectedMaterials(promotionId);
  });

  $("#delete-selected-materials").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    removePromotionFromSelectedMaterials(promotionId);
  });

  $("#actived-selected-materials").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    activateSelectedMaterials(promotionId);
  });

  $(document).on("change", ".common-material-checkbox", function () {
    const promotionId = $("#modalToggle_Material_Apply").attr(
      "data-promotion-id"
    );
    const materialId = $(this).val();
    const checkbox = $(this);
    if (this.checked) {
      checkMaterialInOtherPromotions(materialId, promotionId, checkbox);
    }
  });
});

function fetchMaterialsByPromotion(promotionId) {
  var materialTableBody = $("#material-apply-promotion");
  materialTableBody.empty();
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/promotion-generic/in-promotion/MATERIAL/${promotionId}`,
    "GET",
    function (response) {
      var materials = response.data;
      if (materials.length > 0 && response.status === "OK") {
        $("#notiBlankMaterial").text("");
        materials.forEach(function (material) {
          const row = `
            <tr>
              <td class="px-6 py-3">${material.materialDTO.id}</td>
              <td class="px-6 py-3">${material.materialDTO.name}</td>
              <td class="px-6 py-3">${material.materialDTO.purity}</td>
              <td class="px-6 py-3">${material.materialDTO.priceAtTime}</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="material-checkbox common-material-checkbox" value="${
                  material.materialDTO.id
                }">
              </td>
              <td class="px-6 py-3">${
                material.status ? "Active" : "Inactive"
              }</td>
            </tr>
          `;
          materialTableBody.append(row);
        });
      } else {
        $("#notiBlankMaterial").text("No materials found for this promotion.");
      }
    },
    function (error) {
      console.error("Error fetching materials by promotion:", error);
    },
    null
  );
}

function fetchMaterialsNotInPromotion(promotionId) {
  var materialTableBody = $("#material-not-apply-promotion");
  materialTableBody.empty();
  $.ajax({
    url: `http://${userService.getApiUrl()}/api/promotion-generic/not-in-promotion/MATERIAL/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var materials = response.data;
      if (materials.length > 0) {
        $("#notiBlankMaterialNotInPromotion").text("");
        materials.forEach(function (material) {
          const row = `
              <tr>
                <td class="px-6 py-3">${material.id}</td>
                <td class="px-6 py-3">${material.name}</td>
                <td class="px-6 py-3">${material.purity}</td>
                <td class="px-6 py-3">${material.priceAtTime}</td>
                <td class="px-6 py-3">
                  <input type="checkbox" class="material2-checkbox common-material-checkbox" value="${material.id}">
                </td>
              </tr>
            `;
          materialTableBody.append(row);
        });
      } else {
        $("#notiBlankMaterialNotInPromotion").text(
          "No materials found not in this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching materials not in promotion:", error);
    },
  });
}

function applyPromotionToSelectedMaterials(promotionId) {
  var selectedMaterialIds = [];
  $("#material-not-apply-promotion .material2-checkbox:checked").each(
    function () {
      selectedMaterialIds.push($(this).val());
    }
  );

  if (selectedMaterialIds.length > 0) {
    $.ajax({
      url: `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedMaterialIds,
        entityType: "MATERIAL",
      }),
      success: function (response) {
        fetchMaterialsByPromotion(promotionId);
        $("#add-materials-modal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error applying selected materials:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one material type to add.",
      "Error"
    );
  }
}

function removePromotionFromSelectedMaterials(promotionId) {
  var selectedMaterialIds = [];
  $(".material-checkbox:checked").each(function () {
    selectedMaterialIds.push($(this).val());
  });

  if (selectedMaterialIds.length > 0) {
    $.ajax({
      url: `http://${userService.getApiUrl()}/api/promotion-generic/remove`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedMaterialIds,
        entityType: "MATERIAL",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchMaterialsByPromotion(promotionId);
          showNotification(response.desc, "Error");
        }
      },
      error: function (error) {
        console.error("Error removing selected materials:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one material type to delete.",
      "Error"
    );
  }
}

function activateSelectedMaterials(promotionId) {
  var selectedMaterialIds = [];
  $(".material-checkbox:checked").each(function () {
    selectedMaterialIds.push($(this).val());
  });

  if (selectedMaterialIds.length > 0) {
    $.ajax({
      url: `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedMaterialIds,
        entityType: "MATERIAL",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchMaterialsByPromotion(promotionId);
          showNotification("Activate successful.", "Error");
        }
      },
      error: function (error) {
        console.error("Error activating selected materials:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one material type to activate.",
      "Error"
    );
  }
}

function checkMaterialInOtherPromotions(materialId, promotionId, checkbox) {
  $.ajax({
    url: `http://${userService.getApiUrl()}/api/promotion-generic/check/MATERIAL/${materialId}/${promotionId}`,
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
      console.error("Error checking material type in other promotions:", error);
    },
  });
}
