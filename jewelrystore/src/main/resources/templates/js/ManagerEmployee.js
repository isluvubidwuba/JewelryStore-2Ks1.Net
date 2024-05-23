$(document).ready(function () {
    var currentPage = 0;
    var totalPages = 0;

    loadRoles();
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
                    <button class="deleteBtn bg-red-500 text-white px-4 py-2 rounded">Delete</button>
                </td>
            </tr>
        `;
    }

    // Update pagination controls (Previous and Next buttons)
    function updatePaginationControls(totalPages, currentPage) {
        $('#prevPageBtn').prop('disabled', currentPage <= 0);
        $('#nextPageBtn').prop('disabled', currentPage >= totalPages - 1);
    }

    // Fetch roles and populate the role dropdown
    function loadRoles() {
        $.ajax({
            url: `http://localhost:8080/role/list`,
            method: "GET",
            success: function (response) {
                if (response && response.data) {
                    response.data.forEach(role => {
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
                    // Display the current image
                    if (employee.image) {
                        $("#currentImage").attr("src", `http://localhost:8080/employee/file/${employee.image}`).show();
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

        var formData = new FormData();
        formData.append("id", $("#employeeId").val());
        formData.append("firstName", $("#firstName").val());
        formData.append("lastName", $("#lastName").val());
        formData.append("phoneNumber", $("#phoneNumber").val());
        formData.append("email", $("#email").val());
        formData.append("address", $("#address").val());
        formData.append("roleId", $("#role").val());
        formData.append("status", $("#status").val() === "true");
        var fileInput = $("#file")[0];
        if (fileInput.files.length > 0) {
            formData.append("file", fileInput.files[0]);
        }

        // Log the formData values
        for (var pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }

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
});
