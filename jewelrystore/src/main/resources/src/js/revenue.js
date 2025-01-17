import UserService from "./userService.js";

const userService = new UserService();
$(document).ready(function () {
  const periods = [
    "TODAY",
    "YESTERDAY",
    "LAST_WEEK",
    "LAST_MONTH",
    "LAST_90_DAYS",
  ];
  const ids = [
    "#today-status",
    "#yesterday-status",
    "#last-week-status",
    "#last-month-status",
    "#last-90-days-status",
  ];

  periods.forEach((period, index) => {
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/invoice/revenue/invoice-count?period=${period}`,
        "GET",
        null
      )
      .then((response) => {
        if (response.status === "OK") {
          const totalRevenue = response.data.totalRevenue.toFixed(2);
          const invoiceCount = response.data.invoiceCount;

          $(`${ids[index]} .num-3`).text(formatCurrency(totalRevenue));
          $(`${ids[index]} .num-2`).text(invoiceCount);
        }
      })
      .catch((xhr, status, error) => {
        console.error(`Error fetching data for ${period}:`, error);
      });
  });
});
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}
