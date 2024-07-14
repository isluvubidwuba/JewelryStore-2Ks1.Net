import UserService from "./userService.js";

const userService = new UserService();

// Hàm để tính toán danh sách tuần trong một năm
function getWeeksInYear(year) {
  const weeks = [];
  let startDate = new Date(year, 0, 1);

  // Điều chỉnh ngày bắt đầu tuần để đảm bảo tuần bắt đầu từ thứ Hai
  while (startDate.getDay() !== 1) {
    startDate.setDate(startDate.getDate() + 1);
  }
  const targetYear = parseInt(year, 10);

  while (startDate.getFullYear() === targetYear) {
    const endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 6);

    weeks.push(`${formatDate(startDate)} to ${formatDate(endDate)}`);
    startDate.setDate(startDate.getDate() + 7);
  }

  return weeks;
}

// Hàm để định dạng ngày theo định dạng DD/MM
function formatDate(date) {
  const day = ("0" + date.getDate()).slice(-2);
  const month = ("0" + (date.getMonth() + 1)).slice(-2);
  return `${day}/${month}`;
}

// Hàm để cập nhật danh sách tuần khi chọn năm mới
function populateWeeks(year) {
  const weeks = getWeeksInYear(year);
  const $weekDropdown = $("#week-dropdown");
  $weekDropdown.empty();
  weeks.forEach((week) => {
    $weekDropdown.append(`<option value="${week}">${week}</option>`);
  });
}

// Hàm để lấy tuần hiện tại theo thời gian thực
function getCurrentWeek(year) {
  const today = new Date();
  if (year !== today.getFullYear()) return "";
  let startDate = new Date(year, 0, 1);

  // Điều chỉnh ngày bắt đầu tuần để đảm bảo tuần bắt đầu từ thứ Hai
  while (startDate.getDay() !== 1) {
    startDate.setDate(startDate.getDate() + 1);
  }

  today.setHours(0, 0, 0, 0); // Đặt giờ, phút, giây và mili giây của today về 0

  let week = "";
  while (startDate.getFullYear() === year) {
    const endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 6);
    endDate.setHours(23, 59, 59, 999); // Đặt giờ, phút, giây và mili giây của endDate về cuối ngày
    if (today >= startDate && today <= endDate) {
      week = `${formatDate(startDate)} to ${formatDate(endDate)}`;
      break;
    }
    startDate.setDate(startDate.getDate() + 7);
  }

  return week;
}

// Hàm để cập nhật sự kiện
function updateEvents() {
  const selectedWeek = $("#week-dropdown").val();
  const selectedYear = $("#year-dropdown").val();
  console.log(
    `Updating events for year ${selectedYear} and week ${selectedWeek}`
  );

  // Thêm xử lý để cập nhật sự kiện từ API
  userService.sendAjaxWithAuthen(
    "/api/events",
    "GET",
    function (data) {
      processEventsData(data);
    },
    function (err) {
      console.error("Error fetching events:", err);
    },
    null
  );
}

// Hàm để xử lý dữ liệu sự kiện
function processEventsData(eventsData) {
  const maxTypes = 8;
  const allDays = [
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
  ];

  allDays.forEach((day) => {
    const $eventsGroup = $(`.events-group:has(.top-info:contains(${day})) ul`);
    $eventsGroup.empty();

    if (eventsData[day]) {
      const dayEvents = eventsData[day];
      let index = 0;
      for (const [counter, name] of Object.entries(dayEvents)) {
        const type = (index % maxTypes) + 1;
        $eventsGroup.append(`
          <li class="single-event event-type-${type} hover:bg-opacity-75 shadow-inner h-16 w-1/6 mr-5">
            <a href="#0" class="block h-full p-2">
              <em class="event-counter block text-white">${counter}</em>
              <em class="event-name block text-white">Người quản lý: ${name}</em>
            </a>
          </li>
        `);
        index++;
      }
    }

    // Thêm biểu tượng cộng để thêm người dùng mới
    $eventsGroup.append(`
      <li class="single-event event-add-user hover:bg-opacity-75 shadow-inner h-16 w-1/6 mr-5">
        <a href="#0" class="block h-full p-2 mt-5">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6 mx-auto my-auto">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
          </svg>
        </a>
      </li>
    `);
  });
}

$(document).ready(function () {
  const currentYear = new Date().getFullYear();
  const years = [currentYear - 1, currentYear, currentYear + 1];

  years.forEach((year) => {
    $("#year-dropdown").append(`<option value="${year}">${year}</option>`);
  });

  // Khởi tạo danh sách tuần khi trang được tải
  $("#year-dropdown").val(currentYear);
  populateWeeks(currentYear);

  let currentWeek = getCurrentWeek(currentYear);
  if (!currentWeek) {
    currentWeek = getWeeksInYear(currentYear)[0];
  }

  $("#week-dropdown").val(currentWeek);
  updateEvents();

  // Cập nhật danh sách tuần khi người dùng chọn năm mới
  $("#year-dropdown").change(function () {
    const selectedYear = $(this).val();
    populateWeeks(selectedYear);
    let selectedWeek = getCurrentWeek(selectedYear);
    if (!selectedWeek) {
      selectedWeek = getWeeksInYear(selectedYear)[0];
    }
    $("#week-dropdown").val(selectedWeek);
    updateEvents();
  });

  // Cập nhật sự kiện khi người dùng chọn tuần mới
  $("#week-dropdown").change(function () {
    updateEvents();
  });
});
