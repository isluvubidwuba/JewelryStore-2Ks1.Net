$(document).ready(function () {
    // Fetch roles and populate the dropdown
    function fetchRoles(selectElementId) {
        return $.ajax({
            url: 'http://localhost:8080/role/list',
            method: 'GET',
            success: function (response) {
                var roles = response.data;
                var roleSelect = $('#' + selectElementId);
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
                data.filter(function (employee) { return employee.status; }).forEach(function (employee) {
                    var row = '<tr class="odd:bg-white odd:dark:bg-gray-900 even:bg-gray-50 even:dark:bg-gray-800 border-b dark:border-gray-700">' +
                        '<th scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">' + employee.id + '</th>' +
                        '<td class="px-6 py-4">' + (employee.firstName + " " + employee.lastName)+ '</td>' +
                        '<td class="px-6 py-4">' + (employee.role ? employee.role.name : '') + '</td>' +
                        '<td class="px-6 py-4">Active</td>' +
                        '<td class="px-6 py-4">' +
                        '<button type="button" class="editEmployeeBtn text-white bg-gradient-to-r from-green-400 via-green-500 to-green-600 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-green-300 dark:focus:ring-green-800 shadow-lg shadow-green-500/50 dark:shadow-lg dark:shadow-green-800/80 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2" data-employee=\'' + JSON.stringify(employee) + '\'>Edit</button> ' +
                        '<button type="button" class="deleteEmployeeBtn text-white bg-gradient-to-r from-red-400 via-red-500 to-red-600 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-red-300 dark:focus:ring-red-800 shadow-lg shadow-red-500/50 dark:shadow-lg dark:shadow-red-800/80 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2" data-id="' + employee.id + '">Delete</button>' +
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
                    $('#status').val('Active'); // Display 'Active'
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
                        status: true // Always set status to 'Active'
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

    // Function to delete an employee
    function deleteEmployee(id) {
        if (confirm('Are you sure you want to delete this employee?')) {
            $.ajax({
                url: 'http://localhost:8080/employee/delete/' + id,
                method: 'DELETE',
                success: function (response) {
                    alert('Employee deleted successfully!');
                    fetchEmployees();
                },
                error: function (error) {
                    console.log('Error deleting employee:', error);
                }
            });
        }
    }

    // Show the insert employee modal
    $('#insertEmployeeBtn').click(function () {
        $('#insertEmployeeModal').removeClass('hidden');
        fetchRoles('insertRole'); // Fetch roles for insert modal
    });

    // Close the insert employee modal
    $('#closeInsertModalBtn').click(function () {
        $('#insertEmployeeModal').addClass('hidden');
    });

    // Handle the insert employee form submission
    $('#insertEmployeeForm').submit(function (event) {
        event.preventDefault();
        var formData = {
            firstName: $('#firstName1').val(),
            lastName: $('#lastName1').val(),
            pinCode: $('#pinCode').val(),
            phoneNumber: $('#phoneNumber').val(),
            email: $('#email').val(),
            address: $('#address').val(),
            role: {
                id: $('#insertRole').val()
            },
            status: true // Always set status to 'Active'

        };
        console.log(formData);
        $.ajax({
            url: 'http://localhost:8080/employee/insert',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                alert('Employee inserted successfully!');
                $('#insertEmployeeModal').addClass('hidden');
                fetchEmployees();
            },
            error: function (error) {
                console.log('Error inserting employee:', error);
            }
        });
    });

    // Fetch roles first, then fetch employees
    fetchRoles('role').then(fetchEmployees); // Fetch roles for update modal as well
});
