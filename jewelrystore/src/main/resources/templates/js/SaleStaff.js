$(document).ready(function () {
    loadProducts();

    $('#allBtn').click(function () {
        toggleButtons();
    });

    $("#saltestaff-button").on("click", function () {
        window.location.href = "SaleStaff.html";
    });
});

const token = localStorage.getItem("token");

function toggleButtons() {
    $('#newArrivalsBtn').toggleClass('hidden');
    $('#partyDressesBtn').toggleClass('hidden');
    $('#kidsClothingBtn').toggleClass('hidden');
    $('#shoesBtn').toggleClass('hidden');
}

function openUserModal() {
    $.ajax({
        url: 'http://localhost:8080/earnpoints/rank',
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        success: function (data) {
            if (data.status === 'OK') {
                const userTableBody = $('#user-table-body');
                userTableBody.empty(); // Clear existing data

                data.data.forEach(user => {
                    const row = `
                        <tr>
                            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${user.userInfoDTO.fullName}</td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${user.point}</td>
                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                <button type="button" class="text-blue-600 hover:text-blue-900" onclick="selectUser(${user.userInfoDTO.id}, '${user.userInfoDTO.fullName}')">Select</button>
                            </td>
                        </tr>
                    `;
                    userTableBody.append(row);
                });

                $('#user-modal').removeClass('hidden');
            } else {
                alert('Failed to load user data');
            }
        },
        error: function (error) {
            console.error('Error fetching user data:', error);
            alert('Error fetching user data');
        }
    });
}

function closeModal() {
    $('#user-modal').addClass('hidden');
}

function selectUser(userId, userName) {
    // Hiển thị thông tin người dùng đã chọn
    const userDetails = `
        <p><strong>ID:</strong> ${userId}</p>
        <p><strong>Name:</strong> ${userName}</p>
    `;
    $('#user-details').html(userDetails);
    $('#selected-user-info').removeClass('hidden');

    closeModal();
}
