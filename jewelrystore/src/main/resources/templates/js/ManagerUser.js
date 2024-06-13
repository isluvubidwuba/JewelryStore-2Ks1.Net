$(document).ready(function () {
  fetchRoles();
  fetchUniqueRankData()
  initializeUnique()
  setupModalToggle();
  setupInsertModalToggle();
  setupInsertRoleModalToggle();
});
const token = localStorage.getItem("token");



function fetchRoles() {
  $.ajax({
    url: 'http://localhost:8080/role/list',
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const roles = response.data.filter(role => !['STAFF', 'ADMIN', 'MANAGER'].includes(role.name));
        initTabs(roles);
        roles.forEach(role => setupSearch(role.name));
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
  $('#role-tabs').append('<li class="ml-2"><button id="insert-role-button" class="tab-button bg-black text-white font-semibold py-2 px-4 rounded hover:bg-gray-700 transition duration-300">+</button></li>');
  // Append the insert user button
  $('#role-tabs').append('<li class="ml-auto"><button id="insert-button" class="tab-button bg-black text-white font-semibold py-2 px-4 rounded hover:bg-gray-700 transition duration-300">Insert User</button></li>');

  bindTabClickEvents();
  const firstRole = roles[0].name;
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
      headers: {
        Authorization: `Bearer ${token}`,
      },
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
  const rankHeader = role.name === 'CUSTOMER' ? '<th class="py-2 px-4 border-b">Rank</th>' : '';
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
  $('.tab-button').on('click', function (event) {
    event.preventDefault();
    const role = $(this).data('role');
    switchTabByRole(role);
  });
}

function switchTab(role) {

  if (!role) {
    console.error("Role is undefined"); // Log an error if role is undefined
    return;
  }

  $('.tab-content').hide();
  $('#' + role.replace(/\s+/g, '')).show();
  setActiveTab(role);
  setupSearch(role); // Ensure search setup is called every time a tab is switched
}

function setActiveTab(role) {
  $('.tab-button').removeClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
  $(`.tab-button[data-role=${role}]`).addClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
}


function fetchImage(elementId, imageUrl) {
  console.log(`Fetching image for elementId: ${elementId}, imageUrl: ${imageUrl}`);
  $(`#${elementId}`).html(`<img src="${imageUrl}" alt="Image" class="h-10 w-10">`);
}

function fetchCustomers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listcustomer?page=${page}`,
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const { customers, totalPages, currentPage } = response.data;
        fetchCustomerRanks(function (ranks) {
          populateTable(customers, ranks, currentPage, 'Customer');
          updatePagination(currentPage, totalPages, 'customer');
        });
      }
    },
    error: function (error) {
      console.error('Error fetching customers:', error);
    }
  });
}

function fetchCustomerRanks(callback) {
  $.ajax({
    url: 'http://localhost:8080/earnpoints/rank',
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const ranks = response.data.map(rank => ({
          customerId: rank.userInfoDTO.id,
          rank: rank.customerTypeDTO.type,
          points: rank.point
        }));
        callback(ranks);
      }
    },
    error: function (error) {
      console.error('Error fetching customer ranks:', error);
      callback([]);
    }
  });
}

function fetchSuppliers(page) {
  $.ajax({
    url: `http://localhost:8080/userinfo/listsupplier?page=${page}`,
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const { suppliers, totalPages, currentPage } = response.data;
        populateTable(suppliers, [], currentPage, 'Supplier');
        updatePagination(currentPage, totalPages, 'supplier');
      }
    },
    error: function (error) {
      console.error('Error fetching suppliers:', error);
    }
  });
}

