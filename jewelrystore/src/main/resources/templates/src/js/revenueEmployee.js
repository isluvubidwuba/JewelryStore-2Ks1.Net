let currentPage = 0; // Trang hiện tại mặc định là 0
const pageSize2 = 5; // Kích thước trang mặc định là 5

$(document).ready(function () {
  $("#closeViewModalBtn").on("click", closeModal);
  $("#loadMoreInvoiceEmplBtn").on("click", function () {
    const employeeId = $("#employeeId").text().split(": ")[1];
    loadInvoices(employeeId, currentPage, pageSize2);
  });

  // Sự kiện thay đổi khoảng thời gian trong modal
  $("#revenuePeriodModal").on("change", handlePeriodChangeModal);
  $("#monthSelectModal").on("change", handleMonthChangeModal);
  $("#yearSelectModal").on("change", handleYearChangeModal);
});

function handlePeriodChangeModal() {
  const period = $("#revenuePeriodModal").val();
  if (period === "month") {
    $("#monthSelectModal").removeClass("hidden");
    $("#yearSelectModal").addClass("hidden");
  } else {
    $("#monthSelectModal").addClass("hidden");
    $("#yearSelectModal").removeClass("hidden");
  }
}

function handleMonthChangeModal() {
  const employeeId = $("#employeeId").text().split(": ")[1];
  const month = $("#monthSelectModal").val();
  const year = new Date().getFullYear();
  Update(employeeId, "month", year, month);
}

function handleYearChangeModal() {
  const employeeId = $("#employeeId").text().split(": ")[1];
  const yearOption = $("#yearSelectModal").val();
  const year =
    yearOption === "currentYear"
      ? new Date().getFullYear()
      : new Date().getFullYear() - 1;
  Update(employeeId, "year", year);
}

// Hàm hiển thị thông tin nhân viên
function showEmployeeInfo(data) {
  $("#employeeName").text(
    `${data.employee.firstName} ${data.employee.lastName}`
  );
  $("#employeeRole").text(data.employee.role.name);
  $("#employeeId").text(`ID Employee: ${data.employee.id}`);
  $("#employeePhone").text(`Phone: ${data.employee.phoneNumber}`);
  $("#employeeEmail").text(`Email: ${data.employee.email}`);
  $("#employeeAddress").text(`Address: ${data.employee.address}`);
  // Cập nhật thuộc tính src cho ảnh nhân viên
  $("#employeeImage").attr("src", `${data.employee.image}`);
  // Hiển thị doanh thu với định dạng tiền tệ
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
  const startIndex = currentPage * pageSize2; // Tính toán số thứ tự bắt đầu của trang hiện tại
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
function viewEmployee(
  idEmployee,
  period = "month",
  year = new Date().getFullYear(),
  month = new Date().getMonth() + 1
) {
  // Đặt giá trị mặc định cho tháng hiện tại
  $("#monthSelectModal").val(month);
  currentPage = 0; // Reset lại trang hiện tại về 0 khi hiển thị nhân viên mới
  $("#invoiceEmployeeBody").empty(); // Xóa nội dung của bảng trước khi tải dữ liệu mới
  $.ajax({
    url: `http://localhost:8080/invoice/revenue/employeeId`,
    type: "GET",
    data: {
      period: period,
      year: year,
      month: period === "month" ? month : null,
      employeeId: idEmployee,
    },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        showEmployeeInfo(response.data);
        openModal();
        loadInvoices(idEmployee, currentPage, pageSize2);
      } else {
        alert("Không thể lấy dữ liệu doanh thu của nhân viên.");
      }
    },
    error: function (error) {
      alert("Đã xảy ra lỗi khi gọi API.");
    },
  });
}
// Cập nhật hàm viewEmployee để nhận thêm tham số period và month cho modal
function Update(
  idEmployee,
  period = "month",
  year = new Date().getFullYear(),
  month = new Date().getMonth() + 1
) {
  $.ajax({
    url: `http://localhost:8080/invoice/revenue/employeeId`,
    type: "GET",
    data: {
      period: period,
      year: year,
      month: period === "month" ? month : null,
      employeeId: idEmployee,
    },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        showEmployeeInfo(response.data);
      } else {
        alert("Không thể lấy dữ liệu doanh thu của nhân viên.");
      }
    },
    error: function (error) {
      alert("Đã xảy ra lỗi khi gọi API.");
    },
  });
}

// Hàm tải danh sách hóa đơn
function loadInvoices(employeeId, page, size) {
  $.ajax({
    url: `http://localhost:8080/invoice/employee`,
    type: "GET",
    data: {
      employeeId: employeeId,
      page: page,
      size: size,
    },
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        showInvoiceList(response.data.content);
        currentPage++; // Tăng số trang sau mỗi lần tải
        // Kiểm tra xem đã đến trang cuối cùng chưa
        if (response.data.last) {
          $("#loadMoreInvoiceEmplBtn").addClass("hidden"); // Ẩn nút "Load More"
        } else {
          $("#loadMoreInvoiceEmplBtn").removeClass("hidden"); // Hiển thị nút "Load More" nếu chưa đến trang cuối
        }
      } else {
        alert("Không thể lấy danh sách hóa đơn.");
      }
    },
    error: function (error) {
      alert("Đã xảy ra lỗi khi gọi API.");
    },
  });
}

// Hàm đóng modal
function closeModal() {
  $("#viewEmployeeModal").addClass("hidden");
}

// Hàm mở modal
function openModal() {
  $("#viewEmployeeModal").removeClass("hidden");
}

// Hàm định dạng tiền tệ
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}