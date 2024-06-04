$(document).ready(function () {
  // Function to fetch and display voucher types

  // Show the voucher type modal when the button is clicked
  $("#modalToggle-voucherType").click(function () {
    fetchVoucherTypes();
    $("#voucherTypeModal").removeClass("hidden");
  });

  // Hide the voucher type modal when the close button is clicked
  $("#closeVoucherTypeModal").click(function () {
    $("#voucherTypeModal").addClass("hidden");
  });

  // Hide the update invoice type modal when the close button is clicked
  $("#closeUpdateInvoiceTypeModal").click(function () {
    $("#updateInvoiceTypeModal").addClass("hidden");
  });

  // Hide the add voucher type modal when the close button is clicked
  $("#closeAddVoucherTypeModal").click(function () {
    $("#addVoucherTypeModal").addClass("hidden");
  });

  // Handle form submission for updating invoice type
  $("#updateInvoiceTypeForm").submit(function (e) {
    e.preventDefault();
    var invoiceTypeId = $("#invoiceTypeId").val();
    var invoiceTypeName = $("#invoiceTypeName").val();

    // Submit the form via AJAX (assuming an update endpoint exists)
    $.ajax({
      url: `http://localhost:8080/voucher/update`,
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: {
        id: invoiceTypeId,
        type: invoiceTypeName,
      },
      success: function (response) {
        alert("Invoice type updated successfully!");
        $("#updateInvoiceTypeModal").addClass("hidden");
        fetchVoucherTypes(); // Refresh voucher types without reloading the page
      },
      error: function () {
        alert("Failed to update invoice type");
      },
    });
  });

  // Handle form submission for adding a new voucher type
  $("#addVoucherTypeForm").submit(function (e) {
    e.preventDefault();
    var newVoucherTypeName = $("#newVoucherTypeName").val();
    console.log(newVoucherTypeName);
    // Submit the form via AJAX
    $.ajax({
      url: "http://localhost:8080/voucher/create",
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      contentType: "application/json",
      data: JSON.stringify({
        type: newVoucherTypeName,
      }),
      success: function (response) {
        alert("Voucher type added successfully!");
        $("#addVoucherTypeModal").addClass("hidden");
        clearFormFields("#addVoucherTypeForm");
        fetchVoucherTypes(); // Refresh voucher types without reloading the page
      },
      error: function () {
        alert("Failed to add voucher type");
      },
    });
  });
});
function fetchVoucherTypes() {
  $.ajax({
    url: "http://localhost:8080/voucher/list",
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.data) {
        var voucherTypesContainer = $("#voucherTypesContainer");
        voucherTypesContainer.empty(); // Clear previous data
        $.each(response.data, function (index, voucher) {
          voucherTypesContainer.append(`
            <label class="flex items-center px-4 py-2 text-sm text-gray-700" id="label-${voucher.id}">
              <span class="option-name">${voucher.type}</span>
              <button id="modalVoucherType-${voucher.id}" data-option-id="${voucher.id}" class="text-gray-500 hover:text-gray-700 ml-auto">
                <svg class="svg-inline--fa fa-edit fa-w-18" aria-hidden="true" focusable="false" data-prefix="fas" data-icon="edit" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512" data-fa-i2svg="">
                  <path fill="currentColor" d="M402.6 83.2l90.2 90.2c3.8 3.8 3.8 10 0 13.8L274.4 405.6l-92.8 10.3c-12.4 1.4-22.9-9.1-21.5-21.5l10.3-92.8L388.8 83.2c3.8-3.8 10-3.8 13.8 0zm162-22.9l-48.8-48.8c-15.2-15.2-39.9-15.2-55.2 0l-35.4 35.4c-3.8 3.8-3.8 10 0 13.8l90.2 90.2c3.8 3.8 10 3.8 13.8 0l35.4-35.4c15.2-15.3 15.2-40 0-55.2zM384 346.2V448H64V128h229.8c3.2 0 6.2-1.3 8.5-3.5l40-40c7.6-7.6 2.2-20.5-8.5-20.5H48C21.5 64 0 85.5 0 112v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V306.2c0-10.7-12.9-16-20.5-8.5l-40 40c-2.2 2.3-3.5 5.3-3.5 8.5z"></path>
                </svg>
              </button>
            </label>
          `);
        });
        voucherTypesContainer.append(`
          <label class="flex items-center px-4 py-2 text-sm text-gray-700">
            <span class="option-name">Add more</span>
            <button id="addMoreVoucher" class="text-gray-500 hover:text-gray-700 ml-auto">
              <svg class="svg-inline--fa fa-edit fa-w-18" aria-hidden="true" focusable="false" data-prefix="fas" data-icon="edit" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512" data-fa-i2svg="">
                <path fill="currentColor" d="M402.6 83.2l90.2 90.2c3.8 3.8 3.8 10 0 13.8L274.4 405.6l-92.8 10.3c-12.4 1.4-22.9-9.1-21.5-21.5l10.3-92.8L388.8 83.2c3.8-3.8 10-3.8 13.8 0zm162-22.9l-48.8-48.8c-15.2-15.2-39.9-15.2-55.2 0l-35.4 35.4c-3.8 3.8-3.8 10 0 13.8l90.2 90.2c3.8 3.8 10 3.8 13.8 0l35.4-35.4c15.2-15.3 15.2-40 0-55.2zM384 346.2V448H64V128h229.8c3.2 0 6.2-1.3 8.5-3.5l40-40c7.6-7.6 2.2-20.5-8.5-20.5H48C21.5 64 0 85.5 0 112v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V306.2c0-10.7-12.9-16-20.5-8.5l-40 40c-2.2 2.3-3.5 5.3-3.5 8.5z"></path>
              </svg>
            </button>
          </label>
        `);

        // Bind click event to dynamically added buttons
        $("[id^=modalVoucherType-]").click(function () {
          var voucherId = $(this).data("option-id");
          var voucherType = $(this).siblings(".option-name").text();
          $("#invoiceTypeId").val(voucherId);
          $("#invoiceTypeName").val(voucherType);
          $("#updateInvoiceTypeModal").removeClass("hidden");
        });

        // Bind click event to add more voucher button
        $("#addMoreVoucher").click(function () {
          $("#addVoucherTypeModal").removeClass("hidden");
        });
      }
    },
    error: function () {
      alert("Failed to fetch voucher types");
    },
  });
}
function clearFormFields(formId) {
  $(formId).find("input[type=text], input[type=hidden]").val("");
}
