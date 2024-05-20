$(document).ready(function () {
    // Fetch roles and populate the dropdown
    function fetchRoles() {
        return $.ajax({
            url: 'http://localhost:8080/role/list',
            method: 'GET',
            success: function (response) {
                var roles = response.data;
                var roleSelect = $('#role');
                roleSelect.empty();
                roles.forEach(function (role) {
                    roleSelect.append('<option value="' + role.id + '">' + role.name + '</option>');
                });
            },
            error: function (error) {
                console.log('Error fetching roles:', error);
            }
        });
    }

    // Fetch employees and populate the table
    function fetchEmployees() {
        $.ajax({
            url: 'http://localhost:8080/employee/list',
            method: 'GET',
            success: function (response) {
                var data = response.data;
                var tableBody = $('#employeeTableBody');
                tableBody.empty();
                data.forEach(function (employee) {
                    var row = '<tr class="odd:bg-white odd:dark:bg-gray-900 even:bg-gray-50 even:dark:bg-gray-800 border-b dark:border-gray-700">' +
                        '<th scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">' + employee.id + '</th>' +
                        '<td class="px-6 py-4">' + employee.firstName + '</td>' +
                        '<td class="px-6 py-4">' + employee.lastName + '</td>' +
                        '<td class="px-6 py-4">' + (employee.role ? employee.role.name : '') + '</td>' +
                        '<td class="px-6 py-4">' + (employee.status ? 'Active' : 'Inactive') + '</td>' +
                        '<td class="px-6 py-4">' +
                        '<button class="editEmployeeBtn px-2 py-1 bg-yellow-500 text-white rounded" data-employee=\'' + JSON.stringify(employee) + '\'>Edit</button> ' +
                        '<button class="deleteEmployeeBtn px-2 py-1 bg-red-500 text-white rounded" data-id="' + employee.id + '">Delete</button>' +
                        '</td>' +
                        '</tr>';
                    tableBody.append(row);
                });

                // Add event handlers for edit and delete actions
                $('.editEmployeeBtn').click(function () {
                    var employee = $(this).data('employee');
                    $('#employeeId').val(employee.id);
                    $('#firstName').val(employee.firstName);
                    $('#lastName').val(employee.lastName);
                    $('#role').val(employee.role ? employee.role.id : '');
                    $('#status').val(employee.status ? 'true' : 'false');
                    $('#updateEmployeeModal').removeClass('hidden');
                });

                $('.deleteEmployeeBtn').click(function () {
                    var id = $(this).data('id');
                    deleteEmployee(id);
                });

                $('#updateEmployeeForm').submit(function (event) {
                    event.preventDefault();
                    var formData = {
                        id: $('#employeeId').val(),
                        firstName: $('#firstName').val(),
                        lastName: $('#lastName').val(),
                        role: {
                            id: $('#role').val()
                        },
                        status: $('#status').val() === "true"
                    };
                    $.ajax({
                        url: 'http://localhost:8080/employee/update',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(formData),
                        success: function (response) {
                            alert('Employee updated successfully!');
                            location.reload();
                        },
                        error: function (error) {
                            console.log('Error:', error);
                        }
                    });
                });

                $('#closeModalBtn').click(function () {
                    $('#updateEmployeeModal').addClass('hidden');
                });
            },
            error: function (error) {
                console.log('Error fetching employees:', error);
            }
        });
    }

    // Fetch roles first, then fetch employees
    fetchRoles().then(fetchEmployees);
});
