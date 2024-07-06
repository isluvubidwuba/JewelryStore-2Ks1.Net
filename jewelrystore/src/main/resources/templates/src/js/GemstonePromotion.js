$(document).ready(function () {
  $(document).on("click", "#modalToggle_Gemstone_Apply", function () {
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    $("#detail-modal_GemstoneApply").removeClass("hidden").addClass("flex");
    fetchGemstonesByPromotion(promotionId);
  });

  $("#modalClose_GemstoneApply").on("click", function () {
    $("#detail-modal_GemstoneApply").addClass("hidden");
  });

  $("#add-gemstones").on("click", function () {
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    fetchGemstonesNotInPromotion(promotionId);
    $("#add-gemstones-modal").removeClass("hidden").addClass("flex");
  });

  $("#modalClose_AddGemstones").on("click", function () {
    $("#add-gemstones-modal").addClass("hidden");
  });

  $("#add-selected-gemstones").on("click", function () {
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    applyPromotionToSelectedGemstones(promotionId);
  });

  $("#delete-selected-gemstones").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    removePromotionFromSelectedGemstones(promotionId);
  });

  $("#actived-selected-gemstones").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    activateSelectedGemstones(promotionId);
  });

  $(document).on("change", ".common-gemstone-checkbox", function () {
    const promotionId = $("#modalToggle_Gemstone_Apply").attr(
      "data-promotion-id"
    );
    const gemStoneTypeId = $(this).val();
    const checkbox = $(this);
    if (this.checked) {
      checkGemstoneInOtherPromotions(gemStoneTypeId, promotionId, checkbox);
    }
  });
});

function fetchGemstonesByPromotion(promotionId) {
  var gemstoneTableBody = $("#gemstone-apply-promotion");
  gemstoneTableBody.empty();
  $.ajax({
    url: `http://${apiurl}/promotion-generic/in-promotion/GEMSTONE/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var gemstones = response.data;
      if (gemstones.length > 0 && response.status === "OK") {
        $("#notiBlankGemstone").text("");
        gemstones.forEach(function (gemstone) {
          const row = `
              <tr>
                <td class="px-6 py-3">${gemstone.gemStoneTypeDTO.id}</td>
                <td class="px-6 py-3">${gemstone.gemStoneTypeDTO.name}</td>
                <td class="px-6 py-3">
                  <input type="checkbox" class="gemstone-checkbox common-gemstone-checkbox" value="${
                    gemstone.gemStoneTypeDTO.id
                  }">
                </td>
                <td class="px-6 py-3">${
                  gemstone.status ? "Active" : "Inactive"
                }</td>
              </tr>
            `;
          gemstoneTableBody.append(row);
        });
      } else {
        $("#notiBlankGemstone").text(
          "No gemstone types found for this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching gemstones by promotion:", error);
    },
  });
}

function fetchGemstonesNotInPromotion(promotionId) {
  var gemstoneTableBody = $("#gemstone-not-apply-promotion");
  gemstoneTableBody.empty();
  $.ajax({
    url: `http://${apiurl}/promotion-generic/not-in-promotion/GEMSTONE/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var gemstones = response.data;
      if (gemstones.length > 0) {
        $("#notiBlankGemstoneNotInPromotion").text("");
        gemstones.forEach(function (gemstone) {
          const row = `
              <tr>
                <td class="px-6 py-3">${gemstone.id}</td>
                <td class="px-6 py-3">${gemstone.name}</td>
                <td class="px-6 py-3">
                  <input type="checkbox" class="gemstone2-checkbox common-gemstone-checkbox" value="${gemstone.id}">
                </td>
              </tr>
            `;
          gemstoneTableBody.append(row);
        });
      } else {
        $("#notiBlankGemstoneNotInPromotion").text(
          "No gemstone types found not in this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching gemstones not in promotion:", error);
    },
  });
}

function applyPromotionToSelectedGemstones(promotionId) {
  var selectedGemstoneIds = [];
  $("#gemstone-not-apply-promotion .gemstone2-checkbox:checked").each(
    function () {
      selectedGemstoneIds.push($(this).val());
    }
  );

  if (selectedGemstoneIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedGemstoneIds,
        entityType: "GEMSTONE",
      }),
      success: function (response) {
        fetchGemstonesByPromotion(promotionId);
        $("#add-gemstones-modal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error applying selected gemstones:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one gemstone type to add.",
      "Error"
    );
  }
}

function removePromotionFromSelectedGemstones(promotionId) {
  var selectedGemstoneIds = [];
  $(".gemstone-checkbox:checked").each(function () {
    selectedGemstoneIds.push($(this).val());
  });

  if (selectedGemstoneIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/remove`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedGemstoneIds,
        entityType: "GEMSTONE",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchGemstonesByPromotion(promotionId);
          showNotification(response.desc, "Error");
        }
      },
      error: function (error) {
        console.error("Error removing selected gemstones:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one gemstone type to activate.",
      "Error"
    );
  }
}

function activateSelectedGemstones(promotionId) {
  var selectedGemstoneIds = [];
  $(".gemstone-checkbox:checked").each(function () {
    selectedGemstoneIds.push($(this).val());
  });

  if (selectedGemstoneIds.length > 0) {
    $.ajax({
      url: `http://${apiurl}/promotion-generic/apply`,
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        entityIds: selectedGemstoneIds,
        entityType: "GEMSTONE",
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchGemstonesByPromotion(promotionId);
          showNotification("Activate successful.", "OK");
        }
      },
      error: function (error) {
        console.error("Error activating selected gemstones:", error);
      },
    });
  } else {
    showNotification(
      "Please select at least one gemstone type to activate.",
      "Error"
    );
  }
}

function checkGemstoneInOtherPromotions(gemStoneTypeId, promotionId, checkbox) {
  $.ajax({
    url: `http://${apiurl}/promotion-generic/check/GEMSTONE/${gemStoneTypeId}/${promotionId}`,
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
      console.error("Error checking gemstone type in other promotions:", error);
    },
  });
}
