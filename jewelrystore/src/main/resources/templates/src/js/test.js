import UserService from "./userService.js";

const userService = new UserService();

let page = 0;
const pageSize = 6;
let chart;
// Hàm định dạng tiền tệ
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}

$(document).ready(function () {
  const currentMonth = new Date().getMonth() + 1;
  $("#monthSelect").val(currentMonth);
  // Khởi tạo biểu đồ
  const ctx = document.getElementById("myChart").getContext("2d");
  chart = new Chart(ctx, {
    type: "polarArea",
    data: {
      labels: [],
      datasets: [
        {
          label: "Total Revenue",
          data: [],
          backgroundColor: [
            "#3B82F6",
            "#10B981",
            "#6366F1",
            "#F59E0B",
            "#EF4444",
          ],
        },
      ],
    },
    options: {
      plugins: {
        legend: {
          position: "bottom",
        },
      },
    },
  });

  loadTop5EmployeesByRevenue();
  loadTop5ProductsByRevenue();
  loadCounterRevenue();

  $("#loadMoreBtn").on("click", function () {
    loadMoreInvoices();
  });

  $("#revenuePeriod").on("change", function () {
    const period = $(this).val();
    if (period === "month") {
      $("#monthSelect").removeClass("hidden");
      $("#yearSelect").addClass("hidden");
    } else if (period === "year") {
      $("#yearSelect").removeClass("hidden");
      $("#monthSelect").addClass("hidden");
    } else {
      $("#monthSelect, #yearSelect").addClass("hidden");
    }
    loadRevenueData();
  });
  $("#monthSelect, #yearSelect").on("change", function () {
    loadRevenueData();
  });

  // Tải 5 hóa đơn đầu tiên khi trang được tải
  loadMoreInvoices();
});

function loadRevenueData() {
  loadTop5EmployeesByRevenue();
  loadTop5ProductsByRevenue();
  loadCounterRevenue();
}
function loadMoreInvoices() {
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice?page=${page}&size=${pageSize}`,
    "GET",
    handleSuccessLoadMoreInvoices,
    handleErrorLoadMoreInvoices,
    null
  );
}
function handleSuccessLoadMoreInvoices(response) {
  const invoices = response.data.content;
  const invoiceBody = $("#invoiceBody");

  invoices.forEach((invoice, index) => {
    const rowClass = index % 2 === 0 ? "bg-gray-200" : "bg-white";
    const statusText = invoice.status ? "Completed" : "Cancelled";
    const row = `
        <tr class="${rowClass}">
          <td class="text-left p-2">${new Date(
            invoice.date
          ).toLocaleString()}</td>
          <td class="text-left p-2">${invoice.userInfoDTO.fullName}</td>
          <td class="text-left p-2">${invoice.employeeDTO.firstName} ${
      invoice.employeeDTO.lastName
    }</td>
          <td class="text-left p-2">${invoice.invoiceTypeDTO.name}</td>
          <td class="text-left p-2">${statusText}</td>
          <td class="text-left p-2">${invoice.totalPriceRaw}</td>
          <td class="text-left p-2">${invoice.totalPrice}</td>
          <td class="text-left p-2">${invoice.discountPrice}</td>
          <td class="text-left p-2">${invoice.payment.trim()}</td>
          <td class="text-left p-2">${invoice.note ? invoice.note : ""}</td>
        </tr>
      `;
    invoiceBody.append(row);
  });

  if (!response.data.last) {
    page++;
  } else {
    $("#loadMoreBtn").hide();
  }
}
function handleErrorLoadMoreInvoices(error) {
  console.error("Error loading invoices:", error);
}
// Các hàm tải dữ liệu
function loadTop5EmployeesByRevenue() {
  const period = $("#revenuePeriod").val();
  const year = new Date().getFullYear();
  const month = $("#monthSelect").val();
  const data = {
    period: period,
    year:
      period === "year" && $("#yearSelect").val() === "lastYear"
        ? year - 1
        : year,
  };

  if (period === "month") {
    data.month = month;
  }
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/revenue/top5employees?period=${
      data.period
    }&year=${data.year}&month=${data.month}`,
    "GET",
    handleSuccessLoadTop5EmployeesByRevenue,
    handleErrorLoadTop5EmployeesByRevenue,
    null
  );
}
function handleErrorLoadTop5EmployeesByRevenue(error) {
  console.error(
    "Lỗi khi tải danh sách 5 nhân viên có doanh thu cao nhất:",
    error
  );
}
function handleSuccessLoadTop5EmployeesByRevenue(response) {
  const employeesData = response.data;
  const topRepsContainer = $("#topReps");
  topRepsContainer.empty();

  employeesData.forEach(function (employeeData) {
    const employee = employeeData.entity;
    const formattedRevenue = formatCurrency(employeeData.revenue);

    const repCard = `
                      <div class="flex flex-col bg-gray-50 max-w-sm shadow-md py-4 px-10 md:px-8 rounded-md cursor-pointer" onclick="viewEmployee2('${
                        employee.id
                      }')">
                        <div class="flex flex-col md:flex-row gap-2 md:gap-4">
                          <img class="rounded-full border-4 border-gray-300 h-24 w-24 mx-auto mb-4 object-cover"
                            src="${
                              employee.image
                                ? employee.image
                                : "https://randomuser.me/api/portraits/men/78.jpg"
                            }"
                            alt="${employee.firstName} ${employee.lastName}" />
                          <div class="flex flex-col text-center md:text-left">
                            <div class="text-zinc-900 text-lg font-semibold">${
                              employee.firstName
                            } ${employee.lastName}</div>
                            <div class="text-gray-500 mb-3 whitespace-nowrap">${formattedRevenue}</div>
                          </div>
                        </div>
                      </div>
                    `;

    topRepsContainer.append(repCard);
  });
}

