$(document).ready(function () {
  fetchRoles();
  setupModalToggle();
  setupInsertModalToggle();
  setupInsertRoleModalToggle();
});

function fetchRoles() {
  $.ajax({
    url: 'http://localhost:8080/role/list',
    method: 'GET',
    success: function (response) {
      if (response.status === "OK") {
        const roles = response.data.filter(role => !['STAFF', 'ADMIN', 'MANAGER'].includes(role.name));
        initTabs(roles);
        populateRoleSelect(roles, '#update-role'); // Populate role select for update modal
        populateRoleSelect(roles, '#insert-role');
      } else {
        alert("Fail to load Role");
      }
    },
    error: function (error) {
      console.error('Error fetching roles:', error);
    }
  });
}

function initTabs(roles) {
  roles.forEach(role => {
    $('#role-tabs').append(createTab(role));
    $('#tab-contents').append(createTabContent(role));
  });

  // Append the insert role button
  $('#role-tabs').append('<li class="ml-2"><button id="insert-role-button" class="tab-button bg-green-500 text-white font-semibold py-2 px-4 rounded hover:bg-green-700 transition duration-300">+</button></li>');
  // Append the insert user button
  $('#role-tabs').append('<li class="ml-auto"><button id="insert-button" class="tab-button bg-blue-500 text-white font-semibold py-2 px-4 rounded hover:bg-blue-700 transition duration-300">Insert User</button></li>');

  bindTabClickEvents();
  const firstRole = roles[0].name;
  console.log("First role to switch to:", firstRole);
  switchTabByRole(firstRole); // Ensure first tab is activated
  setupInsertRoleModalToggle(); // Setup insert role modal toggle for the new insert button
}

function populateRoleSelect(roles, selector) {
  const roleSelect = $(selector);
  roleSelect.empty();
  roleSelect.append(`<option value="" disabled selected>Select a role</option>`); // Add default option
  roles.forEach(role => {
    roleSelect.append(`<option value="${role.id}">${role.name}</option>`);
  });
  console.log("Roles populated in select:", selector, roles); // Debug log
}


function setupModalToggle() {
  $('#close-modal').on('click', function () {
    $('#addRoleModal').addClass('hidden');
  });

  $('#add-role-form').on('submit', function (e) {
    e.preventDefault();
    $.ajax({
      url: 'http://localhost:8080/role/insert',
      method: 'POST',
      contentType: 'application/x-www-form-urlencoded',
      data: $(this).serialize(),
      success: function (response) {
        alert(response);
        $('#addRoleModal').addClass('hidden');
        fetchRoles();
      },
      error: function (error) {
        console.error('Error adding role:', error);
        alert('Error adding role!');
      }
    });
  });

  $('#close-update-modal').on('click', function () {
    $('#updateUserModal').addClass('hidden');
  });

  $('#update-user-form').off('submit').on('submit', function (e) {
    e.preventDefault();
    const form = $(this);

    if (!validateForm(form)) return;
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
  $('.tab-button').on('click', function (event) {
    event.preventDefault();
    const role = $(this).data('role');
    switchTabByRole(role);
  });
}

function switchTab(role) {
  console.log("Switching to tab:", role); // Debug log

  if (!role) {
    console.error("Role is undefined"); // Log an error if role is undefined
    return;
  }

  console.log("Switching to tab:", role); // Debug log
  $('.tab-content').hide();
  $('#' + role.replace(/\s+/g, '')).show();
  setActiveTab(role);
  setupSearch(role); // Ensure search setup is called every time a tab is switched
}

function setActiveTab(role) {
  $('.tab-button').removeClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
  $(`.tab-button[data-role=${role}]`).addClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
}

function fetchCustomers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listcustomer?page=${page}`,
    method: 'GET',
    success: function (response) {
      if (response.status === "OK") {
        const { customers, totalPages, currentPage } = response.data;
        populateCustomerTable(customers, currentPage);
        updatePagination(currentPage, totalPages, 'customer');
      }
    },
    error: function (error) {
      console.error('Error fetching customers:', error);
    }
  });
}

function fetchSuppliers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listsupplier?page=${page}`,
    method: 'GET',
    success: function (response) {
      if (response.status === "OK") {
        const { customers: suppliers, totalPages, currentPage } = response.data;
        populateSupplierTable(suppliers, currentPage);
        updatePagination(currentPage, totalPages, 'supplier');
      }
    },
    error: function (error) {
      console.error('Error fetching suppliers:', error);
    }
  });
}