function populateTable(data, ranks, currentPage, role) {
  if (!data || !Array.isArray(data)) {
    console.error('Data is not an array or is undefined.');
    return;
  }

  const tableBody = $(`#${role.toLowerCase()}-table tbody`);
  tableBody.empty();
  let count = currentPage * 5 + 1;

  data.forEach(item => {
    let rankInfo = '';
    if (role === 'Customer') {
      const rankDetail = ranks.find(r => r.customerId === item.id);
      const rank = rankDetail ? rankDetail.rank : 'N/A';
      const points = rankDetail ? rankDetail.points : 'N/A';
      rankInfo = `<td class="py-2 px-4 border-b">${rank} (${points} points)</td>`;
    }
    const row = `<tr class="text-center">
                  <td class="py-2 px-4 border-b">${count++}</td>
                  <td class="py-2 px-4 border-b" id="${role.toLowerCase()}-image-${item.id}">
                    Loading...
                  </td>
                  <td class="py-2 px-4 border-b">${item.fullName}</td>
                  <td class="py-2 px-4 border-b">${item.phoneNumber}</td>
                  <td class="py-2 px-4 border-b">${item.email}</td>
                  <td class="py-2 px-4 border-b">${item.address}</td>
                  ${rankInfo}
                  <td class="py-2 px-4 border-b"><button class="edit-btn" data-id="${item.id}"><i class="fas fa-edit"></i></button></td>
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
    clearImagePreview(); // Xóa hình ảnh khi đóng modal
    clearForm(); // Xóa dữ liệu trong form khi đóng modal
  });

  $('#updateEmployeeImageFile').on('change', function () {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        $('#updateEmployeeImagePreview').attr('src', e.target.result);
      }
      reader.readAsDataURL(file);
    }
  });

  $('#update-user-form').off('submit').on('submit', function (e) {
    e.preventDefault();
    updateUser();
  });
}

function fetchUserInfo(id) {
  $.ajax({
    url: `http://localhost:8080/userinfo/findcustomer/${id}`,
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        const user = response.data;
        $('#update-id').val(user.id);
        $('#update-fullname').val(user.fullName);
        $('#update-phone').val(user.phoneNumber);
        $('#update-email').val(user.email);
        $('#update-address').val(user.address);
        $('#update-role').val(user.role.id);
        $('#updateEmployeeImagePreview').attr('src', user.image);
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
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        alert('User updated successfully!');
        $('#updateUserModal').addClass('hidden');
        clearImagePreview(); // Xóa hình ảnh sau khi cập nhật thành công
        clearForm(); // Xóa dữ liệu trong form sau khi cập nhật thành công
        const activeTab = $('.tab-button.border-l').data('role'); // Đảm bảo lấy đúng tab hiện tại

        if (activeTab === 'CUSTOMER') {
          fetchCustomers(0);
        } else if (activeTab === 'SUPPLIER') {
          fetchSuppliers(0);
        }
      } else {
        alert('Error updating user: ' + response.desc);
      }
    },
    error: function (error) {
      if (error.responseJSON) {
        alert('Error updating user: ' + error.responseJSON.desc);
      } else {
        console.error('Error updating user:', error);
        alert('Error updating user!');
      }
    }
  });
}

function clearImagePreview() {
  $('#updateEmployeeImagePreview').attr('src', ''); // Xóa hình ảnh
}

function clearForm() {
  $('#update-user-form')[0].reset(); // Đặt lại form
  $('#updateEmployeeImageFile').val(''); // Xóa giá trị của input file
}


function setupSearch(role) {
  // Thiết lập tìm kiếm cho một vai trò cụ thể
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
    searchByRole(role, criteria, query, 0);
  });
}

function searchByRole(role, criteria, query, page) {
  const searchUrl = role === 'CUSTOMER' ? 'http://localhost:8080/userinfo/searchcustomer' : 'http://localhost:8080/userinfo/searchsupplier';

  $.ajax({
    url: searchUrl,
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    data: {
      criteria: criteria,
      query: query,
      page: page
    },
    success: function (response) {
      if (response.status === "OK") {
        const { customers, suppliers, totalPages, currentPage } = response.data;
        const data = role === 'CUSTOMER' ? customers : suppliers;
        if (role === 'CUSTOMER') {
          fetchCustomerRanks(function (ranks) {
            populateTable(data, ranks, currentPage, 'Customer');
            updatePagination(currentPage, totalPages, 'customer');
          });
        } else {
          populateTable(data, [], currentPage, 'Supplier');
          updatePagination(currentPage, totalPages, 'supplier');
        }
      }
    },
    error: function (error) {
      console.error(`Error searching ${role.toLowerCase()}s:`, error);
    }
  });
}

