import UserService from "./userService.js";

const userService = new UserService();
const userRole = userService.getUserRole();

$(document).ready(function () {
  fetchRoles();
  fetchUniqueRankData();
  initializeUnique();
  setupInsertModalToggle();
  initializeTabs();
  toggleButtonVisibility(userRole);
});


function toggleButtonVisibility(userRole) {
  console.log("Check role vao trang : " + userRole);
  if (userRole === 'ADMIN') {
    $('.edit-unique-btn').show();
    $('#addCustomerType').show();
  } else {
    $('.edit-unique-btn').hide();
    $('#addCustomerType').hide();
  }
}

function fetchRoles() {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/role/list`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const roles = response.data.filter(
          (role) => !["STAFF", "ADMIN", "MANAGER"].includes(role.name)
        );
        initializeTabs(roles);  // Gọi hàm initializeTabs ở đây
        initTabs(roles);
        roles.forEach((role) => setupSearch(role.name));
        populateRoleSelect(roles, "#update-role"); // Populate role select for update modal
        populateRoleSelect(roles, "#insert-role");
      } else {
        showNotification("Fail to load Role.", "error");
      }
    })
    .catch((error) => {
      console.error("Error fetching roles:", error);
    });
}

function initTabs(roles) {
  roles.forEach((role) => {
    $("#role-tabs").append(createTab(role));
    $("#tab-contents").append(createTabContent(role));
  });

  // Append the insert user button
  $("#role-tabs").append(
    '<li class="ml-auto"><button id="insert-button" class="tab-button bg-black text-white font-semibold py-2 px-4 rounded hover:bg-gray-700 transition duration-300">Insert User</button></li>'
  );

  bindTabClickEvents();
  const firstRole = roles[0].name;
  switchTabByRole(firstRole); // Ensure first tab is activated
}

function populateRoleSelect(roles, selector) {
  const roleSelect = $(selector);
  roleSelect.empty();
  roleSelect.append(
    `<option value="" disabled selected>Select a role</option>`
  ); // Add default option
  roles.forEach((role) => {
    roleSelect.append(`<option value="${role.id}">${role.name}</option>`);
  });
}

function createTab(role) {
  return `<li class="mr-1">
            <a class="tab-button bg-white inline-block py-2 px-4 font-semibold" data-role="${role.name}" href="#">${role.name}</a>
          </li>`;
}

function createTabContent(role) {
  const rankHeader =
    role.name === "CUSTOMER" ? '<th class="py-2 px-4 border-b">Rank</th>' : "";
  const commonContent = `
      <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl">${role.name} List</h2>
          <div class="flex items-center">
            <div class="relative">
              <button id="${role.name.toLowerCase()}-criteria-button" class="bg-gray-300 text-gray-700 font-bold py-2 px-4 rounded inline-flex items-center">
                <span id="${role.name.toLowerCase()}-selected-criteria">Search By</span>
                <svg class="fill-current h-4 w-4 ml-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
                  <path d="M0 0h20v20H0z" fill="none"/>
                  <path d="M5.293 7.293l4.707 4.707 4.707-4.707-1.414-1.414L10 8.586 6.707 5.293z"/>
                </svg>
              </button>
              <ul id="${role.name.toLowerCase()}-criteria-menu" class="absolute hidden text-gray-700 pt-1">
                <li class=""><a class="rounded-t bg-gray-200 hover:bg-gray-400 py-2 px-4 block whitespace-no-wrap" href="#" data-criteria="id">ID</a></li>
                <li class=""><a class="bg-gray-200 hover:bg-gray-400 py-2 px-4 block whitespace-no-wrap" href="#" data-criteria="name">Name</a></li>
                <li class=""><a class="bg-gray-200 hover:bg-gray-400 py-2 px-4 block whitespace-no-wrap" href="#" data-criteria="numberphone">Phone Number</a></li>
                <li class=""><a class="rounded-b bg-gray-200 hover:bg-gray-400 py-2 px-4 block whitespace-no-wrap" href="#" data-criteria="email">Email</a></li>
              </ul>
            </div>
            <input type="text" id="${role.name.toLowerCase()}-search-input" class="border rounded px-4 py-2 ml-2" placeholder="Search...">
            <button id="${role.name.toLowerCase()}-search-button" class="bg-gray-800 hover:bg-black text-white font-bold py-2 px-4 rounded ml-2">Search</button>
             <button id="${role.name.toLowerCase()}-find-all-button" class="bg-gray-800 hover:bg-black text-white font-bold py-2 px-4 rounded ml-2">Find All</button>
          </div>
      </div>
      <table id="${role.name.toLowerCase()}-table" class="min-w-full bg-white">
          <thead>
              <tr class ="bg-gray-300 shadow-sm">
                  <th class="py-2 px-4 border-b">ID</th>
                  <th class="py-2 px-4 border-b">Image</th>
                  <th class="py-2 px-4 border-b">Full Name</th>
                  <th class="py-2 px-4 border-b">Phone Number</th>
                  <th class="py-2 px-4 border-b">Email</th>
                  <th class="py-2 px-4 border-b">Address</th>
                  ${rankHeader}
                  <th class="py-2 px-4 border-b">Action</th>
              </tr>
          </thead>
          <tbody>
              <!-- ${role.name} rows will be dynamically added here -->
          </tbody>
      </table>
      <div id="pagination-${role.name.toLowerCase()}" class="mt-4">
          <button id="prev-page-${role.name.toLowerCase()}" class="bg-black hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Previous</button>
          <span id="page-info-${role.name.toLowerCase()}" class="mx-2"></span>
          <button id="next-page-${role.name.toLowerCase()}" class="bg-black hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Next</button>
      </div>
  `;

  return `<div id="${role.name}" class="tab-content" style="display: none;">${commonContent}</div>`;
}

function bindTabClickEvents() {
  $(".tab-button").on("click", function (event) {
    event.preventDefault();
    const role = $(this).data("role");
    switchTabByRole(role);
  });
}

function switchTab(role) {
  if (!role) {
    console.error("Role is undefined"); // Log an error if role is undefined
    return;
  }

  $(".tab-content").hide();
  $("#" + role.replace(/\s+/g, "")).show();
  setActiveTab(role);
  setupSearch(role); // Ensure search setup is called every time a tab is switched
}

function setActiveTab(role) {
  $(".tab-button").removeClass(
    "border-l border-t border-r rounded-t text-blue-700 font-semibold"
  );
  $(`.tab-button[data-role=${role}]`).addClass(
    "border-l border-t border-r rounded-t text-blue-700 font-semibold"
  );
}

function fetchImage(elementId, imageUrl) {
  console.log(
    `Fetching image for elementId: ${elementId}, imageUrl: ${imageUrl}`
  );
  $(`#${elementId}`).html(
    `<img src="${imageUrl}" alt="Image" class="h-10 w-10">`
  );
}

