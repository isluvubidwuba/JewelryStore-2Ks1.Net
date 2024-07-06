const apiurl = process.env.API_URL;
$(document).ready(function () {
  initializeInsertEmployee();
  initializePagination();
  initializeSearchForm();
  fetchEmployees(0);
});
const token = localStorage.getItem("token");

let currentPage = 0;

function initializePagination() {
  $("#prevPageBtn").click(handlePrevPage);
  $("#nextPageBtn").click(handleNextPage);
  $("#closeViewModalBtn").click(closeModal);
  $("#updateEmployeeBtn").click(updateEmployee);
  $("#deleteEmployeeBtn").click(deleteEmployee); // Added delete button initialization
}

function initializeInsertEmployee() {
  $("#openInsertModalBtn").click(openInsertModal);
  $("#closeInsertModalBtn").click(closeInsertModal);
  $("#insertEmployeeForm").submit(handleInsertEmployee);
  $("#insertEmployeeImageFile").change(previewInsertImage);
}

function fetchEmployeeImage(employeeId, imageUrl) {
  $(`#employee-image-${employeeId}`).html(
    `<img src="${imageUrl}" alt="Employee Image" class="w-10 h-10 rounded-full">`
  );
}

function fetchEmployees(page) {
  $.ajax({
    url: `http://${apiurl}/employee/listpage`,
    type: "GET",
    data: { page: page },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        renderEmployees(response.data.employees);
        updatePagination(response.data.currentPage, response.data.totalPages);
        currentPage = response.data.currentPage;
      } else {
        alert("Failed to load employee data.");
      }
    },
    error: function () {
      alert("Error while fetching employee data.");
    },
  });
}

function renderEmployees(employees) {
  const employeeTableBody = $("#employeeTableBody");
  employeeTableBody.empty();

  employees.forEach((employee) => {
    const statusLabel = employee.status
      ? '<span class="text-green-600">Active</span>'
      : '<span class="text-red-600">Inactive</span>';

    const employeeRow = `
          <tr class="border-b dark:border-gray-700">
              <td class="px-6 py-3">${employee.id}</td>
              <td class="px-6 py-4" id="employee-image-${employee.id}">
                  Loading...
              </td>
              <td class="px-6 py-3">${employee.lastName} ${
      employee.firstName
    }</td>
              <td class="px-6 py-3">${employee.role.name}</td>
              <td class="px-6 py-3">${statusLabel}</td>
              <td class="px-6 py-3">${formatCurrency(
                employee.totalRevenue
              )}</td>
              <td class="px-6 py-3">
                  <button class="bg-black hover:bg-gray-700 text-white px-4 py-2 rounded" onclick="viewEmployee('${
                    employee.id
                  }')">View</button>
                  <button class="bg-black hover:bg-gray-700 text-white px-4 py-2 rounded" onclick="viewEmployee2('${
                    employee.id
                  }')">Revenue</button>
                  
              </td>
          </tr>
      `;
    employeeTableBody.append(employeeRow);

    fetchEmployeeImage(employee.id, employee.image);
  });
}

function handlePrevPage() {
  if (currentPage > 0) {
    ``;
    fetchEmployees(currentPage - 1);
  }
}
// Hàm định dạng tiền tệ
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}
function handleNextPage() {
  fetchEmployees(currentPage + 1);
}

function updatePagination(currentPage, totalPages) {
  $("#currentPageIndicator").text(`Page ${currentPage + 1} of ${totalPages}`);
  $("#prevPageBtn").prop("disabled", currentPage === 0);
  $("#nextPageBtn").prop("disabled", currentPage === totalPages - 1);
}

function fetchRoles(selectElementId) {
  $.ajax({
    url: `http://${apiurl}/role/list`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const roles = response.data;
        const roleSelect = $(`#${selectElementId}`);
        roleSelect.empty();
        roles
          .filter((role) => [1, 2, 3].includes(role.id))
          .forEach((role) => {
            roleSelect.append(
              `<option value="${role.id}">${role.name}</option>`
            );
          });
      } else {
        alert("Failed to load roles: " + response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert("Error while load roles: " + error.responseJSON.desc);
      } else {
        console.error("Error while load roles: ", error);
        alert("Error load roles!");
      }
    },
  });
}

function viewEmployee(id) {
  fetchRoles("viewRole"); // Fetch roles before opening the modal
  $.ajax({
    url: `http://${apiurl}/employee/listemployee/${id}`,
    type: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const employee = response.data;
        // Sử dụng URL hình ảnh từ phản hồi API
        const imageUrl = employee.image;

        $("#viewEmployeeImage").attr("src", imageUrl);
        $("#viewEmployeeId").val(employee.id);
        $("#viewFirstName").val(employee.firstName);
        $("#viewLastName").val(employee.lastName);
        $("#viewPhoneNumber").val(employee.phoneNumber);
        $("#viewEmail").val(employee.email);
        $("#viewAddress").val(employee.address);
        $("#viewRole").val(employee.role.id);
        $("#viewStatus").val(employee.status ? "true" : "false");

        // Kiểm tra trạng thái và ẩn/hiện nút "Delete"
        if (!employee.status) {
          $("#deleteEmployeeBtn").removeClass("hidden");
        } else {
          $("#deleteEmployeeBtn").addClass("hidden");
        }
        openModal();
      } else {
        alert("Failed to load employee details." + response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert(
          "Error while fetching employee details : " + error.responseJSON.desc
        );
      } else {
        console.error("Error while fetching employee details : ", error);
        alert("Error fetching employee details :!");
      }
    },
  });
}