function setupInsertModalToggle() {
  $(document).on('click', '#insert-button', function () {
    $('#insertUserModal').removeClass('hidden');
  });

  $('#close-insert-modal').on('click', function () {
    $('#insertUserModal').addClass('hidden');
    clearInsertForm(); // Xóa dữ liệu trong form khi đóng modal
  });

  // Lưu lại vai trò đã chọn khi thay đổi
  let selectedRole = '';

  $('#insert-role').on('change', function () {
    selectedRole = $(this).find('option:selected').text().toUpperCase();
  });

  // Cập nhật hình ảnh xem trước khi chọn hình ảnh
  $('#insert-file').on('change', function (e) {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (event) {
        $('#insertEmployeeImagePreview').attr('src', event.target.result).show();
      };
      reader.readAsDataURL(file);
    } else {
      $('#insertEmployeeImagePreview').attr('src', '#').hide();
    }
  });

  $('#insert-user-form').off('submit').on('submit', function (e) {
    e.preventDefault();

    const form = $(this);

    if (!validateForm(form)) return;

    var formData = new FormData($("#insert-user-form")[0]);

    // Thêm input file vào formData
    var fileInput = $('#insert-file')[0].files[0];
    if (fileInput) {
      formData.append('file', fileInput);
    }
    $.ajax({
      url: 'http://localhost:8080/userinfo/insert',
      method: 'POST',
      processData: false,
      contentType: false,
      data: formData,
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK") {
          alert('Insert Successful !');
          $('#insertUserModal').addClass('hidden');
          clearInsertForm(); // Xóa các trường trong form
          switchTabByRole(selectedRole);
        } else {
          alert('Error while insert user ' + response.desc);
        }
      },
      error: function (error) {
        console.error('Error while insert user:', error);
        alert('Error while insert user: ' + (error.responseJSON ? error.responseJSON.desc : 'System Error'));
      }
    });
  });
}

function clearInsertForm() {
  $('#insert-user-form')[0].reset(); // Đặt lại form
  $('#insertEmployeeImagePreview').attr('src', '#').hide(); // Xóa hình ảnh xem trước
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
      headers: {
        Authorization: `Bearer ${token}`,
      },
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



// Fetch rank data and populate the table
function fetchUniqueRankData() {
  $.ajax({
    url: 'http://localhost:8080/customertype/findall',
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        var tableBody = $('#rankTableBody');
        tableBody.empty(); // Xóa dữ liệu cũ

        var displayId = 1; // Bắt đầu từ 1

        response.data.forEach(function (rank) {
          var row = '<tr>' +
            '<td class="px-4 py-2 border">' + displayId + '</td>' + // Hiển thị ID tăng dần
            '<td class="px-4 py-2 border">' + rank.type + '</td>' +
            '<td class="px-4 py-2 border">' + rank.pointCondition + '</td>' +
            '<td class="px-4 py-2 border text-center">' +
            '<button class="edit-unique-btn text-blue-500 hover:text-blue-700" data-id="' + rank.id + '" data-type="' + rank.type + '" data-pointcondition="' + rank.pointCondition + '">' +
            '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">' +
            '<path d="M17.414 2.586a2 2 0 010 2.828l-10 10a2 2 0 01-.878.515l-4 1a1 1 0 01-1.263-1.263l1-4a2 2 0 01.515-.878l10-10a2 2 0 012.828 0zM5 13l-1 4 4-1 10-10-3-3L5 13zM3 17h2v2H3v-2z" />' +
            '</svg>' +
            '</button>' +
            '</td>' +
            '</tr>';
          tableBody.append(row);
          displayId++; // Tăng ID hiển thị
        });

        // Gắn sự kiện click cho các nút chỉnh sửa
        attachUniqueEditButtonEvents();
      } else {
        console.error("Error loading customer types:", response.desc);
      }
    },
    error: function (error) {
      console.error("There was an error fetching the rank data: ", error);
    }
  });
}