function fetchCustomers(page) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/userinfo/listcustomer?page=${page}`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const { customers, totalPages, currentPage } = response.data;
        fetchCustomerRanks(function (ranks) {
          populateTable(customers, ranks, currentPage, "Customer");
          updatePagination(currentPage, totalPages, "customer");
        });
      }
    })
    .catch((error) => {
      console.error("Error fetching customers:", error);
    });
}

function fetchCustomerRanks(callback) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/earnpoints/rank`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const ranks = response.data.map((rank) => ({
          customerId: rank.userInfoDTO.id,
          rank: rank.customerTypeDTO.type,
          points: rank.point,
        }));
        callback(ranks);
      }
    })
    .catch((error) => {
      console.error("Error fetching customer ranks:", error);
      callback([]);
    });
}

function fetchSuppliers(page) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/userinfo/listsupplier?page=${page}`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const { suppliers, totalPages, currentPage } = response.data;
        populateTable(suppliers, [], currentPage, "Supplier");
        updatePagination(currentPage, totalPages, "supplier");
      }
    })
    .catch((error) => {
      console.error("Error fetching suppliers:", error);
    });
}

function populateTable(data, ranks, currentPage, role) {
  if (!data || !Array.isArray(data)) {
    console.error("Data is not an array or is undefined.");
    return;
  }

  const tableBody = $(`#${role.toLowerCase()}-table tbody`);
  tableBody.empty();
  let count = currentPage * 5 + 1;

  data.forEach((item) => {
    let rankInfo = "";
    if (role === "Customer") {
      const rankDetail = ranks.find((r) => r.customerId === item.id);
      const rank = rankDetail ? rankDetail.rank : "N/A";
      const points = rankDetail ? rankDetail.points : "N/A";
      rankInfo = `<td class="py-2 px-4 border-b">${rank} (${points} points)</td>`;
    }
    const row = `<tr class="text-center">
                  <td class="py-2 px-4 border-b">${count++}</td>
                  <td class="py-2 px-4 border-b" id="${role.toLowerCase()}-image-${item.id
      }">
                    Loading...
                  </td>
                  <td class="py-2 px-4 border-b">${item.fullName}</td>
                  <td class="py-2 px-4 border-b">${item.phoneNumber}</td>
                  <td class="py-2 px-4 border-b">${item.email}</td>
                  <td class="py-2 px-4 border-b">${item.address}</td>
                  ${rankInfo}
                  <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${item.id
      }"><i class="fas fa-edit"></i></button></td>
                </tr>`;
    tableBody.append(row);

    fetchImage(`${role.toLowerCase()}-image-${item.id}`, item.image);
  });

  setupEditButtons();
}

