import UserService from "./userService.js";

const userService = new UserService();

let currentPage2 = 0; // Trang hiện tại mặc định là 0
const pageSize2 = 5; // Kích thước trang mặc định là 5

$(document).ready(function () {
  $("#loadMoreInvoiceEmplBtn").on("click", function () {
    loadInvoices(currentPage2, pageSize2);
  });
  if (userService.getUserId()) {
    viewEmployee2();
  } else {
    alert("Không có thông tin người dùng, vui lòng đăng nhập lại!");
    window.location.href = "/login.html";
  }
  // Sự kiện thay đổi khoảng thời gian trong modal
  $("#revenuePeriodModal").on("change", handlePeriodChangeModal);
  $("#monthSelectModal").on("change", handleMonthChangeModal);
  $("#yearSelectModal").on("change", handleYearChangeModal);
});

function displayEmployeeDetails(employee) {
  $("#employeeImage img").attr("src", employee.image);
  const employeeInfo = `
  <div class="flex items-center">
                <div>
                  <h1
                    id="employeeName"
                    class="text-2xl font-playwrite"
                    onclick="editField('employeeName')"
                  >
                    <span class="value"> ${employee.firstName} ${employee.lastName}</span>
                  </h1>
                  <input
                    type="text"
                    id="employeeNameInput"
                    class="edit-mode"
                    name="firstName"
                  />
                  <h2 id="employeeRole" class="text-2xl text-gray-500">
                  ${employee.role.name}
                  </h2>
                </div>
              </div>`;
  $(".employee-info").html(employeeInfo);

  const contactHtml = `
  <div id="employeePhoneNumber" onclick="editField('employeePhoneNumber')">
    <strong>Phone Number:</strong>
    <span class="value">${employee.phoneNumber}</span>
  </div>
  <input type="text" id="employeePhoneNumberInput" class="edit-mode" />

  <div id="employeeEmail" onclick="editField('employeeEmail')">
    <strong>Email:</strong>
    <span class="value">${employee.email}</span>
  </div>
  <input type="email" id="employeeEmailInput" class="edit-mode" />

  <div id="employeeAddress" onclick="editField('employeeAddress')">
    <strong>Address:</strong>
    <span class="value">${employee.address}</span>
  </div>
  <input type="text" id="employeeAddressInput" class="edit-mode" />

  <div class="edit-buttons edit-mode">
    <button onclick="saveChanges()">Save</button>
    <button onclick="cancelChanges()">Cancel</button>
  </div>
`;
  $(".employee-contact").html(contactHtml);
}

function handlePeriodChangeModal() {
  const period = $("#revenuePeriodModal").val();
  if (period === "month") {
    $("#monthSelectModal").removeClass("hidden");
    $("#yearSelectModal").addClass("hidden");
    handleMonthChangeModal();
  } else {
    $("#monthSelectModal").addClass("hidden");
    $("#yearSelectModal").removeClass("hidden");
    handleYearChangeModal();
  }
}

function handleMonthChangeModal() {
  const month = $("#monthSelectModal").val();
  const year = new Date().getFullYear();
  Update(userService.getUserId(), "month", year, month);
}

function handleYearChangeModal() {
  const yearOption = $("#yearSelectModal").val();
  const year =
    yearOption === "currentYear"
      ? new Date().getFullYear()
      : new Date().getFullYear() - 1;
  Update(userService.getUserId(), "year", year);
}

// Hàm hiển thị thông tin nhân viên
function showEmployeeInfo(data) {
  displayEmployeeDetails(data.employee);
  $("#totalRevenue").text(formatCurrency(data.totalRevenue));
  $("#totalRevenueAfterDiscount").text(
    formatCurrency(data.totalRevenueAfterDiscount)
  );
  $("#invoiceEmployee").text(data.totalInvoices); // Số lượng hóa đơn
  $("#averageRevenue").text(formatCurrency(data.averageRevenue));
}

