$(document).ready(function () {
  fetchRoles();
  setupModalToggle();
  setupInsertModalToggle();
  setupInsertRoleModalToggle();
});

function fetchRoles() {
  $.ajax({
    url: "http://localhost:8080/role/list",
    method: "GET",
    success: function (response) {
      if (response.status === "OK") {
        const roles = response.data.filter(
          (role) => !["STAFF", "ADMIN", "MANAGER"].includes(role.name)
        );
        initTabs(roles);
        populateRoleSelect(roles, "#update-role"); // Populate role select for update modal
        populateRoleSelect(roles, "#insert-role");
      }
    },
    error: function (error) {
      console.error("Error fetching roles:", error);
    },
  });
}

function initTabs(roles) {
  roles.forEach((role) => {
    $("#role-tabs").append(createTab(role));
    $("#tab-contents").append(createTabContent(role));
  });

  // Append the insert role button
  $("#role-tabs").append(
    '<li class="ml-2"><button id="insert-role-button" class="tab-button bg-green-500 text-white font-semibold py-2 px-4 rounded hover:bg-green-700 transition duration-300">+</button></li>'
  );
  // Append the insert user button
  $("#role-tabs").append(
    '<li class="ml-auto"><button id="insert-button" class="tab-button bg-blue-500 text-white font-semibold py-2 px-4 rounded hover:bg-blue-700 transition duration-300">Insert User</button></li>'
  );

  bindTabClickEvents();
  const firstRole = roles[0].name;
  switchTab(firstRole);
  if (firstRole === "CUSTOMER") {
    fetchCustomers(0);
  } else if (firstRole === "SUPPLIER") {
    fetchSuppliers(0);
  }

  setupInsertRoleModalToggle(); // Setup insert role modal toggle for the new insert button
}

function populateRoleSelect(roles, selector) {
  const roleSelect = $(selector);
  roleSelect.empty();
  roles.forEach((role) => {
    roleSelect.append(`<option value="${role.id}">${role.name}</option>`);
  });
}

function setupModalToggle() {
  $("#close-modal").on("click", function () {
    $("#addRoleModal").addClass("hidden");
  });

  $("#add-role-form").on("submit", function (e) {
    e.preventDefault();
    $.ajax({
      url: "http://localhost:8080/role/insert",
      method: "POST",
      contentType: "application/x-www-form-urlencoded",
      data: $(this).serialize(),
      success: function (response) {
        alert(response);
        $("#addRoleModal").addClass("hidden");
        fetchRoles();
      },
      error: function (error) {
        console.error("Error adding role:", error);
        alert("Error adding role!");
      },
    });
  });

  $("#close-update-modal").on("click", function () {
    $("#updateUserModal").addClass("hidden");
  });

  $("#update-user-form")
    .off("submit")
    .on("submit", function (e) {
      // Use .off('submit') to prevent multiple bindings
      e.preventDefault();
      updateUser();
    });
}

function createTab(role) {
  return `<li class="mr-1">
              <a class="tab-button bg-white inline-block py-2 px-4 font-semibold" data-role="${role.name}" href="#">${role.name}</a>
            </li>`;
}

function createTabContent(role) {
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
                <button id="${role.name.toLowerCase()}-search-button" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded ml-2">Search</button>
            </div>
        </div>
        <table id="${role.name.toLowerCase()}-table" class="min-w-full bg-white">
            <thead>
                <tr>
                    <th class="py-2 px-4 border-b">ID</th>
                    <th class="py-2 px-4 border-b">Image</th>
                    <th class="py-2 px-4 border-b">Full Name</th>
                    <th class="py-2 px-4 border-b">Phone Number</th>
                    <th class="py-2 px-4 border-b">Email</th>
                    <th class="py-2 px-4 border-b">Address</th>
                    <th class="py-2 px-4 border-b">Action</th>
                </tr>
            </thead>
            <tbody>
                <!-- ${role.name} rows will be dynamically added here -->
            </tbody>
        </table>
        <div id="pagination-${role.name.toLowerCase()}" class="mt-4">
            <button id="prev-page-${role.name.toLowerCase()}" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Previous</button>
            <span id="page-info-${role.name.toLowerCase()}" class="mx-2"></span>
            <button id="next-page-${role.name.toLowerCase()}" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Next</button>
        </div>
    `;

  return `<div id="${role.name}" class="tab-content" style="display: none;">${commonContent}</div>`;
}

function bindTabClickEvents() {
  $(".tab-button").on("click", function (event) {
    event.preventDefault();
    const role = $(this).data("role");
    switchTab(role);
    if (role === "CUSTOMER") {
      fetchCustomers(0);
    } else if (role === "SUPPLIER") {
      fetchSuppliers(0);
    }
  });
}

function switchTab(role) {
  $(".tab-content").hide();
  $("#" + role).show();
  setActiveTab(role);
  setupSearch(); // Ensure search setup is called every time a tab is switched
}

function setActiveTab(role) {
  $(".tab-button").removeClass(
    "border-l border-t border-r rounded-t text-blue-700 font-semibold"
  );
  $(`.tab-button[data-role=${role}]`).addClass(
    "border-l border-t border-r rounded-t text-blue-700 font-semibold"
  );
}

function fetchCustomers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listcustomer?page=${page}`,
    method: "GET",
    success: function (response) {
      if (response.status === 200) {
        const { customers, totalPages, currentPage } = response.data;
        populateCustomerTable(customers, currentPage);
        updatePagination(currentPage, totalPages);
      }
    },
    error: function (error) {
      console.error("Error fetching customers:", error);
    },
  });
}