function updatePagination(currentPage, totalPages, type) {
  const prevPageId = `#prev-page-${type}`;
  const nextPageId = `#next-page-${type}`;
  const pageInfoId = `#page-info-${type}`;

  $(pageInfoId).text(`Page ${currentPage + 1} of ${totalPages}`);
  $(prevPageId)
    .off("click")
    .on("click", function () {
      if (currentPage > 0) {
        if (type === "customer") {
          fetchCustomers(currentPage - 1);
        } else {
          fetchSuppliers(currentPage - 1);
        }
      }
    });
  $(nextPageId)
    .off("click")
    .on("click", function () {
      if (currentPage < totalPages - 1) {
        if (type === "customer") {
          fetchCustomers(currentPage + 1);
        } else {
          fetchSuppliers(currentPage + 1);
        }
      }
    });
}

function setupEditButtons() {
  $(".edit-btn")
    .off("click")
    .on("click", function () {
      const id = $(this).data("id");
      fetchUserInfo(id);
      $("#updateUserModal").removeClass("hidden");
    });

  $("#close-update-modal")
    .off("click")
    .on("click", function () {
      $("#updateUserModal").addClass("hidden");
      clearImagePreview(); // Xóa hình ảnh khi đóng modal
      clearForm(); // Xóa dữ liệu trong form khi đóng modal
    });

  $("#updateEmployeeImageFile").on("change", function () {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        $("#updateEmployeeImagePreview").attr("src", e.target.result);
      };
      reader.readAsDataURL(file);
    }
  });

  $("#update-user-form")
    .off("submit")
    .on("submit", function (e) {
      e.preventDefault();
      updateUser();
    });
}

function fetchUserInfo(id) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/userinfo/findcustomer/${id}`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const user = response.data;
        $("#update-id").val(user.id);
        $("#update-fullname").val(user.fullName);
        $("#update-phone").val(user.phoneNumber);
        $("#update-email").val(user.email);
        $("#update-address").val(user.address);
        $("#update-role").val(user.role.id);
        $("#updateEmployeeImagePreview").attr("src", user.image);
      }
    })
    .catch((error) => {
      console.error("Error fetching user info:", error);
    });
}

function updateUser() {
  var formData = new FormData($("#update-user-form")[0]);

  var phoneNumber = $("#update-phone").val();
  var email = $("#update-email").val();
  var fullName = $("#update-fullname").val();
  var address = $("#update-address").val();
  var role = $("#update-role").val();

  // Validate form fields
  var validationMessage = validateFormUpdate({ phoneNumber, email, fullName, address, role });
  if (validationMessage) {
    showNotification(validationMessage, "Error");
    return;
  }

  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/userinfo/update`,
      "POST",
      formData
    )
    .then((response) => {
      if (response.status === "OK") {
        showNotification("User updated successfully!", "OK");

        $("#updateUserModal").addClass("hidden");
        clearImagePreview(); // Xóa hình ảnh sau khi cập nhật thành công
        clearForm(); // Xóa dữ liệu trong form sau khi cập nhật thành công
        const activeTab = $(".tab-button.border-l").data("role"); // Đảm bảo lấy đúng tab hiện tại

        if (activeTab === "CUSTOMER") {
          fetchCustomers(0);
        } else if (activeTab === "SUPPLIER") {
          fetchSuppliers(0);
        }
      } else {
        showNotification("Error updating user: " + response.desc, "OK");
      }
    })
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(
          error.responseJSON.desc,
          "Error"
        );
      } else {
        console.error("Error updating user:", error);
        showNotification("Error updating user!", "OK");
      }
    });
}