function populateCustomerTable(customers, currentPage) {
  const tableBody = $('#customer-table tbody');
  tableBody.empty();
  let count = currentPage * 5 + 1;
  customers.forEach(customer => {
    const row = `<tr class="text-center">
                  <td class="py-2 px-4 border-b">${count++}</td>
                  <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${customer.image}" alt="${customer.fullName}" class="h-10 w-10"></td>
                  <td class="py-2 px-4 border-b">${customer.fullName}</td>
                  <td class="py-2 px-4 border-b">${customer.phoneNumber}</td>
                  <td class="py-2 px-4 border-b">${customer.email}</td>
                  <td class="py-2 px-4 border-b">${customer.address}</td>
                  <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${customer.id}"><i class="fas fa-edit"></i></button></td>
                </tr>`;
    tableBody.append(row);
  });

  setupEditButtons();
}

function populateSupplierTable(suppliers, currentPage) {
  const tableBody = $('#supplier-table tbody');
  tableBody.empty();
  let count = currentPage * 5 + 1;
  suppliers.forEach(supplier => {
    const row = `<tr class="text-center">
                  <td class="py-2 px-4 border-b">${count++}</td>
                  <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${supplier.image}" alt="${supplier.fullName}" class="h-10 w-10"></td>
                  <td class="py-2 px-4 border-b">${supplier.fullName}</td>
                  <td class="py-2 px-4 border-b">${supplier.phoneNumber}</td>
                  <td class="py-2 px-4 border-b">${supplier.email}</td>
                  <td class="py-2 px-4 border-b">${supplier.address}</td>
                  <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${supplier.id}"><i class="fas fa-edit"></i></button></td>
                </tr>`;
    tableBody.append(row);
  });

  setupEditButtons();
}

function updatePagination(currentPage, totalPages, type) {
  const paginationId = `#pagination-${type}`;
  const prevPageId = `#prev-page-${type}`;
  const nextPageId = `#next-page-${type}`;
  const pageInfoId = `#page-info-${type}`;

  $(pageInfoId).text(`Page ${currentPage + 1} of ${totalPages}`);
  $(prevPageId).off('click').on('click', function () {
    if (currentPage > 0) {
      if (type === 'customer') {
        fetchCustomers(currentPage - 1);
      } else {
        fetchSuppliers(currentPage - 1);
      }
    }
  });
  $(nextPageId).off('click').on('click', function () {
    if (currentPage < totalPages - 1) {
      if (type === 'customer') {
        fetchCustomers(currentPage + 1);
      } else {
        fetchSuppliers(currentPage + 1);
      }
    }
  });
}

function setupEditButtons() {
  $('.edit-btn').off('click').on('click', function () {
    const id = $(this).data('id');
    fetchUserInfo(id);
    $('#updateUserModal').removeClass('hidden');
  });

  $('#close-update-modal').off('click').on('click', function () {
    $('#updateUserModal').addClass('hidden');
  });

  $('#update-user-form').off('submit').on('submit', function (e) {
    e.preventDefault();
    updateUser();
  });
}

function fetchUserInfo(id) {
  $.ajax({
    url: `http://localhost:8080/userinfo/${id}`,
    method: 'GET',
    success: function (response) {
      if (response.status === "OK") {
        const user = response.data;
        $('#update-id').val(user.id);
        $('#update-fullname').val(user.fullName);
        $('#update-phone').val(user.phoneNumber);
        $('#update-email').val(user.email);
        $('#update-address').val(user.address);
        $('#update-role').val(user.role.id);
      }
    },
    error: function (error) {
      console.error('Error fetching user info:', error);
    }
  });
}

function updateUser() {
  var formData = new FormData($("#update-user-form")[0]);
  $.ajax({
    url: 'http://localhost:8080/userinfo/update',
    method: 'POST',
    processData: false,
    contentType: false,
    data: formData,
    success: function (response) {
      if (response.status === "OK") {
        alert('User updated successfully!');
        $('#updateUserModal').addClass('hidden');
        const activeTab = $('.tab-button.active').data('role');
        if (activeTab === 'CUSTOMER') {
          fetchCustomers(0);
        } else if (activeTab === 'SUPPLIER') {
          fetchSuppliers(0);
        }
      }
    },
    error: function (error) {
      console.error('Error updating user:', error);
      alert('Error updating user!');
    }
  });
}

function setupSearch(role) {
  // Setup search for a specific role
  $(`#${role.toLowerCase()}-criteria-button`).off('click').on('click', function (e) {
    e.preventDefault();
    $(`#${role.toLowerCase()}-criteria-menu`).toggleClass('hidden');
  });

  $(`#${role.toLowerCase()}-criteria-menu a`).off('click').on('click', function (e) {
    e.preventDefault();
    const criteria = $(this).data('criteria');
    $(`#${role.toLowerCase()}-selected-criteria`).text($(this).text());
    $(`#${role.toLowerCase()}-criteria-menu`).addClass('hidden');
    $(`#${role.toLowerCase()}-search-input`).data('criteria', criteria);
  });

  $(`#${role.toLowerCase()}-search-button`).off('click').on('click', function (e) {
    e.preventDefault();
    const criteria = $(`#${role.toLowerCase()}-search-input`).data('criteria');
    const query = $(`#${role.toLowerCase()}-search-input`).val();
    if (role === 'CUSTOMER') {
      searchCustomers(criteria, query, 0);
    } else if (role === 'SUPPLIER') {
      searchSuppliers(criteria, query, 0);
    }
  });
}

