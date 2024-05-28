const rowsLimit = 6;
let currentPage = 0;
let totalPage = 0;
let policies = [];

function fetchPolicies() {
  $.ajax({
    url: "http://localhost:8080/policy/listpolicy",
    method: "GET",
    success: function (response) {
      if (response.status === "OK") {
        policies = response.data;
        totalPage = Math.ceil(policies.length / rowsLimit);
        displayTableData(currentPage);
        updatePagination(totalPage, currentPage);
        updatePolicyOverview(policies);
      } else {
        alert("Failed to fetch data");
      }
    },
    error: function () {
      alert("Error fetching data");
    },
  });
}
// Function to search and display policies based on keyword
function searchPolicies(keyword) {
  $.ajax({
    url: "http://localhost:8080/policy/searhExchangeRate",
    method: "POST",
    data: { keyword: keyword },
    success: function (response) {
      if (response.status === "OK") {
        policies = response.data;
        totalPage = Math.ceil(policies.length / rowsLimit);
        displayTableData(currentPage);
        updatePagination(totalPage, currentPage);
        updatePolicyOverview(policies);
        console.log(policies);
      } else {
        alert("Failed to fetch data");
      }
    },
    error: function () {
      alert("Error fetching data");
    },
  });
}

function updatePolicyOverview(policies) {
  const totalPolicies = policies.length;
  const activePolicies = policies.filter((policy) => policy.status).length;
  const inactivePolicies = totalPolicies - activePolicies;

  $("#totalPolicies").text(totalPolicies);
  $("#activePolicies").text(activePolicies);
  $("#inactivePolicies").text(inactivePolicies);
}

function displayTableData(page) {
  const startIndex = page * rowsLimit;
  const endIndex = startIndex + rowsLimit;
  const rowsToShow = policies.slice(startIndex, endIndex);

  $("#table-body").empty();
  rowsToShow.forEach((data, index) => {
    $("#table-body").append(`
            <tr class="${
              index % 2 === 0 ? "bg-gray-100" : "bg-white"
            }" data-id="${data.id}">
                <td class="text-left py-3 px-4">${data.id}</td>
                <td class="text-left py-3 px-4">${data.description_policy}</td>
                <td class="text-left py-3 px-4">${data.rate}</td>
                <td class="text-left py-3 px-4">${
                  data.status ? "Active" : "Inactive"
                }</td>
                <td class="text-left py-3 px-4">${new Date(
                  data.lastModified
                ).toLocaleDateString()}</td>
                <td class="text-left py-3 px-4">
                    <button class="text-blue-500 text-xs" data-id="${data.id}">
                    <svg class="text-themeColor-500 w-10 h-10"
                    xmlns="http://www.w3.org/2000/svg" width="24" height="24"  fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                    
                    Edit</button>
                    <button class="text-red-500 text-xs" data-id="${data.id}">
                    <svg class="text-themeColor-500 w-10 h-10"
                    xmlns="http://www.w3.org/2000/svg" width="24"  height="24"  viewBox="0 0 24 24"  fill="none"  stroke="currentColor"  stroke-width="2"  stroke-linecap="round"  stroke-linejoin="round">  <path d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z" />  <line x1="18" y1="9" x2="12" y2="15" />  <line x1="12" y1="9" x2="18" y2="15" /></svg>
                    Delete</button>
                    <button class="text-amber-900 text-xs" id="bttn-detail" data-id="${
                      data.id
                    }">
                    <svg class="text-themeColor-500 w-10 h-10"
                    xmlns="http://www.w3.org/2000/svg" width="24" height="24"  fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4"/>
                    </svg>
                    
                    Detail</button>
                </td>
            </tr>
        `);
  });

  $("#entries-info").text(
    `Showing ${startIndex + 1} to ${
      endIndex > policies.length ? policies.length : endIndex
    } of ${policies.length} entries`
  );
}