function validateFormUpdate({ phoneNumber, email, fullName, address, role }) {
  // Validate if fields are not empty
  if (!fullName.trim() || !phoneNumber.trim() || !email.trim() || !address.trim() || !role) {
    return "All fields must be filled";
  }

  // Validate phone number
  if (!isValidPhoneNumber(phoneNumber)) {
    return "Phone number must be 10 digits and contain no letters or special characters!";
  }

  // Validate email
  if (!isValidEmail(email)) {
    return "Invalid email address. Email must be in format";
  }

  // Validate full name
  if (!isValidFullName(fullName)) {
    return "Full name must contain no special characters or numbers and be appropriate for Vietnamese names!";
  }

  return null; // No errors
}

function clearImagePreview() {
  $("#updateEmployeeImagePreview").attr("src", ""); // Xóa hình ảnh
}

function clearForm() {
  $("#update-user-form")[0].reset(); // Đặt lại form
  $("#updateEmployeeImageFile").val(""); // Xóa giá trị của input file
}

function setupSearch(role) {
  // Thiết lập tìm kiếm cho một vai trò cụ thể
  $(`#${role.toLowerCase()}-criteria-button`)
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      $(`#${role.toLowerCase()}-criteria-menu`).toggleClass("hidden");
    });

  $(`#${role.toLowerCase()}-criteria-menu a`)
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $(this).data("criteria");
      $(`#${role.toLowerCase()}-selected-criteria`).text($(this).text());
      $(`#${role.toLowerCase()}-criteria-menu`).addClass("hidden");
      $(`#${role.toLowerCase()}-search-input`).data("criteria", criteria);
    });

  $(`#${role.toLowerCase()}-search-button`)
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $(`#${role.toLowerCase()}-search-input`).data("criteria");
      const query = $(`#${role.toLowerCase()}-search-input`).val();

      // Check if criteria is selected
      if (!criteria) {
        showNotification("Please select a search criteria !", "error");
        return;
      }
      // Check if search input is empty
      if (!query) {
        showNotification("Search input cannot be empty !", "error");
        return;
      }

      // Validate input based on criteria
      if (criteria === "numberphone") {
        if (!isValidPhoneNumber(query)) {
          showNotification("Invalid phone number. It must contain only digits and be 10 numbers long", "error");
          return;
        }
      } else if (criteria === "email") {
        if (!isValidEmail(query)) {
          showNotification("Invalid email address. Email must be in format", "error");
          return;
        }
      } else if (criteria === "id") {
        if (!/^\d+$/.test(query)) {
          showNotification("ID must be a number", "error");
          return;
        }
      } else if (criteria === "name") {
        // Added validation for name
        const vietnameseNameRegex = /^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+(\s[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+)*$/u;

        if (!vietnameseNameRegex.test(query)) {
          showNotification("Full name must contain no special characters or numbers and be appropriate for Vietnamese names!", "error");
          return;
        }
      }
      searchByRole(role, criteria, query, 0);
    });
}

function searchByRole(role, criteria, query, page) {
  console.log("Check para search by role: " + role);
  console.log("Check para search by role: " + criteria);
  console.log("Check para search by role: " + query);
  console.log("Check para search by role: " + page);

  const searchUrl =
    role === "CUSTOMER"
      ? `http://${userService.getApiUrl()}/api/userinfo/searchcustomer`
      : `http://${userService.getApiUrl()}/api/userinfo/searchsupplier`;

  const searchParams = new URLSearchParams({
    criteria: criteria,
    query: query,
    page: page,
  });

  userService
    .sendAjaxWithAuthen(searchUrl, "POST", searchParams.toString())
    .then((response) => {
      console.log("Response received:", response); // Log the response

      if (response.status === "OK") {
        const { customers, suppliers, totalPages, currentPage } = response.data;
        const data = role === "CUSTOMER" ? customers : suppliers;

        if (data.length === 0) {
          showNotification("User not found in system.", "error");
          return;
        }

        if (role === "CUSTOMER") {
          fetchCustomerRanks(function (ranks) {
            populateTable(data, ranks, currentPage, "Customer");
            updatePagination(currentPage, totalPages, "customer");
          });
        } else {
          populateTable(data, [], currentPage, "Supplier");
          updatePagination(currentPage, totalPages, "supplier");
        }
      } else {
        showNotification("Failed to search user data. " + response.desc, "error");
      }
    })
    .catch((error) => {
      console.error("Error details:", error); // Log detailed error information
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else if (error.statusText) {
        showNotification("Error while searching user data: " + error.statusText, "error");
      } else {
        showNotification("Error while searching user data.", "error");
      }
    });
}