function openModal() {
  $("#viewEmployeeModal").removeClass("hidden");
}

function closeModal() {
  $("#viewEmployeeModal").addClass("hidden");
  $("#insertEmployeeModal").addClass("hidden");
  // Xóa ảnh và form khi đóng modal
  clearUpdateForm();
}

function updateEmployee() {
  var formData = new FormData($("#viewEmployeeForm")[0]);
  var fileInput = $("#viewEmployeeImageFile")[0];

  formData.append("file", fileInput.files[0]);

  // In ra console để kiểm tra FormData
  for (var pair of formData.entries()) {
    console.log(pair[0] + ": " + pair[1]);
  }

  $.ajax({
    url: `http://${apiurl}/employee/update`,
    type: "POST",
    data: formData,
    headers: {
      Authorization: `Bearer ${token}`,
    },
    processData: false,
    contentType: false,
    success: function (response) {
      if (response.status === "OK") {
        alert(response.desc);
        clearUpdateForm();
        closeModal();
        fetchEmployees(currentPage);
      } else {
        alert("Failed to update employee: " + response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert("Error while update employee: " + error.responseJSON.desc);
      } else {
        console.error("Error while update employee: ", error);
        alert("Error update employee!");
      }
    },
  });
}

function clearUpdateForm() {
  $("#viewEmployeeForm")[0].reset();
  $("#viewEmployeeImage").attr("src", ""); // Xóa ảnh xem trước
  $("#viewRole").val("");
  $("#viewStatus").val("");
  // Xóa tệp tin đã chọn
  const fileInput = document.getElementById("viewEmployeeImageFile");
  if (fileInput) {
    fileInput.value = "";
  }
}

function previewInsertImage() {
  const fileInput = document.getElementById("insertEmployeeImageFile");
  const imagePreview = document.getElementById("insertEmployeeImagePreview");
  const file = fileInput.files[0];

  if (file) {
    const reader = new FileReader();
    reader.onload = function (e) {
      imagePreview.src = e.target.result;
      imagePreview.style.display = "block"; // Hiển thị hình ảnh
    };
    reader.readAsDataURL(file);
  } else {
    imagePreview.src = "#";
    imagePreview.style.display = "none"; // Ẩn hình ảnh khi không có file
  }
}

function openInsertModal() {
  fetchRoles("insertRole");
  $("#insertEmployeeModal").removeClass("hidden");
}

function closeInsertModal() {
  $("#insertEmployeeModal").addClass("hidden");
  document.getElementById("insertEmployeeForm").reset();
  $("#insertEmployeeImagePreview").attr("src", "#");
}

function handleInsertEmployee(event) {
  event.preventDefault();
  var formData = new FormData($("#insertEmployeeForm")[0]);

  $.ajax({
    url: `http://${apiurl}/employee/insert`,
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        alert(response.desc);
        handleSendMailEmployee(response.data);
        fetchEmployees(currentPage);
        closeInsertModal();
      } else {
        alert("Failed to update employee: " + response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert("Error while inserting employee: " + error.responseJSON.desc);
      } else {
        console.error("Error while inserting employee: ", error);
        alert("Error updating user!");
      }
    },
  });
}

function handleSendMailEmployee(idEmploy) {
  $.ajax({
    url: `http://${apiurl}/mail/sendInfo/${idEmploy}`,
    type: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        alert(response.desc);
      } else {
        alert(response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert("Error while send mail employee: " + error.responseJSON.desc);
      } else {
        console.error("Error while send mail employee: ", error);
        alert("Error send mail user!");
      }
    },
  });
}
function initializeSearchForm() {
  $("#dropdown-button").click(function () {
    $("#dropdown").toggleClass("hidden");
  });

  $(".dropdown-item").click(function () {
    const selectedCriteria = $(this).data("criteria");
    $("#selected-criteria").text(selectedCriteria);
    $("#dropdown").addClass("hidden");
  });

  $("#searchForm").submit(function (event) {
    event.preventDefault();
    const criteria = $("#selected-criteria").text().toLowerCase();
    const query = $("#searchInput").val();
    searchEmployees(criteria, query, 0);
  });
}

function searchEmployees(criteria, query, page) {
  $.ajax({
    url: `http://${apiurl}/employee/search`,
    type: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    data: { criteria: criteria, query: query, page: page },
    success: function (response) {
      if (response.status === "OK") {
        renderEmployees(response.data.employees);
        updatePagination(response.data.currentPage, response.data.totalPages);
        currentPage = response.data.currentPage;
      } else {
        alert("Failed to search employee data.");
      }
    },
    error: function () {
      alert("Error while searching employee data.");
    },
  });
}

function deleteEmployee() {
  const employeeId = $("#viewEmployeeId").val();

  if (confirm("Are you sure you want to delete this employee?")) {
    $.ajax({
      url: `http://${apiurl}/employee/delete/${employeeId}`,
      type: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        console.log("Delete response:", response); // Ghi log phản hồi
        if (response.status === "OK") {
          alert(response.desc);
          closeModal();
          fetchEmployees(currentPage);
        } else {
          alert("Failed to delete employee: " + response.desc);
        }
      },
      error: function (error) {
        console.error("Error while deleting employee:", error); // Ghi log lỗi
        if (error.responseJSON) {
          alert("Error while deleting employee: " + error.responseJSON.desc);
        } else {
          alert("Error deleting employee!");
        }
      },
    });
  }
}
