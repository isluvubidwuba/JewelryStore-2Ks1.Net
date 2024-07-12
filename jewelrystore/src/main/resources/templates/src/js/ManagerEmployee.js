import UserService from "./userService.js";
import { viewEmployee2 } from "./revenueEmployee.js";
const userService = new UserService();

$(document).ready(function () {
  initializeInsertEmployee();
  initializePagination();
  initializeSearchForm();
  fetchEmployees(0);
});

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
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/listpage?page=${page}`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        renderEmployees(response.data.employees);
        updatePagination(response.data.currentPage, response.data.totalPages);
        currentPage = response.data.currentPage;
      } else {
        showNotification("Failed to load employee data.", "error");
      }
    })
    .catch(() => {
      showNotification("Error while fetching employee data.", "error");
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
              <td class="px-6 py-3">${employee.firstName} ${employee.lastName
      }</td>
              <td class="px-6 py-3">${employee.role.name}</td>
              <td class="px-6 py-3">${statusLabel}</td>
              <td class="px-6 py-3">${formatCurrency(
        employee.totalRevenue
      )}</td>
              <td class="px-6 py-3">
                <button class="bg-black hover:bg-gray-700 text-white px-4 py-2 rounded" data-view-employee="${employee.id
      }">View</button>
                <button class="bg-black hover:bg-gray-700 text-white px-4 py-2 rounded" data-view-employee2="${employee.id
      }">Revenue</button>
              </td>
          </tr>
      `;
    employeeTableBody.append(employeeRow);

    fetchEmployeeImage(employee.id, employee.image);
  });
  // Attach event listeners after rendering rows
  $("#employeeTableBody").on("click", "[data-view-employee]", function () {
    const employeeId = $(this).data("viewEmployee");
    console.log("Check ID Employee from view button: " + employeeId);
    if (employeeId) {
      viewEmployee(employeeId);
    } else {
      showNotification("Error: Employee ID is undefined", "error");
    }
  });

  $("#employeeTableBody").on("click", "[data-view-employee2]", function () {
    const employeeId = $(this).data("viewEmployee2");
    console.log("Check ID Employee from view button: " + employeeId);
    if (employeeId) {
      viewEmployee2(employeeId);
    } else {
      showNotification("Error: Employee ID is undefined", "error");
    }
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
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/role/list`,
      "GET",
      null
    )
    .then((response) => {
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
        showNotification("Failed to load roles: " + response.desc, "error");
      }
    })
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(
          "Error while load roles: " + error.responseJSON.desc,
          "error"
        );
      } else {
        console.error("Error while load roles: ", error);
        showNotification("Error load roles!", "error");
      }
    });
}

function viewEmployee(id) {
  fetchRoles("viewRole"); // Fetch roles before opening the modal

  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/listemployee/${id}`,
      "GET",
      null
    )
    .then((response) => {
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

        // // Kiểm tra trạng thái và ẩn/hiện nút "Delete"
        // if (!employee.status) {
        //   $("#deleteEmployeeBtn").removeClass("hidden");
        // } else {
        //   $("#deleteEmployeeBtn").addClass("hidden");
        // }
        openModal();
      } else {
        showNotification(
          "Failed to load employee details." + response.desc,
          "error"
        );
      }
    })
    .catch((error) => {
      showNotification("Error while fetching employee details", "error");
      if (error.responseJSON) {
        showNotification(
          "Error while fetching employee details" + error.responseJSON.desc,
          "error"
        );
      } else {
        console.error("Error while fetching employee details : ", error);
        showNotification("Error fetching employee details :!", "error");
      }
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

  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/update`,
      "POST",
      formData
    )
    .then((response) => {
      if (response.status === "OK") {
        showNotification(response.desc, "OK");
        clearUpdateForm();
        closeModal();
        fetchEmployees(currentPage);
      } else {
        showNotification(
          "Failed to update employee: " + response.desc,
          "error"
        );
      }
    })
    .catch((error) => {
      showNotification("Error update employee!", "error");
      if (error.responseJSON) {
        showNotification(
          "Failed while update employee: " + error.responseJSON.desc,
          "error"
        );
      } else {
        console.error("Error while update employee: ", error);
        showNotification("Error update employee!", "error");
      }
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

  const phoneNumber = $("#insertPhoneNumber").val();
  const email = $("#insertEmail").val();
  const firstName = $("#insertFirstName").val();
  const lastName = $("#insertLastName").val();
  const address = $("#insertAddress").val().trim();
  const role = $("#insertRole").val();

  // Check if any field is empty
  if (!phoneNumber || !email || !firstName || !lastName || !address || !role) {
    showNotification("All fields are required", "Error");
    return;
  }

  if (!isValidPhoneNumber(phoneNumber)) {
    showNotification("Phone number must be exactly 10 digits", "Error");
    return;
  }

  if (!isValidEmail(email)) {
    showNotification("Email must be in the format @gmail.com", "Error");
    return;
  }

  if (!isValidName(firstName)) {
    showNotification("Name must not contain special characters or numbers", "Error");
    return;
  }

  if (!isValidName(lastName)) {
    showNotification("Name must not contain special characters or numbers", "Error");
    return;
  }

  var formData = new FormData($("#insertEmployeeForm")[0]);
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/insert`,
      "POST",
      formData
    )
    .then((response) => {
      if (response.status === "OK") {
        showNotification(response.desc, "OK");
        handleSendMailEmployee(response.data);
        fetchEmployees(currentPage);
        closeInsertModal();
      } else {
        showNotification(
          "Failed to update employee: " + response.desc,
          "Error"
        );
      }
    })
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(
          error.responseJSON.desc,
          "Error"
        );
      } else {
        showNotification("Error inserting user!", "Error");
      }
    });
}
function isValidPhoneNumber(phoneNumber) {
  var phoneRegex = /^\d{10}$/;
  return phoneRegex.test(phoneNumber);
}

