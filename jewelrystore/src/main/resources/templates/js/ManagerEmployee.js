// $(document).ready(function () {
//     var currentPage = 0; // Khởi tạo trang hiện tại

//     // Fetch roles and populate the dropdown
//     function fetchRoles(selectElementId) {
//         return $.ajax({
//             url: 'http://localhost:8080/role/list',
//             method: 'GET',
//             success: function (response) {
//                 var roles = response.data;
//                 var roleSelect = $('#' + selectElementId);
//                 roleSelect.empty();
//                 roles.forEach(function (role) {
//                     roleSelect.append('<option value="' + role.id + '">' + role.name + '</option>');
//                 });
//             },
//             error: function (error) {
//                 console.log('Error fetching roles:', error);
//             }
//         });
//     }

//     // Fetch employees and populate the table
//     function fetchEmployees(page = currentPage) {
//         console.log("Fetching employees for page: ", data.totalPages);
//         $.ajax({
//             url: `http://localhost:8080/employee/list?page=${page}`,
//             method: 'GET',
//             success: function (response) {
//                 var data = response.data.employees;
//                 var tableBody = $('#employeeTableBody');
//                 tableBody.empty();
//                 data.forEach(function (employee) {
//                     var row = '<tr class="odd:bg-white odd:dark:bg-gray-900 even:bg-gray-50 even:dark:bg-gray-800 border-b dark:border-gray-700">' +
//                         '<th scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">' + employee.id + '</th>' +
//                         '<td class="px-6 py-4">' + (employee.firstName + " " + employee.lastName) + '</td>' +
//                         '<td class="px-6 py-4">' + (employee.role ? employee.role.name : '') + '</td>' +
//                         '<td class="px-6 py-4">' + (employee.status ? 'Active' : 'Inactive') + '</td>' +
//                         '<td class="px-6 py-4">' +
//                         '<button type="button" class="editEmployeeBtn text-white bg-gradient-to-r from-green-400 via-green-500 to-green-600 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-green-300 dark:focus:ring-green-800 shadow-lg shadow-green-500/50 dark:shadow-lg dark:shadow-green-800/80 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2" data-employee=\'' + JSON.stringify(employee) + '\'>Edit</button> ' +
//                         '<button type="button" class="deleteEmployeeBtn text-white bg-gradient-to-r from-red-400 via-red-500 to-red-600 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-red-300 dark:focus:ring-red-800 shadow-lg shadow-red-500/50 dark:shadow-lg dark:shadow-red-800/80 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2" data-id="' + employee.id + '">Delete</button>' +
//                         '</td>' +
//                         '</tr>';
//                     tableBody.append(row);
//                 });

//                 // Add event handlers for edit and delete actions
//                 $('.editEmployeeBtn').click(function () {
//                     var employee = $(this).data('employee');
//                     $('#employeeId').val(employee.id);
//                     $('#firstName').val(employee.firstName);
//                     $('#lastName').val(employee.lastName);
//                     $('#role').val(employee.role ? employee.role.id : '');
//                     $('#phoneNumber').val(employee.phoneNumber);
//                     $('#email').val(employee.email);
//                     $('#address').val(employee.address);
//                     $('#status').val(employee.status ? 'Active' : 'Inactive');
//                     $('#updateEmployeeModal').removeClass('hidden');
//                 });

//                 $('.deleteEmployeeBtn').click(function () {
//                     var id = $(this).data('id');
//                     deleteEmployee(id);
//                 });

//                 updatePaginationControls(response.data.currentPage, response.data.totalPages);

