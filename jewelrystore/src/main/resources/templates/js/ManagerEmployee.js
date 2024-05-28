$(document).ready(function () {
    var currentPage = 0;
    var totalPages = 0;

    loadRoles();
    loadRoleInsert();
    fetchEmployees(currentPage); // Fetch initial page

    // Fetch employees list and render it
    function fetchEmployees(page = 0) {
        $.ajax({
            url: `http://localhost:8080/employee/listpage?page=${page}`,
            method: "GET",
            success: function (response) {
                processEmployeeResponse(response);
            },
            error: function (error) {
                console.error('Error fetching employees:', error);
            }
        });
    }

    // Process response and render employees
    function processEmployeeResponse(response) {
        emptyTableBody();

        if (response && response.data) {
            const { employees, totalPages: tp, currentPage: cp } = response.data;
            totalPages = tp;
            currentPage = cp;

            employees.forEach(function (employee) {
                const employeeRow = createEmployeeRow(employee);
                $("#employeeTableBody").append(employeeRow);
            });
            console.log(totalPages, currentPage)
            updatePaginationControls(totalPages, currentPage);
        }
    }

    // Clear existing rows in the employee table body
    function emptyTableBody() {
        $("#employeeTableBody").empty();
    }

    // Create an employee row for the table
    function createEmployeeRow(employee) {
        const statusText = employee.status ? "Active" : "Inactive";
        const statusColor = employee.status ? "text-green-500" : "text-red-500";

        return `
            <tr>
                <td class="px-6 py-4">${employee.id}</td>
                <td class="px-6 py-4">
                <img src="http://localhost:8080/employee/files/${employee.image}" alt="Employee Image" class="w-10 h-10 rounded-full">
                </td>

                <td class="px-6 py-4">${employee.firstName} ${employee.lastName}</td>
                <td class="px-6 py-4">${employee.role.name}</td>
                <td class="px-6 py-4 ${statusColor}">${statusText}</td>
                <td class="px-6 py-4">
                    <button class="editBtn bg-blue-500 text-white px-4 py-2 rounded" data-id="${employee.id}">Edit</button>
                    <button class="deleteBtn bg-red-500 text-white px-4 py-2 rounded" data-id="${employee.id}">Delete</button>
                </td>
            </tr>
        `;
    }

    // Update pagination controls (Previous and Next buttons)
    function updatePaginationControls(totalPages, currentPage) {
        $('#paginationControls').empty();
        for (let i = 0; i < totalPages; i++) {
            const button = $('<button class="pagination-button">').text(i + 1).data('page', i);
            if (i === currentPage) {
                button.prop('disabled', true).addClass('active');
            }
            $('#paginationControls').append(button);
        }
    }

    // Handle click event for previous page button
    $('#prevPageBtn').on('click', function () {
        if (currentPage > 0) {
            fetchEmployees(--currentPage);
        }
    });

    // Handle click event for next page button
    $('#nextPageBtn').on('click', function () {
        if (currentPage < totalPages - 1) {
            fetchEmployees(++currentPage);
        }
    });

    // Fetch roles and populate the role dropdown
    function loadRoles() {
        $.ajax({
            url: `http://localhost:8080/role/list`,
            method: "GET",
            success: function (response) {
                if (response && response.data) {
                    const rolesToShow = response.data.slice(0, 3); // Get only the first 3 roles
                    rolesToShow.forEach(role => {
                        $('#role').append(new Option(role.name, role.id));
                    });
                }
            },
            error: function (error) {
                console.error('Error fetching roles:', error);
            }
        });
    }

    // Handle click event on edit button to show the modal with employee data
    $('#employeeTableBody').on('click', '.editBtn', function () {
        const id = $(this).data('id');
        showEmployeeModal(id);
    });

    // Fetch and display employee data in the modal
    function showEmployeeModal(id) {
        $.ajax({
            url: `http://localhost:8080/employee/listemployee/${id}`,
            method: "GET",
            success: function (response) {
                if (response && response.data) {
                    const employee = response.data;
                    // Populate the modal with employee data
                    $("#employeeId").val(employee.id);
                    $("#firstName").val(employee.firstName);
                    $("#lastName").val(employee.lastName);
                    $("#phoneNumber").val(employee.phoneNumber);
                    $("#email").val(employee.email);
                    $("#address").val(employee.address);
                    $("#role").val(employee.role.id);
                    $("#status").val(employee.status.toString());
                    $("#pinCode").val(employee.pinCode);
                    // Display the current image
                    if (employee.image) {
                        $("#currentImage").attr("src", `http://localhost:8080/employee/files/${employee.image}`).show();
                    } else {
                        $("#currentImage").hide();
                    }
                    $("#file").val(''); // Clear file input

                    // Show the modal
                    $("#updateEmployeeModal").removeClass("hidden");
                } else {
                    console.error('No employee data found');
                }
            },
            error: function (error) {
                console.error("Error fetching employee data:", error);
            }
        });
    }

    // Handle form submit
    $("#updateEmployeeForm").on("submit", function (event) {
        event.preventDefault();

        var formData = new FormData($("#updateEmployeeForm")[0]);

        $.ajax({
            url: "http://localhost:8080/employee/update",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (response) {
                alert(response.desc);
                if (response.status !== 500) {
                    $("#updateEmployeeModal").addClass("hidden");
                    fetchEmployees(currentPage); // Reload current page after update
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Update fail. Internal Server Error");
            }
        });
    });

    // Close modal
    $("#closeModalBtn").click(function () {
        $("#updateEmployeeModal").addClass("hidden");
    });

    // Fetch roles and populate the role dropdown for insert modal
    function loadRoleInsert() {
        $.ajax({
            url: `http://localhost:8080/role/list`,
            method: "GET",
            success: function (response) {
                if (response && response.data) {
                    const rolesToShow = response.data.slice(0, 3); // Get only the first 3 roles
                    $('#insertRole').empty(); // Clear existing options
                    rolesToShow.forEach(role => {
                        $('#insertRole').append(new Option(role.name, role.id));
                    });
                }
            },
            error: function (error) {
                console.error('Error fetching roles:', error);
            }
        });
    }

    // Handle click event on insert button to show the insert modal
    $("#insertEmployeeBtn").click(function () {
        $("#insertEmployeeModal").removeClass("hidden");
    });

    // Handle form submit for inserting new employee
    $("#insertEmployeeForm").on("submit", function (event) {
        event.preventDefault();

        var formData = new FormData($("#insertEmployeeForm")[0]);
        $.ajax({
            url: "http://localhost:8080/employee/insert",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (response) {
                alert(response.desc);
                if (response.status !== 500) {
                    $("#insertEmployeeModal").addClass("hidden");
                    resetInsertForm();
                    fetchEmployees(currentPage); // Reload current page after insert
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Insert fail. Internal Server Error");
            }
        });
    });

    // Close insert modal
    $("#closeInsertModalBtn").click(function () {
        $("#insertEmployeeModal").addClass("hidden");
    });

    // Function to clear the insert employee form
    function resetInsertForm() {
        $("#insertEmployeeForm")[0].reset(); // Clear all form fields
    }

    // Handle click event on delete button to delete an employee
    $('#employeeTableBody').on('click', '.deleteBtn', function () {
        const id = $(this).data('id');
        if (confirm('Are you sure you want to delete this employee?')) {
            deleteEmployee(id);
        }
    });

    // Delete employee function
    function deleteEmployee(id) {
        $.ajax({
            url: `http://localhost:8080/employee/delete/${id}`,
            type: "DELETE",
            success: function (response) {
                alert(response.desc);
                if (response.status !== 500) {
                    fetchEmployees(currentPage); // Reload current page after delete
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Delete fail. Internal Server Error");
            }
        });
    }

    function fetchAndProcessSearchEmployees(criteria, query, page = 0) {
        // Gửi yêu cầu tìm kiếm dựa trên tiêu chí và giá trị tìm kiếm
        $.ajax({
            url: 'http://localhost:8080/employee/search',
            method: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            data: {
                criteria: criteria,
                query: query,
                page: page
            },
            success: function (response) {
                // Xử lý phản hồi và hiển thị nhân viên
                console.log(response);
                processEmployeeResponse(response); // Gọi hàm xử lý phản hồi với dữ liệu tìm kiếm
            },
            error: function (error) {
                console.error('Error fetching search results:', error);
            }
        });
    }

    // Handle pagination click event
    $(document).on('click', '.pagination-button', function () {
        var criteria = $('#selected-criteria').text();
        var query = $('#search-input').val();
        var page = $(this).data('page');

        fetchAndProcessSearchEmployees(criteria, query, page);
    });

    // Khi người dùng chọn một tiêu chí tìm kiếm
    $('.dropdown-item').on('click', function () {
        var criteria = $(this).data('criteria');
        $('#selected-criteria').text(criteria);
        $('#dropdown').addClass('hidden');
        $('#search-input').attr('placeholder', 'Search by ' + criteria);
    });

    // Khi người dùng gửi form tìm kiếm
    $('form').on('submit', function (event) {
        event.preventDefault();

        var criteria = $('#selected-criteria').text();
        var query = $('#search-input').val();

        // Đặt lại trang hiện tại về 0 khi thực hiện tìm kiếm mới
        currentPage = 0;
        fetchAndProcessSearchEmployees(criteria, query, currentPage);
    });

    // Toggle dropdown menu
    $('#dropdown-button').on('click', function () {
        $('#dropdown').toggleClass('hidden');
    });

    // Close dropdown when clicking outside
    $(document).on('click', function (event) {
        if (!$(event.target).closest('#dropdown-button').length && !$(event.target).closest('#dropdown').length) {
            $('#dropdown').addClass('hidden');
        }
    });
});
