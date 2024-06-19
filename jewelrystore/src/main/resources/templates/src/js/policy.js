const token = localStorage.getItem("token");
let policies = [];

function fetchPolicies() {
  $.ajax({
    url: "http://localhost:8080/exchange-rate-policy",
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        policies = response.data;
        displayTableData(policies);
        updatePolicyOverview(policies);
      } else {
        alert("Failed to fetch data");
      }
    },
    error: function (xhr, status, error) {
      alert("Error fetching data");
    },
  });
}

function searchPolicies(keyword) {
  const filteredPolicies = policies.filter((policy) => {
    return (
      policy.id.toLowerCase().includes(keyword.toLowerCase()) ||
      policy.description_policy.toLowerCase().includes(keyword.toLowerCase()) ||
      policy.invoiceTypeDTO.name
        .toLowerCase()
        .includes(keyword.toLowerCase()) ||
      (policy.status ? "active" : "inactive")
        .toLowerCase()
        .includes(keyword.toLowerCase())
    );
  });

  displayTableData(filteredPolicies);
  updatePolicyOverview(filteredPolicies);
}

function updatePolicyOverview(policies) {
  const totalPolicies = policies.length;
  const activePolicies = policies.filter((policy) => policy.status).length;
  const inactivePolicies = totalPolicies - activePolicies;

  $("#totalPolicies").text(totalPolicies);
  $("#activePolicies").text(activePolicies);
  $("#inactivePolicies").text(inactivePolicies);
}

// <button class="text-blue-500 text-xs update-invoice-type" data-id="${
//   data.invoiceTypeDTO.id
// }">
//   Update
// </button>
// <button class="text-red-500 text-xs delete-invoice-type" data-id="${
//   data.invoiceTypeDTO.id
// }">
//   Delete
// </button>
function displayTableData(policies) {
  $("#table-body").empty();
  policies.forEach((data, index) => {
    $("#table-body").append(`
      <tr class="${index % 2 === 0 ? "bg-gray-100" : "bg-white"}" data-id="${
      data.id
    }">
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
        ${data.invoiceTypeDTO.name}
      </td>

        <td class="text-left py-3 px-4">
          <button class="text-blue-500 text-xs update-open" data-id="${
            data.id
          }">
            <svg class="text-themeColor-500 w-10 h-10" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
            Edit
          </button>
          ${
            data.status
              ? `
          <button class="text-red-500 text-xs" data-id="${data.id}">
            <svg class="text-themeColor-500 w-10 h-10" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z"/>
              <line x1="18" y1="9" x2="12" y2="15"/>
              <line x1="12" y1="9" x2="18" y2="15"/>
            </svg>
            Inactive
          </button>
          `
              : ``
          }
        </td>
      </tr>
    `);
  });

  $("#entries-info").text(
    `Showing 1 to ${policies.length} of ${policies.length} entries`
  );
}

function openUpdateInvoiceTypeModal(id) {
  $.ajax({
    url: `http://localhost:8080/invoice-type/${id}`,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const data = response.data;
        $("#invoice-type-id").val(data.id);
        $("#invoice-type-name").val(data.name);
        $("#updateInvoiceTypeModal").removeClass("hidden");
      } else {
        alert("Failed to fetch invoice type data");
      }
    },
    error: function (xhr, status, error) {
      alert("Error fetching invoice type data");
    },
  });
}

function closeUpdateInvoiceTypeModal() {
  $("#updateInvoiceTypeModal").addClass("hidden");
}
function submitUpdateInvoiceTypeForm() {
  $("#update-invoice-type-form").on("submit", function (event) {
    event.preventDefault();

    const id = $("#invoice-type-id").val();
    const name = $("#invoice-type-name").val();

    if (!name.trim()) {
      alert("Invoice Type name cannot be empty.");
      return;
    }

    $.ajax({
      url: `http://localhost:8080/invoice-type/update/${id}`,
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: {
        name: name,
      },
      success: function (response) {
        if (response.status === "OK") {
          alert("Invoice type updated successfully");
          closeUpdateInvoiceTypeModal();
          fetchPolicies();
        } else {
          alert("Failed to update invoice type");
        }
      },
      error: function (xhr, status, error) {
        alert("Error updating invoice type");
      },
    });
  });
}

// Các hàm mở và đóng modal insert/update
function openInsertModal() {
  fetchInvoiceTypes();
  $("#insert-modal").removeClass("hidden");
}

function closeInsertModal() {
  $("#insert-modal").addClass("hidden");
}