function updatePagination(totalPages, currentPage) {
  let pagination = $(".pagination");

  pagination.empty();

  if (currentPage > 0) {
    pagination.append(`
            <li>
                <span
                    tabindex="0"
                    aria-label="backward"
                    role="button"
                    class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 p-1 flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 focus:outline-none mr-1 sm:mr-3"
                    onclick="fetchPage(${currentPage - 1})"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z"/>
                        <polyline points="15 6 9 12 15 18" />
                    </svg>
                </span>
            </li>
        `);
  }

  for (let i = 0; i < totalPages; i++) {
    pagination.append(`
            <li>
                <span
                    tabindex="0"
                    class="focus:outline-none focus:bg-indigo-700 focus:text-white flex text-indigo-700 dark:text-indigo-400 ${
                      i === currentPage
                        ? "bg-gray-400 text-white"
                        : "bg-white dark:bg-gray-700 hover:bg-indigo-600 hover:text-white"
                    } ${
      i < currentPage - 1 || i > currentPage + 1 ? "hidden" : " "
    } text-base leading-tight font-bold cursor-pointer shadow transition duration-150 ease-in-out mx-2 sm:mx-4 rounded px-3 py-2"
                    onclick="fetchPage(${i})"
                >
                    ${i + 1}
                </span>
            </li>
        `);
  }

  if (currentPage < totalPages - 1) {
    pagination.append(`
            <li>
                <span
                    tabindex="0"
                    aria-label="forward"
                    role="button"
                    class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 focus:outline-none flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 p-1 focus:outline-none ml-1 sm:ml-3"
                    onclick="fetchPage(${currentPage + 1})"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z"/>
                        <polyline points="9 6 15 12 9 18" />
                    </svg>
                </span>
            </li>
        `);
  }
}

function fetchPage(page) {
  currentPage = page;
  displayTableData(currentPage);
  updatePagination(totalPage, currentPage);
}

function loadDropdownOptions(idExchangeRate, button) {
  $.ajax({
    url: `http://localhost:8080/policy/detail?idExchangeRate=${idExchangeRate}`,
    method: "POST",
    success: function (response) {
      if (response.status === "OK") {
        populateDropdown(
          response.data.fullOption,
          response.data.selectOption,
          idExchangeRate
        );
      } else {
        alert("Failed to fetch options");
      }
    },
    error: function () {
      alert("Error fetching options");
    },
  });
}

function populateDropdown(fullOptions, selectedOptions, idExchangeRate) {
  const dropdownContent = $("<div>", { class: "py-1" });

  // Add the header
  dropdownContent.append(`
    <div class="px-4 py-2 text-center font-bold text-lg flex justify-between items-center">
      APPLIED FOR
      <button id="closeButton" class="text-gray-500 hover:text-gray-700">
        <svg class="h-6 w-6" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd"
            d="M10 8.586L15.657 2.93a1 1 0 011.414 1.414L11.414 10l5.657 5.657a1 1 0 01-1.414 1.414L10 11.414l-5.657 5.657a1 1 0 01-1.414 1.414L8.586 10 2.93 4.343A1 1 0 014.343 2.93L10 8.586z"
            clip-rule="evenodd" />
        </svg>
      </button>
    </div>
  `);

  fullOptions.forEach((option) => {
    const isChecked = selectedOptions.some(
      (selected) => selected.id === option.id
    );
    const optionElement = `
      <label class="flex items-center px-4 py-2 text-sm text-gray-700" id="label-${
        option.id
      }">
        <input type="checkbox" class="mr-2" name="options" value="${
          option.id
        }" ${isChecked ? "checked" : ""} />
        <span class="option-name">${option.name}</span>
        <button id="modal-updateInvoiceType-${option.id}" data-option-id="${
      option.id
    }" class="text-gray-500 hover:text-gray-700 ml-auto">
    <svg class="svg-inline--fa fa-edit fa-w-18" aria-hidden="true" focusable="false" data-prefix="fas" data-icon="edit" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512" data-fa-i2svg=""><path fill="currentColor" d="M402.6 83.2l90.2 90.2c3.8 3.8 3.8 10 0 13.8L274.4 405.6l-92.8 10.3c-12.4 1.4-22.9-9.1-21.5-21.5l10.3-92.8L388.8 83.2c3.8-3.8 10-3.8 13.8 0zm162-22.9l-48.8-48.8c-15.2-15.2-39.9-15.2-55.2 0l-35.4 35.4c-3.8 3.8-3.8 10 0 13.8l90.2 90.2c3.8 3.8 10 3.8 13.8 0l35.4-35.4c15.2-15.3 15.2-40 0-55.2zM384 346.2V448H64V128h229.8c3.2 0 6.2-1.3 8.5-3.5l40-40c7.6-7.6 2.2-20.5-8.5-20.5H48C21.5 64 0 85.5 0 112v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V306.2c0-10.7-12.9-16-20.5-8.5l-40 40c-2.2 2.3-3.5 5.3-3.5 8.5z"></path></svg>
        </button>
      </label>
    `;
    dropdownContent.append(optionElement);
  });

  dropdownContent.append(`
    <button
      id="applyButton"
      data-id="${idExchangeRate}"
      class="w-full px-4 py-2 bg-indigo-600 text-white text-sm font-medium hover:bg-indigo-700 focus:outline-none"
    >
      Apply
    </button>
  `);

  // Remove existing dropdowns
  $(".dynamic-dropdown").remove();

  // Create a new dropdown
  const dropdownMenu = $("<div>", {
    class:
      "fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50 hidden dynamic-dropdown",
  }).append(
    $("<div>", {
      class:
        "relative p-4 w-full max-w-md max-h-full bg-white rounded-lg shadow",
    }).append(dropdownContent)
  );

  // Append to the body
  $("body").append(dropdownMenu);

  // Show the dropdown
  dropdownMenu.removeClass("hidden");

  // Close button functionality
  $("#closeButton").click(function () {
    dropdownMenu.addClass("hidden");
  });
}