function loadCounterRevenue() {
  const period = $("#revenuePeriod").val();
  const year = new Date().getFullYear();
  const month = $("#monthSelect").val();
  const data = {
    period: period,
    year:
      period === "year" && $("#yearSelect").val() === "lastYear"
        ? year - 1
        : year,
    quarterOrMonth: month,
  };
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/revenue/counter?period=${
      data.period
    }&year=${data.year}&quarterOrMonth=${data.quarterOrMonth}`,
    "GET",
    handleSuccessLoadCounterRevenue,
    handleErrorLoadCounterRevenue,
    null
  );
}
function handleSuccessLoadCounterRevenue(response) {
  const countersData = response.data;
  const counterList = $("#counterList");
  counterList.empty(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới

  // Thêm tiêu đề cho bảng
  const headerRow = `
    <li class="py-3 flex justify-between text-sm text-black font-semibold">
      <p class="px-4 font-semibold">Counter Name</p>
      <p class="px-4 text-black">Revenue</p>
    </li>
  `;
  counterList.append(headerRow);

  const labels = [];
  const revenues = [];

  // Duyệt qua danh sách các quầy và thêm từng quầy vào thẻ counterList
  countersData.forEach(function (counterData, index) {
    const counter = counterData.entity;
    const formattedRevenue = formatCurrency(counterData.revenue);

    // Tạo hàng cho từng quầy
    const row = `
      <li class="py-3 flex justify-between text-sm text-gray-500 font-semibold">
        <p class="px-4 text-gray-600">${counter.name}</p>
        <p class="px-4 text-blue-600">${formattedRevenue}</p>
      </li>
    `;
    counterList.append(row);

    // Thêm dữ liệu vào biểu đồ
    labels.push(counter.name);
    revenues.push(counterData.revenue);
  });

  // Cập nhật dữ liệu biểu đồ
  updateChart(chart, labels, revenues);
}
function handleErrorLoadCounterRevenue(error) {
  console.error("Lỗi khi tải doanh thu các quầy:", error);
}

function updateChart(chart, labels, data) {
  chart.data.labels = labels;
  chart.data.datasets[0].data = data;
  chart.update();
}

function loadTop5ProductsByRevenue() {
  const period = $("#revenuePeriod").val();
  const year = new Date().getFullYear();
  const month = $("#monthSelect").val();
  const data = {
    period: period,
    year:
      period === "year" && $("#yearSelect").val() === "lastYear"
        ? year - 1
        : year,
  };

  if (period === "month") {
    data.month = month;
  }
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice/revenue/top5products?period=${
      data.period
    }&year=${data.year}&month=${data.month}`,
    "GET",
    handleSuccessLoadTop5ProductsByRevenue,
    handleErrorLoadTop5ProductsByRevenue,
    null
  );
}
function handleErrorLoadTop5ProductsByRevenue(error) {
  console.error(
    "Lỗi khi tải danh sách 5 sản phẩm có doanh thu cao nhất:",
    error
  );
}
function handleSuccessLoadTop5ProductsByRevenue(response) {
  const productsData = response.data;
  const topProductsContainer = $("#topProducts");
  topProductsContainer.empty(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới

  // Duyệt qua danh sách sản phẩm và thêm từng sản phẩm vào thẻ topProductsContainer
  productsData.forEach(function (productData) {
    const product = productData.entity;
    const formattedRevenue = formatCurrency(productData.revenue);

    // Chỉnh sửa phần hiển thị thông tin sản phẩm
    const productCard = `
        <div class="bg-white  rounded-lg shadow-md p-4">
            <img src="https://storage.googleapis.com/jewelrystore-2ks1dotnet.appspot.com/Product/${product.imgPath}" 
                alt="${product.name}" class="w-full h-32 object-cover rounded-md mb-4" />
            <h3 class="text-zinc-900  text-lg font-semibold">${product.name}</h3>
            <p class="text-zinc-600 ">${formattedRevenue}</p>
        </div>
      `;
    topProductsContainer.append(productCard);
  });
}