function openUpdateModal(id) {
  $.ajax({
    url: `http://localhost:8080/exchange-rate-policy/${id}`,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const data = response.data;
        $("#update-idExchange").val(data.id);
        $("#update-desc").val(data.description_policy);
        $("#update-rate").val(data.rate);
        $("#update-status").val(data.status ? "true" : "false");

        fetchInvoiceTypes(function () {
          $("#update-invoiceType").val(data.invoiceTypeDTO.id);
        });

        $("#update-modal").removeClass("hidden");
      } else {
        alert("Failed to fetch exchange rate policy data");
      }
    },
    error: function (xhr, status, error) {
      alert("Error fetching exchange rate policy data");
    },
  });
}

function closeUpdateModal() {
  $("#update-modal").addClass("hidden");
}
function fetchInvoiceTypes(callback) {
  $.ajax({
    url: "http://localhost:8080/invoice-type",
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const invoiceTypes = response.data;
        $("#invoiceType, #update-invoiceType").empty();
        invoiceTypes.forEach((type) => {
          $("#invoiceType, #update-invoiceType").append(
            `<option value="${type.id}">${type.name}</option>`
          );
        });
        if (callback) callback();
      } else {
        alert("Failed to fetch invoice types");
      }
    },
    error: function (xhr, status, error) {
      alert("Error fetching invoice types");
    },
  });
}

// Hàm để submit form insert
function submitInsertForm() {
  $(document).on("click", "#submit-insert", function (event) {
    event.preventDefault();

    if (validateForm("#form-insert")) {
      var formData = new FormData($("#form-insert")[0]);

      $.ajax({
        url: "http://localhost:8080/exchange-rate-policy/create",
        type: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          if (response.status === "CREATED") {
            alert("Exchange rate policy created successfully");
            closeInsertModal();
            fetchPolicies();
          } else {
            alert("Failed to create exchange rate policy");
          }
        },
        error: function (xhr, status, error) {
          alert("Error creating exchange rate policy");
        },
      });
    }
  });
}

// Hàm để submit form cập nhật
function submitUpdateForm() {
  $(document).on("click", "#submit-update", function (event) {
    event.preventDefault();

    if (validateForm("#form-update")) {
      var formData = new FormData($("#form-update")[0]);

      $.ajax({
        url: `http://localhost:8080/exchange-rate-policy/update/${$(
          "#update-idExchange"
        ).val()}`,
        type: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          if (response.status === "OK") {
            alert("Exchange rate policy updated successfully");
            closeUpdateModal();
            fetchPolicies();
          } else {
            alert("Failed to update exchange rate policy");
          }
        },
        error: function (xhr, status, error) {
          alert("Error updating exchange rate policy");
        },
      });
    }
  });
}

// Hàm kiểm tra tính hợp lệ của form
function validateForm(formSelector) {
  let isValid = true;
  let errorMessage = "";

  const form = $(formSelector);
  const idExchange = form.find("[name='idExchange']").val().trim();
  const desc = form.find("[name='desc']").val().trim();
  const rate = parseFloat(form.find("[name='rate']").val().trim());
  const status = form.find("[name='status']").val();
  const invoiceTypeId = form.find("[name='invoiceType']").val();

  if (!idExchange || !desc || !status || !invoiceTypeId || isNaN(rate)) {
    isValid = false;
    errorMessage = "Tất cả các trường phải được điền.\n";
    alert(errorMessage);
    return isValid;
  }
  if (isNaN(rate) || rate <= 0 || rate >= 10) {
    isValid = false;
    errorMessage += "Rate phải là số dương và nhỏ hơn 10.\n";
  }

  if (!isValid) {
    alert(errorMessage);
  }

  return isValid;
}

$(document).ready(function () {
  fetchPolicies();
  submitInsertForm();
  $("#insert-button").on("click", openInsertModal);
  $("#modalInsertClose").on("click", closeInsertModal);
  $("#form-insert").on("submit", submitInsertForm);

  $(document).on("click", ".update-open", function () {
    const id = $(this).data("id");
    openUpdateModal(id);
  });

  $("#modalUpdateClose").on("click", closeUpdateModal);
  submitUpdateForm();

  $("#keyword").on("input", function () {
    const keyword = $(this).val();
    searchPolicies(keyword);
  });
  // Thêm sự kiện để mở modal cập nhật invoice type
  $(document).on("click", ".update-invoice-type", function () {
    const id = $(this).data("id");
    openUpdateInvoiceTypeModal(id);
  });

  // Thêm sự kiện để đóng modal cập nhật invoice type
  $("#close-update-invoice-type-modal").on(
    "click",
    closeUpdateInvoiceTypeModal
  );
  submitUpdateInvoiceTypeForm();
});
