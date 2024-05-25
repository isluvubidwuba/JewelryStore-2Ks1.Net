const rowsLimit = 10;
let currentPage = 0;
let totalPage = 0;
let policies = [];

function fetchPolicies() {
  $.ajax({
    url: "http://localhost:8080/policy/listpolicy",
    method: "GET",
    success: function (response) {
      if (response.status === 200) {
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
                    <button class="text-blue-500" data-id="${
                      data.id
                    }">Edit</button>
                    <button class="text-red-500" data-id="${
                      data.id
                    }">Delete</button>
                    <button id="bttn-detail" class="text-amber-900">Detail</button>
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

// Initial fetch
$(document).ready(function () {
  fetchPolicies();
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

  $(document).on("click", ".text-blue-500", function () {
    const idExchangeRate = $(this).data("id");

    $.ajax({
      url: `http://localhost:8080/policy/infor?idExchangeRate=${idExchangeRate}`,
      type: "POST",
      success: function (response) {
        if (response.status === 200) {
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
        if (response.status === 200) {
          const updatedRow = $(`#table-body tr[data-id='${idExchange}']`);
          updatedRow.find("td:eq(1)").text(response.data.description_policy);
          updatedRow.find("td:eq(2)").text(response.data.rate);
          updatedRow
            .find("td:eq(3)")
            .text(response.data.status ? "Active" : "Inactive");
          updatedRow
            .find("td:eq(4)")
            .text(new Date(response.data.lastModified).toLocaleDateString());

          $("#form-update").find("input, select").val("");
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

  $(document).on("click", ".text-red-500", function () {
    const idExchangeRate = $(this).data("id");

    if (confirm("Are you sure you want to delete this policy?")) {
      $.ajax({
        url: `http://localhost:8080/policy/deleteexchange?idExchange=${idExchangeRate}`,
        type: "POST",
        success: function (response) {
          if (response.status === 200) {
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

  submitInsertForm(); // Initialize the insert form submission handler
});

function submitInsertForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    // Flag to track if all fields are filled
    let allFieldsFilled = true;

    // Validate all required fields
    $("#form-insert")
      .find("input[type='text'], input[type='number'], textarea, select")
      .each(function () {
        if ($(this).val() === "") {
          allFieldsFilled = false;
          return false; // Break out of the loop if any field is empty
        }
      });

    // If all fields are filled, proceed with AJAX request
    if (allFieldsFilled) {
      var formData = new FormData($("#form-insert")[0]); // Sử dụng id của biểu mẫu

      $.ajax({
        url: "http://localhost:8080/policy/createexchange",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          if (response.status === 200) {
            const newData = response.data;
            const newRow = `
              <tr class="${
                $("#table-body tr").length % 2 === 0
                  ? "bg-gray-100"
                  : "bg-white"
              }" data-id="${newData.id}">
                <td class="text-left py-3 px-4">${newData.id}</td>
                <td class="text-left py-3 px-4">${
                  newData.description_policy
                }</td>
                <td class="text-left py-3 px-4">${newData.rate}</td>
                <td class="text-left py-3 px-4">${
                  newData.status ? "Active" : "Inactive"
                }</td>
                <td class="text-left py-3 px-4">${new Date(
                  newData.lastModified
                ).toLocaleDateString()}</td>
                <td class="text-left py-3 px-4">
                  <button class="text-blue-500" data-id="${
                    newData.id
                  }">Edit</button>
                  <button class="text-red-500" data-id="${
                    newData.id
                  }">Delete</button>
                  <button id="bttn-detail" class="text-amber-900">Detail</button>
                </td>
              </tr>
            `;
            $("#table-body").append(newRow);
            $("#form-insert")
              .find(
                "input[type='text'], input[type='number'], textarea, select"
              )
              .val("");
            $("#form-insert").find("select").prop("selectedIndex", 0);
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