function isValidEmail(email) {
  var emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
  return emailRegex.test(email);
}
function isValidName(name) {
  var nameRegex = /^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+(\s[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+)*$/u;
  return nameRegex.test(name);
}

function handleSendMailEmployee(idEmploy) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/sendInfo/${idEmploy}`,
      "POST",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        showNotification(response.desc, "OK");
      } else {
        showNotification(response.desc, "Error");
      }
    })
    .catch((error) => {
      showNotification("Error send mail user!", "Error");
      if (error.responseJSON) {
        showNotification(
          "Error while send mail employee: " + error.responseJSON.desc,
          "Error"
        );
      } else {
        console.error("Error while send mail employee: ", error);
        showNotification("Error send mail user!", "Error");
      }
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

    // General validation for empty input
    if (query === "") {
      showNotification("Search query cannot be empty", "Error");
      return;
    }
    // Validation
    if (criteria === "id" && !/^SE\d{8}$/.test(query)) {
      showNotification(
        "Invalid ID format. ID should start with 'SE' followed by 8 digits",
        "Error"
      );
      return;
    }

    if (
      criteria === "role" &&
      !["ADMIN", "MANAGER", "STAFF"].includes(query.toUpperCase())
    ) {
      showNotification(
        "Invalid role. Valid roles are ADMIN, MANAGER, STAFF",
        "Error"
      );
      return;
    }

    if (
      criteria === "status" &&
      !["active", "inactive"].includes(query.toLowerCase())
    ) {
      showNotification(
        "Invalid status. Valid statuses are active or inactive",
        "Error"
      );
      return;
    }
    
    if (criteria === "name" && !isValidName(query)) {
      showNotification(
        "Invalid name format. Name should not contain special characters or numbers",
        "Error"
      );
      return;
    }

    searchEmployees(criteria, query, 0);
  });
}

function searchEmployees(criteria, query, page) {
  // Chuyển đổi giá trị tiêu chí
  if (criteria === "role") {
    query = query.toUpperCase();
  }

  if (criteria === "status") {
    query = query.toLowerCase();
  }

  const apiUrl = `http://${userService.getApiUrl()}/api/employee/search?criteria=${criteria}&query=${encodeURIComponent(
    query
  )}&page=${page}`;

  console.log("Check Search Employee : " + criteria);
  console.log("Check Search Employee : " + query);
  console.log("Check Search Employee : " + page);

  userService
    .sendAjaxWithAuthen(apiUrl, "POST", null)
    .then((response) => {
      if (response.status === "OK") {
        if (response.data.employees && response.data.employees.length > 0) {
          showNotification(response.desc, "OK");
          renderEmployees(response.data.employees);
          updatePagination(response.data.currentPage, response.data.totalPages);
          currentPage = response.data.currentPage;
        } else {
          showNotification("Employee not exist in system", "Error");
        }
      } else {
        showNotification(response.desc || "Failed to search employee data.", "Error");
      }
    })
    .catch((error) => {
      // Check if the error response contains a JSON body
      if (error.responseJSON && error.responseJSON.desc) {
        showNotification(error.responseJSON.desc, "Error");
      } else {
        console.error("Error details: ", error);
        showNotification("Error while searching employee data.", "Error");
      }
    });
}

function deleteEmployee() {
  const employeeId = $("#viewEmployeeId").val();

  if (confirm("Are you sure you want to delete this employee?")) {
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/employee/delete/${employeeId}`,
        "GET",
        null
      )
      .then((response) => {
        console.log("Delete response:", response); // Ghi log phản hồi
        if (response.status === "OK") {
          showNotification(response.desc, "Error");
          closeModal();
          fetchEmployees(currentPage);
        } else {
          showNotification(
            "Failed to delete employee: " + response.desc,
            "Error"
          );
        }
      })
      .catch((error) => {
        showNotification("Error deleting employee!", "Error");
        console.error("Error while deleting employee:", error); // Ghi log lỗi
        if (error.responseJSON) {
          showNotification(
            "Error while deleting employee: " + error.responseJSON.desc,
            "Error"
          );
        } else {
          showNotification("Error deleting employee!", "Error");
        }
      });
  }
}
