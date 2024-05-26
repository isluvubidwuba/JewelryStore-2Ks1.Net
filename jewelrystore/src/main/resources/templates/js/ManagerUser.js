$(document).ready(function () {
    fetchRoles();
    setupModalToggle();
});

function fetchRoles() {
    $.ajax({
        url: 'http://localhost:8080/role/list', // Thay thế bằng endpoint API của bạn
        method: 'GET',
        success: function (response) {
            if (response.status === 200) {
                const roles = response.data.filter(role => !['STAFF', 'ADMIN', 'MANAGER'].includes(role.name));
                initTabs(roles);
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

    bindTabClickEvents();
    const firstRole = roles[0].name;
    switchTab(firstRole); // Đặt tab mặc định là role đầu tiên có sẵn
    if (firstRole === 'CUSTOMER') {
        fetchCustomers(0); // Fetch customers for the first page if the default tab is CUSTOMER
    } else if (firstRole === 'SUPPLIER') {
        fetchSuppliers(0); // Fetch suppliers for the first page if the default tab is SUPPLIER
    }

    // Append the Add Role button after the tabs have been created
    $('#role-tabs').append('<li><button id="add-role-button" class="tab-button bg-white inline-block py-2 px-4 font-semibold hover:bg-blue-500 hover:text-white transition duration-300">+</button></li>');
    $('#add-role-button').on('click', function () {
        $('#addRoleModal').removeClass('hidden');
    });
}


function setupModalToggle() {
    $('#close-modal').on('click', function () {
        $('#addRoleModal').addClass('hidden');
    });

    $('#add-role-form').on('submit', function (e) {
        e.preventDefault();
        const roleName = $('#role-name').val();
        $.ajax({
            url: 'http://localhost:8080/role/insert',
            method: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            data: { roleName: roleName },
            success: function (response) {
                alert(response); // Hiển thị thông báo từ server
                $('#addRoleModal').addClass('hidden');
                fetchRoles(); // Cập nhật danh sách roles sau khi thêm mới
            },
            error: function (error) {
                console.error('Error adding role:', error);
                alert('Error adding role!');
            }
        });
    });
}

function createTab(role) {
    return `<li class="mr-1">
              <a class="tab-button bg-white inline-block py-2 px-4 font-semibold" data-role="${role.name}" href="#">${role.name}</a>
            </li>`;
}

function createTabContent(role) {
    if (role.name === 'CUSTOMER') {
        return `<div id="${role.name}" class="tab-content" style="display: none;">
                <h2 class="text-2xl mb-4">Customer List</h2>
                <table id="customer-table" class="min-w-full bg-white">
                  <thead>
                    <tr>
                      <th class="py-2 px-4 border-b">ID</th>
                      <th class="py-2 px-4 border-b">Full Name</th>
                      <th class="py-2 px-4 border-b">Phone Number</th>
                      <th class="py-2 px-4 border-b">Email</th>
                      <th class="py-2 px-4 border-b">Address</th>
                      <th class="py-2 px-4 border-b">Role</th>
                      <th class="py-2 px-4 border-b">Image</th>
                    </tr>
                  </thead>
                  <tbody>
                    <!-- Customer rows will be dynamically added here -->
                  </tbody>
                </table>
                <div id="pagination" class="mt-4">
                  <button id="prev-page" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Previous</button>
                  <span id="page-info" class="mx-2"></span>
                  <button id="next-page" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Next</button>
                </div>
              </div>`;
    } else if (role.name === 'SUPPLIER') {
        return `<div id="${role.name}" class="tab-content" style="display: none;">
                <h2 class="text-2xl mb-4">Supplier List</h2>
                <table id="supplier-table" class="min-w-full bg-white">
                  <thead>
                    <tr>
                      <th class="py-2 px-4 border-b">ID</th>
                      <th class="py-2 px-4 border-b">Full Name</th>
                      <th class="py-2 px-4 border-b">Phone Number</th>
                      <th class="py-2 px-4 border-b">Email</th>
                      <th class="py-2 px-4 border-b">Address</th>
                      <th class="py-2 px-4 border-b">Role</th>
                      <th class="py-2 px-4 border-b">Image</th>
                    </tr>
                  </thead>
                  <tbody>
                    <!-- Supplier rows will be dynamically added here -->
                  </tbody>
                </table>
                <div id="pagination-supplier" class="mt-4">
                  <button id="prev-page-supplier" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Previous</button>
                  <span id="page-info-supplier" class="mx-2"></span>
                  <button id="next-page-supplier" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Next</button>
                </div>
              </div>`;
    }
    return `<div id="${role.name}" class="tab-content" style="display: none;">
              <p>${role.name} Content</p>
            </div>`;
}

function bindTabClickEvents() {
    $('.tab-button').on('click', function (event) {
        event.preventDefault();
        const role = $(this).data('role');
        switchTab(role);
        if (role === 'CUSTOMER') {
            fetchCustomers(0); // Fetch customers for the first page
        } else if (role === 'SUPPLIER') {
            fetchSuppliers(0); // Fetch suppliers for the first page
        }
    });
}

function switchTab(role) {
    $('.tab-content').hide();
    $('#' + role).show();
    setActiveTab(role);
}

function setActiveTab(role) {
    $('.tab-button').removeClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
    $(`.tab-button[data-role=${role}]`).addClass('border-l border-t border-r rounded-t text-blue-700 font-semibold');
}

function fetchCustomers(page) {
    $.ajax({
        url: `http://localhost:8080/userinfo/listcustomer?page=${page}`, // Thay thế bằng endpoint API của bạn
        method: 'GET',
        success: function (response) {
            if (response.status === 200) {
                const { customers, totalPages, currentPage } = response.data;
                populateCustomerTable(customers);
                updatePagination(currentPage, totalPages);
            }
        },
        error: function (error) {
            console.error('Error fetching customers:', error);
        }
    });
}

function fetchSuppliers(page) {
    $.ajax({
        url: `http://localhost:8080/userinfo/listsupplier?page=${page}`, // Thay thế bằng endpoint API của bạn
        method: 'GET',
        success: function (response) {
            if (response.status === 200) {
                const { customers: suppliers, totalPages, currentPage } = response.data;
                populateSupplierTable(suppliers);
                updatePaginationSupplier(currentPage, totalPages);
            }
        },
        error: function (error) {
            console.error('Error fetching suppliers:', error);
        }
    });
}

function populateCustomerTable(customers) {
    const tableBody = $('#customer-table tbody');
    tableBody.empty();
    customers.forEach(customer => {
        const row = `<tr class = "text-center">
                    <td class="py-2 px-4 border-b">${customer.id}</td>
                    <td class="py-2 px-4 border-b">${customer.fullName}</td>
                    <td class="py-2 px-4 border-b">${customer.phoneNumber}</td>
                    <td class="py-2 px-4 border-b">${customer.email}</td>
                    <td class="py-2 px-4 border-b">${customer.address}</td>
                    <td class="py-2 px-4 border-b">${customer.role.name}</td>
                    <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${customer.image}" alt="${customer.fullName}" class="h-10 w-10"></td>
                  </tr>`;
        tableBody.append(row);
    });
}

function populateSupplierTable(suppliers) {
    const tableBody = $('#supplier-table tbody');
    tableBody.empty();
    suppliers.forEach(supplier => {
        const row = `<tr class = "text-center">
                    <td class="py-2 px-4 border-b">${supplier.id}</td>
                    <td class="py-2 px-4 border-b">${supplier.fullName}</td>
                    <td class="py-2 px-4 border-b">${supplier.phoneNumber}</td>
                    <td class="py-2 px-4 border-b">${supplier.email}</td>
                    <td class="py-2 px-4 border-b">${supplier.address}</td>
                    <td class="py-2 px-4 border-b">${supplier.role.name}</td>
                    <td class="py-2 px-4 border-b"><img src="http://localhost:8080/employee/files/${supplier.image}" alt="${supplier.fullName}" class="h-10 w-10"></td>
                  </tr>`;
        tableBody.append(row);
    });
}

function updatePagination(currentPage, totalPages) {
    $('#page-info').text(`Page ${currentPage + 1} of ${totalPages}`);
    $('#prev-page').off('click').on('click', function () {
        if (currentPage > 0) {
            fetchCustomers(currentPage - 1);
        }
    });
    $('#next-page').off('click').on('click', function () {
        if (currentPage < totalPages - 1) {
            fetchCustomers(currentPage + 1);
        }
    });
}

function updatePaginationSupplier(currentPage, totalPages) {
    $('#page-info-supplier').text(`Page ${currentPage + 1} of ${totalPages}`);
    $('#prev-page-supplier').off('click').on('click', function () {
        if (currentPage > 0) {
            fetchSuppliers(currentPage - 1);
        }
    });
    $('#next-page-supplier').off('click').on('click', function () {
        if (currentPage < totalPages - 1) {
            fetchSuppliers(currentPage + 1);
        }
    });
}
