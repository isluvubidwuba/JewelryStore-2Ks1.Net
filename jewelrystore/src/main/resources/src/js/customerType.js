import UserService from "./userService.js";
import { displayConflictModal } from "./forProduct.js";

const userService = new UserService();
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
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/in-promotion/CUSTOMER/${promotionId}`,
      "GET",
      null
    )
    .then((response) => {
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
    })
    .catch((error) => {
      console.error("Error fetching customers by promotion:", error);
    });
}

function fetchCustomersNotInPromotion(promotionId) {
  var customerTableBody = $("#customer-not-apply-voucher");
  customerTableBody.empty();
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/not-in-promotion/CUSTOMER/${promotionId}`,
      "GET",
      null
    )
    .then((response) => {
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
    })
    .catch((error) => {
      console.error("Error fetching customers not in promotion:", error);
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
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
        "POST",
        {
          promotionId: promotionId,
          entityIds: selectedCustomerIds,
          entityType: "CUSTOMER",
        }
      )
      .then((response) => {
        fetchCustomersByPromotion(promotionId);
        $("#add-customers-modal").addClass("hidden");
      })
      .catch((error) => {
        console.error("Error applying selected customers:", error);
      });
  } else {
    showNotification(
      "Please select at least one customer type to add.",
      "error"
    );
  }
}

function removePromotionFromSelectedCustomers(promotionId) {
  var selectedCustomerIds = [];
  $(".customer-checkbox:checked").each(function () {
    selectedCustomerIds.push($(this).val());
  });

  if (selectedCustomerIds.length > 0) {
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/promotion-generic/remove`,
        "POST",
        {
          promotionId: promotionId,
          entityIds: selectedCustomerIds,
          entityType: "CUSTOMER",
        }
      )
      .then((response) => {
        if (response.status === "OK") {
          fetchCustomersByPromotion(promotionId);
          showNotification(response.desc, "OK");
        }
      })
      .catch((error) => {
        console.error("Error removing selected customers:", error);
      });
  } else {
    showNotification(
      "Please select at least one customer type to delete.",
      "error"
    );
  }
}

function activateSelectedCustomers(promotionId) {
  var selectedCustomerIds = [];
  $(".customer-checkbox:checked").each(function () {
    selectedCustomerIds.push($(this).val());
  });

  if (selectedCustomerIds.length > 0) {
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/promotion-generic/apply`,
        "POST",
        {
          promotionId: promotionId,
          entityIds: selectedCustomerIds,
          entityType: "CUSTOMER",
        }
      )
      .then((response) => {
        if (response.status === "OK") {
          fetchCustomersByPromotion(promotionId);
          showNotification("Activate successful", "OK");
        }
      })
      .cath((error) => {
        console.error("Error activating selected customers:", error);
      });
  } else {
    showNotification(
      "Please select at least one customer type to activate.",
      "Error"
    );
  }
}

function checkCustomerInOtherPromotions(customerTypeId, promotionId, checkbox) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/promotion-generic/check/CUSTOMER/${customerTypeId}/${promotionId}`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "CONFLICT") {
        displayConflictModal(response.data, response.desc, checkbox);
      }
    })
    .catch((error) => {
      console.error("Error checking customer type in other promotions:", error);
    });
}