$(document).on("click", "#bttn-detail", function () {
  const idExchangeRate = $(this).data("id");
  loadDropdownOptions(idExchangeRate);
});

// Function to open the modal
function openInvoiceTypeModal() {
  $("#addInvoiceTypeModal").removeClass("hidden");
}

// Function to close the modal
function closeInvoiceTypeModal() {
  $("#addInvoiceTypeModal").addClass("hidden");
}

// Initial fetch
$(document).ready(function () {
  fetchPolicies();
  $("#exchange-search-button").click(function () {
    const keyword = $("#keyword").val();
    searchPolicies(keyword);
  });

  $(document).on("click", "#bttn-detail", function () {
    const idExchangeRate = $(this).data("id");
    loadDropdownOptions(idExchangeRate);
  });

  const insertModal = $("#insert-modal");
  const updateModal = $("#update-modal");

  const modalInsertCloseButton = $("#modalInsertClose");
  const modalInsertOpenButton = $("#modalOpen");
  const modalUpdateCloseButton = $("#modalUpdateClose");

  modalInsertOpenButton.click(function () {
    insertModal.removeClass("hidden");
  });

  modalInsertCloseButton.click(function () {
    insertModal.addClass("hidden");
  });

  modalUpdateCloseButton.click(function () {
    updateModal.addClass("hidden");
  });
  // Event listener to open the modal
  $(document).on("click", "#open-invoice-type-modal", function () {
    openInvoiceTypeModal();
  });

  // Event listener to close the modal
  $(document).on("click", "#close-invoice-type-modal", function () {
    closeInvoiceTypeModal();
  });

  //update exchange rate
  $(document).on("click", ".text-blue-500", function () {
    const idExchangeRate = $(this).data("id");

    $.ajax({
      url: `http://localhost:8080/policy/infor?idExchangeRate=${idExchangeRate}`,
      type: "POST",
      success: function (response) {
        if (response.status === "OK") {
          const data = response.data;
          $("#update-idExchange").val(data.id);
          $("#update-desc").val(data.description_policy);
          $("#update-rate").val(data.rate);
          $("#update-status").val(data.status ? "true" : "false");

          updateModal.removeClass("hidden");
        } else {
          alert("Failed to fetch exchange rate policy details");
        }
      },
      error: function (xhr, status, error) {
        alert(
          "An error occurred while fetching the exchange rate policy details."
        );
        console.log(xhr.responseText);
      },
    });
  });

  //submit form update
  $("#form-update").submit(function (event) {
    event.preventDefault();

    const formData = new FormData($("#form-update")[0]);
    const idExchange = formData.get("idExchange");

    $.ajax({
      url: "http://localhost:8080/policy/updateexchange",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        if (response.status === "OK") {
          // const updatedRow = $(`#table-body tr[data-id='${idExchange}']`);
          // updatedRow.find("td:eq(1)").text(response.data.description_policy);
          // updatedRow.find("td:eq(2)").text(response.data.rate);
          // updatedRow
          //   .find("td:eq(3)")
          //   .text(response.data.status ? "Active" : "Inactive");
          // updatedRow
          //   .find("td:eq(4)")
          //   .text(new Date(response.data.lastModified).toLocaleDateString());

          // $("#form-update").find("input, select").val("");
          fetchPolicies();
          updateModal.addClass("hidden");
          alert(response.desc);
        } else {
          alert("Failed to update exchange rate policy");
        }
      },
      error: function (xhr, status, error) {
        alert("An error occurred while updating the exchange rate policy.");
        console.log(xhr.responseText);
      },
    });
  });

  //submit delete
  $(document).on("click", ".text-red-500", function () {
    const idExchangeRate = $(this).data("id");

    if (confirm("Are you sure you want to delete this policy?")) {
      $.ajax({
        url: `http://localhost:8080/policy/deleteexchange?idExchange=${idExchangeRate}`,
        type: "POST",
        success: function (response) {
          if (response.status === "OK") {
            $(`#table-body tr[data-id='${idExchangeRate}']`).remove();
            alert("Policy deleted successfully");
            fetchPolicies();
          } else {
            alert("Failed to delete exchange rate policy");
          }
        },
        error: function (xhr, status, error) {
          alert("An error occurred while deleting the exchange rate policy.");
          console.log(xhr.responseText);
        },
      });
    }
  });

  //submit apply options
  $(document).on("click", "#applyButton", function () {
    const selectedOptions = [];
    $(".dynamic-dropdown")
      .find("input[type='checkbox']:checked")
      .each(function () {
        selectedOptions.push($(this).val());
      });

    const idExchangeRate = $(this).data("id"); // Keep as string

    console.log(selectedOptions, idExchangeRate);

    $.ajax({
      url: "http://localhost:8080/policy/applySelectedOptions",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ idExchangeRate, selectedOptions }), // Include idExchangeRate
      success: function (response) {
        if (response.status === "OK") {
          alert("Options applied successfully");
          $(".dynamic-dropdown").addClass("hidden");
        } else {
          alert("Failed to apply options");
        }
      },
      error: function () {
        alert("Error applying options");
      },
    });
  });

  // Event listener for form submission
  $(document).on("submit", "#add-invoice-type-form", function (event) {
    event.preventDefault();
    const invoiceTypeData = {
      invoiceType: $("#invoice-type").val(),
    };

    $.ajax({
      url: "http://localhost:8080/policy/addinvoicetype",
      type: "POST",
      data: invoiceTypeData,
      success: function (response) {
        if (response.status === "OK") {
          alert("Add invoice type successful!");
          closeInvoiceTypeModal();
          $("#add-invoice-type-form")[0].reset();
        } else {
          alert("Error: " + response.desc);
        }
      },
      error: function (error) {
        console.error("Error:", error);
        alert("An error occurred while adding the invoice type.");
      },
    });
  });

  //update invoice type
  // Function to open the update modal invoice type
  function openUpdateInvoiceTypeModal(id, name) {
    $("#invoice-type-id").val(id);
    $("#invoice-type-name").val(name);
    $("#updateInvoiceTypeModal").removeClass("hidden");
    $(".dynamic-dropdown").addClass("hidden");
  }

  // Function to close the update modal invoice type
  function closeUpdateInvoiceTypeModal() {
    $("#updateInvoiceTypeModal").addClass("hidden");
    $(".dynamic-dropdown").removeClass("hidden");
  }

  // Event listener to open the update modal invoice type
  $(document).on("click", "[id^=modal-updateInvoiceType-]", function () {
    const id = $(this).attr("id").split("-").pop();
    const name = $(this).closest("label").text().trim();
    openUpdateInvoiceTypeModal(id, name);
  });

  // Event listener to close the update modal
  $(document).on("click", "#close-update-invoice-type-modal", function () {
    closeUpdateInvoiceTypeModal();
  });

  // Event listener for form submission
  $(document).on("submit", "#update-invoice-type-form", function (event) {
    event.preventDefault();

    const invoiceTypeData = {
      idInvoiceType: $("#invoice-type-id").val(),
      invoiceType: $("#invoice-type-name").val(),
    };

    $.ajax({
      url: "http://localhost:8080/policy/updateinvoicetype",
      type: "POST",
      data: invoiceTypeData,
      success: function (response) {
        if (response.status === "OK") {
          alert("Update invoice type successful!");
          closeUpdateInvoiceTypeModal();
          $(`#label-${response.data.id} .option-name`).text(response.data.name);
          // Optionally refresh the data here
        } else {
          alert("Error: " + response.desc);
        }
      },
      error: function (error) {
        console.error("Error:", error);
        alert("An error occurred while updating the invoice type.");
      },
    });
  });

  submitInsertForm();
});

function submitInsertForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    let allFieldsFilled = true;

    $("#form-insert")
      .find("input[type='text'], input[type='number'], textarea, select")
      .each(function () {
        if ($(this).val() === "") {
          allFieldsFilled = false;
          return false;
        }
      });

    if (allFieldsFilled) {
      var formData = new FormData($("#form-insert")[0]);

      $.ajax({
        url: "http://localhost:8080/policy/createexchange",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          if (response.status === "OK") {
            // const newData = response.data;
            // const newRow = `
            //   <tr class="${
            //     $("#table-body tr").length % 2 === 0
            //       ? "bg-gray-100"
            //       : "bg-white"
            //   }" data-id="${newData.id}">
            //     <td class="text-left py-3 px-4">${newData.id}</td>
            //     <td class="text-left py-3 px-4">${
            //       newData.description_policy
            //     }</td>
            //     <td class="text-left py-3 px-4">${newData.rate}</td>
            //     <td class="text-left py-3 px-4">${
            //       newData.status ? "Active" : "Inactive"
            //     }</td>
            //     <td class="text-left py-3 px-4">${new Date(
            //       newData.lastModified
            //     ).toLocaleDateString()}</td>

            //     <td class="text-left py-3 px-4">
            //         <button class="text-blue-500 text-xs" data-id="${
            //           newData.id
            //         }">
            //         <svg class="text-themeColor-500 w-10 h-10"
            //         xmlns="http://www.w3.org/2000/svg" width="24" height="24"  fill="none" viewBox="0 0 24 24" stroke="currentColor">
            //           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
            //           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            //         </svg>

            //         Edit</button>
            //         <button class="text-red-500 text-xs" data-id="${
            //           newData.id
            //         }">
            //         <svg class="text-themeColor-500 w-10 h-10"
            //         xmlns="http://www.w3.org/2000/svg" width="24"  height="24"  viewBox="0 0 24 24"  fill="none"  stroke="currentColor"  stroke-width="2"  stroke-linecap="round"  stroke-linejoin="round">  <path d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z" />  <line x1="18" y1="9" x2="12" y2="15" />  <line x1="12" y1="9" x2="18" y2="15" /></svg>
            //         Delete</button>
            //         <button class="text-amber-900 text-xs" id="bttn-detail" data-id="${
            //           newData.id
            //         }">
            //         <svg class="text-themeColor-500 w-10 h-10"
            //         xmlns="http://www.w3.org/2000/svg" width="24" height="24"  fill="none" viewBox="0 0 24 24" stroke="currentColor">
            //           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4"/>
            //         </svg>

            //         Detail</button>
            //     </td>
            //   </tr>

            // `;
            // $("#table-body").append(newRow);
            // $("#form-insert").find("input, select").val("");
            // $("#form-insert").find("select").prop("selectedIndex", 0);
            fetchPolicies();
            insertModal.addClass("hidden");
            alert(response.desc);
          } else {
            alert("Failed to create exchange rate policy");
          }
        },
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