function fetchSuppliers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listsupplier?page=${page}`,
    method: "GET",
    success: function (response) {
      if (response.status === "200") {
        const { customers: suppliers, totalPages, currentPage } = response.data;
        populateSupplierTable(suppliers, currentPage);
        updatePaginationSupplier(currentPage, totalPages);
      }
    },
    error: function (error) {
      console.error("Error fetching suppliers:", error);
    },
  });
}

function populateCustomerTable(customers, currentPage) {
  const tableBody = $("#customer-table tbody");
  tableBody.empty();
  let count = currentPage * 5 + 1;
  customers.forEach((customer) => {
    const row = `<tr class="text-center">
                    <td class="py-2 px-4 border-b">${count++}</td>
                    <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${
                      customer.image
                    }" alt="${customer.fullName}" class="h-10 w-10"></td>
                    <td class="py-2 px-4 border-b">${customer.fullName}</td>
                    <td class="py-2 px-4 border-b">${customer.phoneNumber}</td>
                    <td class="py-2 px-4 border-b">${customer.email}</td>
                    <td class="py-2 px-4 border-b">${customer.address}</td>
                    <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${
                      customer.id
                    }"><i class="fas fa-edit"></i></button></td>
                  </tr>`;
    tableBody.append(row);
  });

  setupEditButtons();
}

function populateSupplierTable(suppliers, currentPage) {
  const tableBody = $("#supplier-table tbody");
  tableBody.empty();
  let count = currentPage * 5 + 1;
  suppliers.forEach((supplier) => {
    console.log("Supplier:", supplier); // Debug log
    const row = `<tr class="text-center">
                    <td class="py-2 px-4 border-b">${count++}</td>
                    <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${
                      supplier.image
                    }" alt="${supplier.fullName}" class="h-10 w-10"></td>
                    <td class="py-2 px-4 border-b">${supplier.fullName}</td>
                    <td class="py-2 px-4 border-b">${supplier.phoneNumber}</td>
                    <td class="py-2 px-4 border-b">${supplier.email}</td>
                    <td class="py-2 px-4 border-b">${supplier.address}</td>
                    <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${
                      supplier.id
                    }"><i class="fas fa-edit"></i></button></td>
                  </tr>`;
    tableBody.append(row);
  });

  setupEditButtons();
}

function updatePagination(currentPage, totalPages) {
  $("#page-info").text(`Page ${currentPage + 1} of ${totalPages}`);
  $("#prev-page")
    .off("click")
    .on("click", function () {
      if (currentPage > 0) {
        fetchCustomers(currentPage - 1);
      }
    });
  $("#next-page")
    .off("click")
    .on("click", function () {
      if (currentPage < totalPages - 1) {
        fetchCustomers(currentPage + 1);
      }
    });
}

function updatePaginationSupplier(currentPage, totalPages) {
  $("#page-info-supplier").text(`Page ${currentPage + 1} of ${totalPages}`);
  $("#prev-page-supplier")
    .off("click")
    .on("click", function () {
      if (currentPage > 0) {
        fetchSuppliers(currentPage - 1);
      }
    });
  $("#next-page-supplier")
    .off("click")
    .on("click", function () {
      if (currentPage < totalPages - 1) {
        fetchSuppliers(currentPage + 1);
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
    });

  $("#update-user-form")
    .off("submit")
    .on("submit", function (e) {
      // Use .off('submit') to prevent multiple bindings
      e.preventDefault();
      updateUser();
    });
}

function fetchUserInfo(id) {
  $.ajax({
    url: `http://localhost:8080/userinfo/${id}`,
    method: "GET",
    success: function (response) {
      if (response.status === 200) {
        const user = response.data;
        $("#update-id").val(user.id);
        $("#update-fullname").val(user.fullName);
        $("#update-phone").val(user.phoneNumber);
        $("#update-email").val(user.email);
        $("#update-address").val(user.address);
        $("#update-role").val(user.role.id);
      }
    },
    error: function (error) {
      console.error("Error fetching user info:", error);
    },
  });
}

function updateUser() {
  var formData = new FormData($("#update-user-form")[0]);
  $.ajax({
    url: "http://localhost:8080/userinfo/update",
    method: "POST",
    processData: false,
    contentType: false,
    data: formData,
    success: function (response) {
      if (response.status === 200) {
        alert("User updated successfully!");
        $("#updateUserModal").addClass("hidden");
        if ($("#CUSTOMER").is(":visible")) {
          fetchCustomers(0);
        } else if ($("#SUPPLIER").is(":visible")) {
          fetchSuppliers(0);
        }
      }
    },
    error: function (error) {
      console.error("Error updating user:", error);
      alert("Error updating user!");
    },
  });
}