//                 $('#updateEmployeeForm').submit(function (event) {
//                     event.preventDefault();
//                     var formData = {
//                         id: $('#employeeId').val(),
//                         firstName: $('#firstName').val(),
//                         lastName: $('#lastName').val(),
//                         roleId: $('#role').val(),
//                         status: $('#status').val() === 'Active',
//                         phoneNumber: $('#phoneNumber').val(),
//                         email: $('#email').val(),
//                         address: $('#address').val()
//                     };
//                     $.ajax({
//                         url: 'http://localhost:8080/employee/update',
//                         method: 'POST',
//                         data: formData,
//                         success: function (response) {
//                             alert('Employee updated successfully!');
//                             location.reload();
//                         },
//                         error: function (error) {
//                             console.log('Error:', error);
//                         }
//                     });
//                 });

//                 $('#closeModalBtn').click(function () {
//                     $('#updateEmployeeModal').addClass('hidden');
//                 });
//             },
//             error: function (error) {
//                 console.log('Error fetching employees:', error);
//             }
//         });
//     }

//     // Function to delete an employee
//     function deleteEmployee(id) {
//         if (confirm('Are you sure you want to delete this employee?')) {
//             $.ajax({
//                 url: 'http://localhost:8080/employee/delete/' + id,
//                 method: 'DELETE',
//                 success: function (response) {
//                     alert('Employee deleted successfully!');
//                     fetchEmployees(currentPage); // Fetch employees on the current page
//                 },
//                 error: function (error) {
//                     console.log('Error deleting employee:', error);
//                 }
//             });
//         }
//     }

//     // Update pagination controls
//     function updatePaginationControls(currentPage, totalPages) {
//         var pagination = $('#pagination');
//         pagination.empty();
//         if (currentPage > 0) {
//             pagination.append('<button id="prevPageBtn" class="page-btn">Previous</button>');
//         }
//         for (var i = 0; i < totalPages; i++) {
//             pagination.append('<button class="page-btn" data-page="' + i + '">' + (i + 1) + '</button>');
//         }
//         if (currentPage < totalPages - 1) {
//             pagination.append('<button id="nextPageBtn" class="page-btn">Next</button>');
//         }

//         $('.page-btn').click(function () {
//             var page = $(this).data('page');
//             if (page !== undefined) {
//                 currentPage = page;
//                 fetchEmployees(page);
//             }
//         });

//         $('#prevPageBtn').click(function () {
//             if (currentPage > 0) {
//                 currentPage--;
//                 fetchEmployees(currentPage);
//             }
//         });

//         $('#nextPageBtn').click(function () {
//             if (currentPage < totalPages - 1) {
//                 currentPage++;
//                 fetchEmployees(currentPage);
//             }
//         });
//     }

//     // Show the insert employee modal
//     $('#insertEmployeeBtn').click(function () {
//         $('#insertEmployeeModal').removeClass('hidden');
//         fetchRoles('insertRole'); // Fetch roles for insert modal
//     });

//     // Close the insert employee modal
//     $('#closeInsertModalBtn').click(function () {
//         $('#insertEmployeeModal').addClass('hidden');
//     });

//     // Handle the insert employee form submission
//     $('#insertEmployeeForm').submit(function (event) {
//         event.preventDefault();
//         var formData = {
//             firstName: $('#firstName1').val(),
//             lastName: $('#lastName1').val(),
//             pinCode: $('#pinCode').val(),
//             phoneNumber: $('#phoneNumber1').val(),
//             email: $('#email1').val(),
//             address: $('#address1').val(),
//             roleId: $('#insertRole').val(),
//             status: true // Always set status to 'Active'
//         };
//         $.ajax({
//             url: 'http://localhost:8080/employee/insert',
//             method: 'PUT',
//             data: formData,
//             success: function (response) {
//                 alert('Employee inserted successfully!');
//                 $('#insertEmployeeModal').addClass('hidden');
//                 fetchEmployees(currentPage); // Fetch employees on the current page
//             },
//             error: function (error) {
//                 console.log('Error inserting employee:', error);
//             }
//         });
//     });

//     // Fetch roles first, then fetch employees
//     fetchRoles('role').then(fetchEmployees); // Fetch roles for update modal as well
// });