// Gắn sự kiện click vào các nút chỉnh sửa
function attachUniqueEditButtonEvents() {
  $('.edit-unique-btn').click(function () {
    var id = $(this).data('id');
    var type = $(this).data('type');
    var pointCondition = $(this).data('pointcondition');

    // Điền dữ liệu hiện có vào form
    $('#updateUniqueId').val(id);
    $('#updateUniqueType').val(type);
    $('#updateUniquePointCondition').val(pointCondition);

    // Hiển thị modal cập nhật
    $('#updateCustomerTypeModal').removeClass('hidden');
  });
}

// Update customer type
function updateUniqueCustomerType() {
  $('#updateCustomerTypeForm').submit(function (e) {
    e.preventDefault();

    var id = $('#updateUniqueId').val();
    var type = $('#updateUniqueType').val();
    var pointCondition = $('#updateUniquePointCondition').val();

    $.ajax({
      url: 'http://localhost:8080/customertype/updatepointcondition',
      method: 'POST',
      data: {
        id: id,
        type: type,
        pointCondition: pointCondition
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        alert('Update successful!');
        $('#updateCustomerTypeModal').addClass('hidden');
        fetchUniqueRankData(); // Refresh the data in the main modal
        location.reload();
      },
      error: function (error) {
        console.error("There was an error updating the rank data: ", error);
      }
    });
  });
}

// Open customer type modal
function openUniqueCustomerTypeModal() {
  fetchUniqueRankData();
  $('#customerTypeModal').removeClass('hidden');
}

// Close customer type modal
function closeUniqueCustomerTypeModal() {
  $('#customerTypeModal').addClass('hidden');
}

// Close update customer type modal
function closeUpdateUniqueCustomerTypeModal() {
  $('#updateCustomerTypeModal').addClass('hidden');
}

// Initialize event listeners
function initializeUniqueEventListeners() {
  $('#managerRank').click(openUniqueCustomerTypeModal);
  $('#closeCustomerTypeModal, #closeCustomerTypeModalFooter').click(closeUniqueCustomerTypeModal);
  $('#closeUpdateCustomerTypeModal').click(closeUpdateUniqueCustomerTypeModal);
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
  $('#addCustomerType').click(function () {
    $('#addCustomerTypeModal').removeClass('hidden');
  });
}

// Add new customer type
function addUniqueCustomerType() {
  $('#addCustomerTypeForm').submit(function (e) {
    e.preventDefault();

    var type = $('#addType').val();
    var pointCondition = $('#addPointCondition').val();

    $.ajax({
      url: 'http://localhost:8080/customertype/add',
      method: 'POST',
      data: {
        type: type,
        pointCondition: pointCondition
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        alert('Customer Type added successfully!');
        $('#addCustomerTypeModal').addClass('hidden');
        fetchUniqueRankData(); // Refresh the data in the main modal
        location.reload();
      },
      error: function (error) {
        console.error("There was an error adding the customer type: ", error);
      }
    });
  });
}

// Close add customer type modal
function closeAddCustomerTypeModal() {
  $('#closeAddCustomerTypeModal').click(function () {
    $('#addCustomerTypeModal').addClass('hidden');
  });
}

// Delete customer type
function deleteUniqueCustomerType() {
  $('#deleteCustomerType').click(function () {
    var id = $('#updateUniqueId').val();

    // Show confirmation dialog
    var isConfirmed = confirm("Are you sure you want to delete this customer type?");

    if (isConfirmed) {
      $.ajax({
        url: 'http://localhost:8080/customertype/delete',
        method: 'POST',
        data: {
          customerTypeId: id
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
        success: function (response) {
          alert('Delete successful!');
          $('#updateCustomerTypeModal').addClass('hidden');
          fetchUniqueRankData(); // Refresh the data in the main modal
          location.reload();
        },
        error: function (error) {
          console.error("There was an error deleting the customer type: ", error);
        }
      });
    }
  });
}