function setupSearch() {
  // Customer Search
  $("#criteria-button")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      $("#criteria-menu").toggleClass("hidden");
    });

  $("#criteria-menu a")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $(this).data("criteria");
      $("#selected-criteria").text($(this).text());
      $("#criteria-menu").addClass("hidden");
      $("#search-input").data("criteria", criteria);
    });

  $("#search-button")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $("#search-input").data("criteria");
      const query = $("#search-input").val();
      searchCustomers(criteria, query, 0);
    });

  // Supplier Search
  $("#supplier-criteria-button")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      $("#supplier-criteria-menu").toggleClass("hidden");
    });

  $("#supplier-criteria-menu a")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $(this).data("criteria");
      $("#supplier-selected-criteria").text($(this).text());
      $("#supplier-criteria-menu").addClass("hidden");
      $("#supplier-search-input").data("criteria", criteria);
    });

  $("#supplier-search-button")
    .off("click")
    .on("click", function (e) {
      e.preventDefault();
      const criteria = $("#supplier-search-input").data("criteria");
      const query = $("#supplier-search-input").val();
      searchSuppliers(criteria, query, 0);
    });
}

function searchCustomers(criteria, query, page) {
  $.ajax({
    url: "http://localhost:8080/userinfo/searchcustomer",
    method: "POST",
    data: {
      criteria: criteria,
      query: query,
      page: page,
    },
    success: function (response) {
      if (response.status === 200) {
        const { customers, totalPages, currentPage } = response.data;
        populateCustomerTable(customers, currentPage);
        updatePagination(currentPage, totalPages);
      }
    },
    error: function (error) {
      console.error("Error searching customers:", error);
    },
  });
}

function searchSuppliers(criteria, query, page) {
  $.ajax({
    url: "http://localhost:8080/userinfo/searchsupplier",
    method: "POST",
    data: {
      criteria: criteria,
      query: query,
      page: page,
    },
    success: function (response) {
      if (response.status === 200) {
        const { suppliers, totalPages, currentPage } = response.data;
        console.log(response.data);
        console.log("Suppliers:", suppliers); // Debug log
        console.log("Current Page:", currentPage); // Debug log
        console.log("Total Pages:", totalPages); // Debug log

        populateSupplierTable(suppliers, currentPage);
        updatePaginationSupplier(currentPage, totalPages);
      }
    },
    error: function (error) {
      console.error("Error searching suppliers:", error);
    },
  });
}

function setupInsertModalToggle() {
  $(document).on("click", "#insert-button", function () {
    $("#insertUserModal").removeClass("hidden");
  });

  $("#close-insert-modal").on("click", function () {
    $("#insertUserModal").addClass("hidden");
  });

  // Store the selected role when it changes
  let selectedRole = "";

  $("#insert-role").on("change", function () {
    selectedRole = $(this).find("option:selected").text().toUpperCase();
  });

  $("#insert-user-form")
    .off("submit")
    .on("submit", function (e) {
      e.preventDefault();
      var formData = new FormData($("#insert-user-form")[0]);
      $.ajax({
        url: "http://localhost:8080/userinfo/insert",
        method: "POST",
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
          if (response.status === 200) {
            alert("User inserted successfully!");
            $("#insertUserModal").addClass("hidden");
            clearInsertForm(); // Clear the form fields
            console.log("De test chuyen trang :" + selectedRole);
            switchTabByRole(selectedRole);
          } else {
            alert("Error inserting user: " + response.desc);
          }
        },
        error: function (error) {
          console.error("Error inserting user:", error);
          alert("Error inserting user!");
        },
      });
    });
}

function setupInsertRoleModalToggle() {
  $("#insert-role-button").on("click", function () {
    $("#addRoleModal").removeClass("hidden");
  });

  $("#close-role-modal").on("click", function () {
    $("#addRoleModal").addClass("hidden");
  });

  $("#add-role-form")
    .off("submit")
    .on("submit", function (e) {
      e.preventDefault();
      var formData = new FormData($("#add-role-form")[0]);
      $.ajax({
        url: "http://localhost:8080/role/insert",
        method: "POST",
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
          if (response.status === 200) {
            alert("Role inserted successfully!");
            $("#addRoleModal").addClass("hidden");
            fetchRoles(); // Refresh the roles
          } else {
            alert("Error inserting role: " + response.desc);
          }
        },
        error: function (error) {
          console.error("Error inserting role:", error);
          alert("Error inserting role!");
        },
      });
    });
}

function clearInsertForm() {
  $("#insert-user-form")[0].reset();
}

function switchTabByRole(role) {
  if (role === "CUSTOMER") {
    switchTab("CUSTOMER");
    fetchCustomers(0);
  } else if (role === "SUPPLIER") {
    switchTab("SUPPLIER");
    fetchSuppliers(0);
  }
}