function searchCustomers(criteria, query, page) {
  $.ajax({
    url: 'http://localhost:8080/userinfo/searchcustomer',
    method: 'POST',
    data: {
      criteria: criteria,
      query: query,
      page: page
    },
    success: function (response) {
      if (response.status === "OK") {
        const { customers, totalPages, currentPage } = response.data;
        populateCustomerTable(customers, currentPage);
        updatePagination(currentPage, totalPages, 'customer');
      }
    },
    error: function (error) {
      console.error('Error searching customers:', error);
    }
  });
}

function searchSuppliers(criteria, query, page) {
  $.ajax({
    url: 'http://localhost:8080/userinfo/searchsupplier',
    method: 'POST',
    data: {
      criteria: criteria,
      query: query,
      page: page
    },
    success: function (response) {
      if (response.status === "OK") {
        const { suppliers, totalPages, currentPage } = response.data;
        populateSupplierTable(suppliers, currentPage);
        updatePagination(currentPage, totalPages, 'supplier');
      }
    },
    error: function (error) {
      console.error('Error searching suppliers:', error);
    }
  });
}

function setupInsertModalToggle() {
  $(document).on('click', '#insert-button', function () {
    $('#insertUserModal').removeClass('hidden');
  });

  $('#close-insert-modal').on('click', function () {
    $('#insertUserModal').addClass('hidden');
  });

  // Store the selected role when it changes
  let selectedRole = '';

  $('#insert-role').on('change', function () {
    selectedRole = $(this).find('option:selected').text().toUpperCase();
    console.log("Selected role on change:", selectedRole);
  });

  $('#insert-user-form').off('submit').on('submit', function (e) {
    e.preventDefault();

    const form = $(this);

    if (!validateForm(form)) return;

    var formData = new FormData($("#insert-user-form")[0]);
    $.ajax({
      url: 'http://localhost:8080/userinfo/insert',
      method: 'POST',
      processData: false,
      contentType: false,
      data: formData,
      success: function (response) {
        if (response.status === "OK") {
          alert('User inserted successfully!');
          $('#insertUserModal').addClass('hidden');
          clearInsertForm(); // Clear the form fields
          switchTabByRole(selectedRole);
        } else {
          alert('Error inserting user: ' + response.desc);
        }
      },
      error: function (error) {
        console.error('Error inserting user:', error);
        alert('Error inserting user: ' + (error.responseJSON ? error.responseJSON.desc : 'Internal Server Error'));
      }
    });
  });
}

function setupInsertRoleModalToggle() {
  $('#insert-role-button').on('click', function () {
    $('#addRoleModal').removeClass('hidden');
  });

  $('#close-role-modal').on('click', function () {
    $('#addRoleModal').addClass('hidden');
  });

  $('#add-role-form').off('submit').on('submit', function (e) {
    e.preventDefault();
    var formData = new FormData($("#add-role-form")[0]);
    $.ajax({
      url: 'http://localhost:8080/role/insert',
      method: 'POST',
      processData: false,
      contentType: false,
      data: formData,
      success: function (response) {
        if (response.status === "OK") {
          alert('Role inserted successfully!');
          $('#addRoleModal').addClass('hidden');
          fetchRoles(); // Refresh the roles
        } else {
          alert('Error inserting role: ' + response.desc);
        }
      },
      error: function (error) {
        console.error('Error inserting role:', error);
        alert('Error inserting role!');
      }
    });
  });
}

function clearInsertForm() {
  $('#insert-user-form')[0].reset();
}

function switchTabByRole(role) {
  switchTab(role); // Switch the tab first
  if (role === 'CUSTOMER') {
    fetchCustomers(0);
  } else if (role === 'SUPPLIER') {
    fetchSuppliers(0);
  }
}

function validateForm(form) {
  const email = form.find('input[name="email"]').val();
  const phoneNumber = form.find('input[name="phoneNumber"]').val();

  if (!isValidEmail(email)) {
    alert('Invalid email address.');
    return false;
  }

  if (!isValidPhoneNumber(phoneNumber)) {
    alert('Invalid phone number. It should contain only digits and be between 10 to 12 digits long.');
    return false;
  }

  return true;
}

function isValidEmail(email) {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailPattern.test(email);
}

function isValidPhoneNumber(phoneNumber) {
  const phonePattern = /^\d{10,12}$/;
  return phonePattern.test(phoneNumber);
}