function setupInsertModalToggle() {
  $(document).on("click", "#insert-button", function () {
    $("#insertUserModal").removeClass("hidden");
  });

  $("#close-insert-modal").on("click", function () {
    $("#insertUserModal").addClass("hidden");
    clearInsertForm(); // Xóa dữ liệu trong form khi đóng modal
  });

  // Lưu lại vai trò đã chọn khi thay đổi
  let selectedRole = "";

  $("#insert-role").on("change", function () {
    selectedRole = $(this).find("option:selected").text().toUpperCase();
  });

  // Cập nhật hình ảnh xem trước khi chọn hình ảnh
  $("#insert-file").on("change", function (e) {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (event) {
        $("#insertEmployeeImagePreview")
          .attr("src", event.target.result)
          .show();
      };
      reader.readAsDataURL(file);
    } else {
      $("#insertEmployeeImagePreview").attr("src", "#").hide();
    }
  });

  $("#insert-user-form")
    .off("submit")
    .on("submit", function (e) {
      e.preventDefault();

      const form = $(this);

      if (!validateForm(form)) return;

      var formData = new FormData($("#insert-user-form")[0]);

      // Thêm input file vào formData
      var fileInput = $("#insert-file")[0].files[0];
      if (fileInput) {
        formData.append("file", fileInput);
      }
      userService
        .sendAjaxWithAuthen(
          `http://${userService.getApiUrl()}/api/userinfo/insert`,
          "POST",
          formData
        )
        .then((response) => {
          if (response.status === "OK") {
            showNotification("Insert Successful !", "OK");
            $("#insertUserModal").addClass("hidden");
            clearInsertForm(); // Xóa các trường trong form
            switchTabByRole(selectedRole);
          } else {
            showNotification(
              response.desc,
              "error"
            );
          }
        })
        .catch((error) => {
          console.error("Error while insert user:", error);
          showNotification(
            (error.responseJSON ? error.responseJSON.desc : "System Error"),
            "error",
            "error"
          );
        });
    });
}

function clearInsertForm() {
  $("#insert-user-form")[0].reset(); // Đặt lại form
  $("#insertEmployeeImagePreview").attr("src", "#").hide(); // Xóa hình ảnh xem trước
}

function switchTabByRole(role) {
  switchTab(role); // Switch the tab first
  if (role === "CUSTOMER") {
    fetchCustomers(0);
  } else if (role === "SUPPLIER") {
    fetchSuppliers(0);
  }
}

function validateForm(form) {
  const fullName = form.find('input[name="fullName"]').val().trim();
  const email = form.find('input[name="email"]').val().trim();
  const phoneNumber = form.find('input[name="phoneNumber"]').val().trim();
  const address = form.find('input[name="address"]').val().trim();
  const role = form.find('select[name="roleId"]').val();

  // Check if any field is empty
  if (!fullName || !email || !phoneNumber || !address || !role) {
    showNotification("All fields are required", "error");
    return false;
  }

  // Validate full name
  if (!isValidFullName(fullName)) {
    showNotification("Full name must contain no special characters or numbers and be appropriate for Vietnamese names!", "error");
    return false;
  }

  // Validate email
  if (!isValidEmail(email)) {
    showNotification("Invalid email address. Email must be", "error");
    return false;
  }

  // Validate phone number
  if (!isValidPhoneNumber(phoneNumber)) {
    showNotification("Invalid phone number. It must contain only digits and be 10 numbers long", "error");
    return false;
  }

  return true;
}

function isValidEmail(email) {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailPattern.test(email);
}

function isValidPhoneNumber(phoneNumber) {
  var phoneRegex = /^[0-9]{10}$/;
  return phoneRegex.test(phoneNumber);
}