// Hàm hiển thị danh sách hóa đơn
function showInvoiceList(invoices) {
  const invoiceBody = document.getElementById("invoiceEmployeeBody");
  const startIndex = currentPage2 * pageSize2; // Tính toán số thứ tự bắt đầu của trang hiện tại
  invoices.forEach((invoice, index) => {
    const row = document.createElement("tr");

    row.innerHTML = `
        <td class="p-2 border-b border-gray-300">${startIndex + index + 1}</td>
        <td class="p-2 border-b border-gray-300">${new Date(
          invoice.date
        ).toLocaleDateString("vi-VN")}</td>
        <td class="p-2 border-b border-gray-300">${
          invoice.userInfoDTO.fullName
        }</td>
        <td class="p-2 border-b border-gray-300">${
          invoice.employeeDTO.firstName
        } ${invoice.employeeDTO.lastName}</td>
        <td class="p-2 border-b border-gray-300">${formatCurrency(
          invoice.totalPriceRaw
        )}</td>
        <td class="p-2 border-b border-gray-300">${formatCurrency(
          invoice.totalPrice
        )}</td>
        <td class="p-2 border-b border-gray-300">${formatCurrency(
          invoice.discountPrice
        )}</td>
      `;

    invoiceBody.appendChild(row);
  });
}

// Cập nhật hàm viewEmployee để nhận thêm tham số period và month cho modal
function viewEmployee2(
  period = "month",
  year = new Date().getFullYear(),
  month = new Date().getMonth() + 1
) {
  // Đặt giá trị mặc định cho tháng hiện tại
  $("#monthSelectModal").val(month);
  currentPage2 = 0; // Reset lại trang hiện tại về 0 khi hiển thị nhân viên mới
  $("#invoiceEmployeeBody").empty(); // Xóa nội dung của bảng trước khi tải dữ liệu mới
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/revenue/employeeId`,
    "GET",
    handleSuccessViewEmployee2,
    handleErrorViewEmployee2,
    $.param({
      period: period,
      year: year,
      month: period === "month" ? month : null,
      employeeId: userService.getUserId(),
    })
  );
}
function handleSuccessViewEmployee2(response) {
  if (response.status === "OK") {
    showEmployeeInfo(response.data);
    loadInvoices(currentPage2, pageSize2);
  } else {
    showNotification("Error when retrieving employee revenue data.", "Error");
  }
}
function handleErrorViewEmployee2() {
  showNotification("Error when call API.", "Error");
}
// Cập nhật hàm viewEmployee để nhận thêm tham số period và month cho modal
function Update(
  idEmployee,
  period = "month",
  year = new Date().getFullYear(),
  month = new Date().getMonth() + 1
) {
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/revenue/employeeId`,
    "GET",
    handleSuccessUdate,
    handleErrorUpdate,
    $.param({
      period: period,
      year: year,
      month: period === "month" ? month : null,
      employeeId: idEmployee,
    })
  );
}
function handleErrorUpdate() {
  showNotification("Error when call API.", "Error");
}
function handleSuccessUdate(response) {
  if (response.status === "OK") {
    showEmployeeInfo(response.data);
  } else {
    showNotification("Error when retrieving employee revenue data.", "Error");
  }
}
// Hàm tải danh sách hóa đơn
function loadInvoices(page, size) {
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/employee2`,
    "GET",
    handleSuccessloadInvoices,
    handleErrorloadInvoices,
    $.param({
      employeeId: userService.getUserId(),
      page: page,
      size: size,
    })
  );
}
function handleErrorloadInvoices() {
  showNotification("Error when call API.", "Error");
}
function handleSuccessloadInvoices(response) {
  if (response.status === "OK") {
    showInvoiceList(response.data.content);
    currentPage2++; // Tăng số trang sau mỗi lần tải
    // Kiểm tra xem đã đến trang cuối cùng chưa
    if (response.data.last) {
      $("#loadMoreInvoiceEmplBtn").addClass("hidden"); // Ẩn nút "Load More"
    } else {
      $("#loadMoreInvoiceEmplBtn").removeClass("hidden"); // Hiển thị nút "Load More" nếu chưa đến trang cuối
    }
  } else {
    showNotification("Load fail invoice.", "Error");
  }
}
// Hàm định dạng tiền tệ
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}
