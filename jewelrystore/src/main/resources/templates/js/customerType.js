$(document).ready(function () {
  $(document).on("click", "#modalToggle_Customer_Apply", function () {
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    $("#detail-modal_CustomerApply").removeClass("hidden").addClass("flex");
    fetchCustomersByPromotion(promotionId);
  });

  $("#modalClose_CustomerApply").on("click", function () {
    $("#detail-modal_CustomerApply").addClass("hidden");
  });

  $("#add-customers").on("click", function () {
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    fetchCustomersNotInPromotion(promotionId);
    $("#add-customers-modal").removeClass("hidden").addClass("flex");
  });

  $("#modalClose_AddCustomers").on("click", function () {
    $("#add-customers-modal").addClass("hidden");
  });

  $("#add-selected-customers").on("click", function () {
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    applyPromotionToSelectedCustomers(promotionId);
  });

  $("#delete-selected-customers").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    removePromotionFromSelectedCustomers(promotionId);
  });

  $("#actived-selected-customers").on("click", function (event) {
    event.preventDefault();
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    activateSelectedCustomers(promotionId);
  });

  $(document).on("change", ".common-customer-checkbox", function () {
    const promotionId = $("#modalToggle_Customer_Apply").attr(
      "data-promotion-id"
    );
    const customerTypeId = $(this).val();
    const checkbox = $(this);
    if (this.checked) {
      checkCustomerInOtherPromotions(customerTypeId, promotionId, checkbox);
    }
  });
});

function fetchCustomersByPromotion(promotionId) {
  var customerTableBody = $("#customer-apply-promotion");
  customerTableBody.empty();
  $.ajax({
    url: `http://localhost:8080/promotion-for-customer/promotion/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var customers = response.data;
      if (customers.length > 0 && response.status === "OK") {
        $("#notiBlankCustomer").text("");
        customers.forEach(function (customer) {
          const row = `
            <tr>
              <td class="px-6 py-3">${customer.customerTypeDTO.id}</td>
              <td class="px-6 py-3">${customer.customerTypeDTO.type}</td>
              <td class="px-6 py-3">${
                customer.customerTypeDTO.pointCondition
              }</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="customer-checkbox common-customer-checkbox" value="${
                  customer.customerTypeDTO.id
                }">
              </td>
              <td class="px-6 py-3">${
                customer.status ? "Active" : "Inactive"
              }</td>
            </tr>
          `;
          customerTableBody.append(row);
        });
      } else {
        $("#notiBlankCustomer").text(
          "No customer types found for this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching customers by promotion:", error);
    },
  });
}

function fetchCustomersNotInPromotion(promotionId) {
  var customerTableBody = $("#customer-not-apply-voucher");
  customerTableBody.empty();
  $.ajax({
    url: `http://localhost:8080/promotion-for-customer/not-in-promotion/${promotionId}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      var customers = response.data;
      if (customers.length > 0) {
        $("#notiBlankCustomerNotInVoucher").text("");
        customers.forEach(function (customer) {
          const row = `
            <tr>
              <td class="px-6 py-3">${customer.id}</td>
              <td class="px-6 py-3">${customer.type}</td>
              <td class="px-6 py-3">${customer.pointCondition}</td>
              <td class="px-6 py-3">
                <input type="checkbox" class="customer2-checkbox common-customer-checkbox" value="${customer.id}">
              </td>
            </tr>
          `;
          customerTableBody.append(row);
        });
      } else {
        $("#notiBlankCustomerNotInVoucher").text(
          "No customer types found not in this promotion."
        );
      }
    },
    error: function (error) {
      console.error("Error fetching customers not in promotion:", error);
    },
  });
}

function applyPromotionToSelectedCustomers(promotionId) {
  var selectedCustomerIds = [];
  $("#customer-not-apply-voucher .customer2-checkbox:checked").each(
    function () {
      selectedCustomerIds.push($(this).val());
    }
  );

  if (selectedCustomerIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/promotion-for-customer/apply-promotion",
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        productIds: selectedCustomerIds,
      }),
      success: function (response) {
        fetchCustomersByPromotion(promotionId);
        $("#add-customers-modal").addClass("hidden");
      },
      error: function (error) {
        console.error("Error applying selected customers:", error);
      },
    });
  } else {
    alert("Please select at least one customer type to add.");
  }
}

function removePromotionFromSelectedCustomers(promotionId) {
  var selectedCustomerIds = [];
  $(".customer-checkbox:checked").each(function () {
    selectedCustomerIds.push($(this).val());
  });

  if (selectedCustomerIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/promotion-for-customer/remove-promotion",
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        productIds: selectedCustomerIds,
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchCustomersByPromotion(promotionId);
          alert("Remove successful");
        }
      },
      error: function (error) {
        console.error("Error removing selected customers:", error);
      },
    });
  } else {
    alert("Please select at least one customer type to delete.");
  }
}

function activateSelectedCustomers(promotionId) {
  var selectedCustomerIds = [];
  $(".customer-checkbox:checked").each(function () {
    selectedCustomerIds.push($(this).val());
  });

  if (selectedCustomerIds.length > 0) {
    $.ajax({
      url: "http://localhost:8080/promotion-for-customer/apply-promotion",
      type: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        promotionId: promotionId,
        productIds: selectedCustomerIds,
      }),
      success: function (response) {
        if (response.status === "OK") {
          fetchCustomersByPromotion(promotionId);
          alert("Activate successful");
        }
      },
      error: function (error) {
        console.error("Error activating selected customers:", error);
      },
    });
  } else {
    alert("Please select at least one customer type to activate.");
  }
}

function checkCustomerInOtherPromotions(customerTypeId, promotionId, checkbox) {
  $.ajax({
    url: `http://localhost:8080/promotion-for-customer/check-customer/${customerTypeId}/${promotionId}`,
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
      console.error("Error checking customer type in other promotions:", error);
    },
  });
}