function isValidFullName(fullName) {
  // Biểu thức chính quy để kiểm tra tên đầy đủ tiếng Việt không chứa số và ký tự đặc biệt
  var fullNameRegex = /^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+(\s[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯàáâãèéêìíòóôõùúăđĩũơưĂẮẰẲẴẶẤẦẨẪẬẮẰẲẴẶÉẾỀỂỄỆÍÌỈĨỊỈÌỊÉÊÍÒÓÔÕÙÚỦŨỤƯỨỪỬỮỰÝỲỶỸỴỹýỳỵỷỹỵơớờởỡợợáạảãàâấầẩậẫắằẳẵặèéẹẻẽêếềểễệìíỉĩịòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựýỳỷỹỵỵ]+)*$/u;
  return fullNameRegex.test(fullName);
}

// Fetch rank data and populate the table
function fetchUniqueRankData() {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/customertype/findall`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        var tableBody = $("#rankTableBody");
        tableBody.empty(); // Xóa dữ liệu cũ

        var displayId = 1; // Bắt đầu từ 1

        response.data.forEach(function (rank) {
          var row =
            "<tr>" +
            '<td class="px-4 py-2 border">' +
            displayId +
            "</td>" + // Hiển thị ID tăng dần
            '<td class="px-4 py-2 border">' +
            rank.type +
            "</td>" +
            '<td class="px-4 py-2 border">' +
            rank.pointCondition +
            "</td>" +
            '<td class="px-4 py-2 border text-center">' +
            '<button class="edit-unique-btn text-blue-500 hover:text-blue-700" data-id="' +
            rank.id +
            '" data-type="' +
            rank.type +
            '" data-pointcondition="' +
            rank.pointCondition +
            '">' +
            '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">' +
            '<path d="M17.414 2.586a2 2 0 010 2.828l-10 10a2 2 0 01-.878.515l-4 1a1 1 0 01-1.263-1.263l1-4a2 2 0 01.515-.878l10-10a2 2 0 012.828 0zM5 13l-1 4 4-1 10-10-3-3L5 13zM3 17h2v2H3v-2z" />' +
            "</svg>" +
            "</button>" +
            "</td>" +
            "</tr>";
          tableBody.append(row);
          displayId++; // Tăng ID hiển thị
        });

        // Gắn sự kiện click cho các nút chỉnh sửa
        attachUniqueEditButtonEvents();
        toggleButtonVisibility(userRole);
      } else {
        console.error("Error loading customer types:", response.desc);
      }
    })
    .catch((error) => {
      console.error("There was an error fetching the rank data: ", error);
    });
}

// Gắn sự kiện click vào các nút chỉnh sửa
function attachUniqueEditButtonEvents() {
  $(".edit-unique-btn").click(function () {
    var id = $(this).data("id");
    var type = $(this).data("type");
    var pointCondition = $(this).data("pointcondition");

    // Điền dữ liệu hiện có vào form
    $("#updateUniqueId").val(id);
    $("#updateUniqueType").val(type);
    $("#updateUniquePointCondition").val(pointCondition);

    // Hiển thị modal cập nhật
    $("#updateCustomerTypeModal").removeClass("hidden");
  });
}

// Update customer type
function updateUniqueCustomerType() {
  $("#updateCustomerTypeForm").submit(function (e) {
    e.preventDefault();

    var id = $("#updateUniqueId").val();
    var type = $("#updateUniqueType").val();
    var pointCondition = $("#updateUniquePointCondition").val();

    // Tạo đối tượng dữ liệu để gửi
    const formData = new URLSearchParams({
      id: id,
      type: type,
      pointCondition: pointCondition,
    });

    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/customertype/updatepointcondition`,
        "POST",
        formData.toString() // Chuyển đổi đối tượng URLSearchParams thành chuỗi
      )
      .then((response) => {
        showNotification(response.desc, "OK");
        $("#updateCustomerTypeModal").addClass("hidden");
        fetchUniqueRankData(); // Làm mới dữ liệu trong modal chính
        fetchCustomers(0);
        fetchSuppliers(0);
      })
      .catch((error) => {
        console.error("There was an error updating the rank data: ", error);
      });
  });
}

// Open customer type modal
function openUniqueCustomerTypeModal() {
  fetchUniqueRankData();
  $("#customerTypeModal").removeClass("hidden");
}

// Close customer type modal
function closeUniqueCustomerTypeModal() {
  $("#customerTypeModal").addClass("hidden");
}

// Close update customer type modal
function closeUpdateUniqueCustomerTypeModal() {
  $("#updateCustomerTypeModal").addClass("hidden");
}

// Initialize event listeners
function initializeUniqueEventListeners() {
  $("#managerRank").click(openUniqueCustomerTypeModal);
  $("#closeCustomerTypeModal, #closeCustomerTypeModalFooter").click(
    closeUniqueCustomerTypeModal
  );
  $("#closeUpdateCustomerTypeModal").click(closeUpdateUniqueCustomerTypeModal);
}

// Initialize all functionalities
function initializeUnique() {
  initializeUniqueEventListeners();
  deleteUniqueCustomerType();
  updateUniqueCustomerType();
  initializeAddCustomerTypeEventListeners();
  addUniqueCustomerType();
}

// Initialize event listeners
function initializeAddCustomerTypeEventListeners() {
  attachAddCustomerTypeEvent();
  closeAddCustomerTypeModal();
}

// Attach click event to the add button
function attachAddCustomerTypeEvent() {
  $("#addCustomerType").click(function () {
    $("#addCustomerTypeModal").removeClass("hidden");
  });
}

// Add new customer type
function addUniqueCustomerType() {
  $("#addCustomerTypeForm").submit(function (e) {
    e.preventDefault();

    var type = $("#addType").val();
    var pointCondition = $("#addPointCondition").val();

    // Tạo đối tượng dữ liệu để gửi
    const formData = new URLSearchParams({
      type: type,
      pointCondition: pointCondition,
    });

    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/customertype/add`,
        "POST",
        formData.toString() // Chuyển đổi đối tượng URLSearchParams thành chuỗi
      )
      .then((response) => {
        showNotification("Customer Type added successfully!", "OK");

        $("#addCustomerTypeModal").addClass("hidden");
        fetchUniqueRankData(); // Làm mới dữ liệu trong modal chính
        fetchCustomers(0);
        fetchSuppliers(0);
      })
      .catch((error) => {
        console.error("There was an error adding the customer type: ", error);
      });
  });
}

// Close add customer type modal
function closeAddCustomerTypeModal() {
  $("#closeAddCustomerTypeModal").click(function () {
    $("#addCustomerTypeModal").addClass("hidden");
  });
}

// Delete customer type
function deleteUniqueCustomerType() {
  $("#deleteCustomerType").click(function () {
    var id = $("#updateUniqueId").val();

    // Show confirmation dialog
    var isConfirmed = confirm(
      "Are you sure you want to delete this customer type?"
    );

    if (isConfirmed) {
      // Tạo đối tượng dữ liệu để gửi
      const formData = new URLSearchParams({
        customerTypeId: id,
      });

      userService
        .sendAjaxWithAuthen(
          `http://${userService.getApiUrl()}/api/customertype/delete`,
          "POST",
          formData.toString(), // Chuyển đổi đối tượng URLSearchParams thành chuỗi
          { "Content-Type": "application/x-www-form-urlencoded" } // Đặt loại nội dung là URL-encoded
        )
        .then((response) => {
          showNotification("Delete successful!", "OK");
          $("#updateCustomerTypeModal").addClass("hidden");
          fetchUniqueRankData(); // Làm mới dữ liệu trong modal chính
          fetchCustomers(0);
          fetchSuppliers(0);
        })
        .catch((error) => {
          console.error(
            "There was an error deleting the customer type: ",
            error
          );
        });
    }
  });
}

function initializeTabs(roles) {
  if (!Array.isArray(roles)) {
    console.error("Roles is not an array or is undefined");
    return;
  }

  roles.forEach(role => {
    const tabContent = createTabContent(role);
    $('body').append(tabContent);
    addFindAllEventListener(role);
  });
}

function addFindAllEventListener(role) {
  $(document).on('click', `#${role.name.toLowerCase()}-find-all-button`, function () {
    handleFindAllClick(role);
  });
}

function handleFindAllClick(role) {
  if (role.name === "CUSTOMER") {
    fetchCustomers(0);
  } else if (role.name === "SUPPLIER") {
    fetchSuppliers(0);
  }
}

