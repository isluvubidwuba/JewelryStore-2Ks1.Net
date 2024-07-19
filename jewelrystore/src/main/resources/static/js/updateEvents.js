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
// Hàm để tính toán và hiển thị ngày tương ứng cho mỗi ngày trong tuần
function updateDayDates(startDateStr) {
  const startDate = new Date(startDateStr.split("/").reverse().join("/")); // Chuyển đổi định dạng ngày
  const daysOfWeek = [
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
  ];

  // Tính toán và hiển thị ngày cho mỗi ngày trong tuần
  daysOfWeek.forEach((day, index) => {
    const currentDate = new Date(startDate);
    currentDate.setDate(startDate.getDate() + index);
    const formattedDate = formatDate(currentDate);

    $(`.top-info:contains(${day}) .date`).text(` - ${formattedDate}`);
    $(`.top-info:contains(${day})`).attr("data-date", formattedDate);
  });
}
// Hàm để cập nhật sự kiện
function updateEvents() {
  const selectedWeek = $("#week-dropdown").val();
  const selectedYear = $("#year-dropdown").val();
  console.log(
    `Updating events for year ${selectedYear} and week ${selectedWeek}`
  );

  const [startDate, endDate] = selectedWeek.split(" to ");
  updateDayDates(startDate + "/" + selectedYear);

  // Thêm xử lý để cập nhật sự kiện từ API
  userService
    .sendAjaxWithAuthen(
      `http://${userService.apiurl}/api/schedule/events?startDateStr=${startDate}/${selectedYear}&endDateStr=${endDate}/${selectedYear}`,
      "GET",

      null
    )
    .then(function (response) {
      if (response.status === "OK") {
        processEventsData(response.data);
      } else {
        console.error("Error fetching events:", response.desc);
      }
    })
    .catch(function (err) {
      console.error("Error fetching events:", err);
    });
}

// Hàm để xử lý dữ liệu sự kiện
function processEventsData(eventsData) {
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
      // Sắp xếp các counters theo thứ tự số
      const sortedCounters = eventsData[day].sort((a, b) => {
        const aNumber = a.counterDTO.id;
        const bNumber = b.counterDTO.id;
        return aNumber - bNumber;
      });

      sortedCounters.forEach((event) => {
        const counterDTO = event.counterDTO;
        const manager = event.assignShiftForStaffDTO.employeeDTO;
        const assign = event.assignShiftForStaffDTO;
        const counterNumber = counterDTO.id;
        const checkInStatus = assign.checkIn
          ? assign.isLate
            ? "Lated"
            : "Checked"
          : "Not Yet";

        $eventsGroup.append(`
          <li class="single-event event-type-${
            counterNumber - 1
          } hover:bg-opacity-75 shadow-inner h-16 w-1/6 mr-5">
            <a href="#0" class="block h-full p-2 open-employee-modal" data-color='${
              counterNumber - 1
            }' 
              data-assign='${JSON.stringify(assign)}' 
              data-counter='${JSON.stringify(counterDTO)}'>
              <em class="event-counter block text-white">
                ${counterDTO.name} -
                <span class="${
                  checkInStatus == "Not Yet" ? "text-red-800" : "text-black"
                } text-xs align-top">${checkInStatus}</span>
              </em>
              <em class="event-name block text-white">${manager.firstName} ${
          manager.lastName
        }</em>
            </a>
          </li>
        `);
      });
    }

    const today = new Date();
    today.setHours(0, 0, 0);
    const dateAttr = $(`.top-info:contains(${day})`).attr("data-date");

    if (dateAttr) {
      const [dayPart, monthPart] = dateAttr.split("/");
      const currentYear = $("#year-dropdown").val();
      const dateToCheck = new Date(`${currentYear}-${monthPart}-${dayPart}`);
      const userRole = sessionStorage.getItem("userRole");
      if (
        dateToCheck >= today &&
        (userRole == "ADMIN" || userRole == "MANAGER")
      ) {
        // Thêm biểu tượng cộng để thêm người dùng mới
        $eventsGroup.append(`
          <li class="single-event event-add-user mr-5">
            <a href="#0" class="block h-full p-2 mt-5 open-modal">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6 mx-auto my-auto">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
              </svg>
            </a>
          </li>
        `);
      }
    }
  });

  // Thêm sự kiện click cho thẻ sự kiện để hiển thị modal
  $(".open-employee-modal").click(function () {
    const counter = $(this).data("counter");
    const assign = $(this).data("assign");
    const manager = assign.employeeDTO;
    const color = $(this).data("color");
    // Xóa tất cả các lớp bắt đầu với 'event-type-'
    $("#backgroundColor").removeClass(function (index, className) {
      return (className.match(/(^|\s)event-type-\S+/g) || []).join(" ");
    });
    $("#backgroundColor").addClass("event-type-" + color);

    console.log("color: " + color);
    // Xử lý logic check-in, check-out và isLate
    const checkInStatus = assign.checkIn
      ? new Date(assign.checkIn).toLocaleString()
      : "Not Yet";
    const checkOutStatus = assign.checkOut
      ? new Date(assign.checkOut).toLocaleString()
      : "Not Yet";
    const isLateStatus =
      assign.isLate === null ? "Not Yet" : assign.isLate ? "Late" : "On time";

    // Điền thông tin vào modal
    $("#employeeImage").attr(
      "src",
      manager.image
        ? `${manager.image}`
        : "https://randomuser.me/api/portraits/men/94.jpg"
    );
    $("#employeeName").text(`${manager.firstName} ${manager.lastName}`);
    $("#employeeRole").text(manager.role.name);
    $("#employeeId2").text(`ID Employee: ${manager.id}`);

    $("#employeePhone").text(`Phone: ${manager.phoneNumber}`);
    $("#employeeEmail").text(`Email: ${manager.email}`);
    $("#employeeAddress").text(`Address: ${manager.address}`);

    // Điền thông tin shift
    $("#shiftCounter").text(`${counter.name}`);
    $("#checkInStatus")
      .text(`${checkInStatus}`)
      .addClass(
        `${checkInStatus == "Not Yet" ? "text-red-600" : "text-green-600"}`
      );

    $("#checkOutStatus")
      .text(`${checkOutStatus}`)
      .addClass(
        `${checkOutStatus == "Not Yet" ? "text-red-600" : "text-green-600"}`
      );
    $("#isLateStatus")
      .text(`${isLateStatus}`)
      .addClass(
        `${isLateStatus == "On time" ? "text-green-600" : "text-red-600"}`
      );

    const isoDateStr = assign.date;
    const date = new Date(isoDateStr);

    // Adjust to the desired timezone (+7 hours)
    const offsetDate = new Date(date.getTime() + 7 * 60 * 60 * 1000);

    // Format the date to "yyyy-MM-dd"
    const formattedDate = offsetDate.toISOString().split("T")[0];

    // Kiểm tra điều kiện để hiển thị hoặc ẩn nút
    if (formattedDate <= new Date().toISOString().split("T")[0]) {
      $("#buttonDeleteSChedule").addClass("hidden"); // Ẩn nút nếu ngày là quá khứ hoặc đã check-in
    } else {
      $("#buttonDeleteSChedule").removeClass("hidden"); // Hiển thị nút nếu ngày không phải là quá khứ và chưa check-in
    }
    $("#date-shift").text("#" + formattedDate);

    $("#formDelete").empty();
    // Gán giá trị vào các input ẩn
    $("#hiddenDate").val(formattedDate);
    $("#hiddenCounterId").val(counter.id);
    $("#hiddenEmployeeId").val(manager.id);
    // Hiển thị modal

    $("#viewEmployeeModal2").removeClass("hidden");
  });

  // Đóng modal
  $("#closeViewModalBtn2").click(function () {
    $("#viewEmployeeModal2").addClass("hidden");
  });

  // Thêm sự kiện click cho biểu tượng cộng để hiển thị modal
  $(".open-modal").click(function () {
    const dayElement = $(this).closest(".events-group").find(".top-info");
    const day = dayElement.text().trim();
    const date = dayElement.attr("data-date");

    const selectedYear = $("#year-dropdown").val();
    const [dayPart, monthPart] = date.split("/");

    const formattedDate = `${selectedYear}-${monthPart}-${dayPart}`;

    $("#create-modal .date").text(`${day}`);
    $("#date").val(formattedDate);

    $("#create-modal").removeClass("hidden");
    loadEmployeeAndCounters();
  });

  // Đóng modal
  $("#modalClose").click(function () {
    $("#create-modal").addClass("hidden");
  });
}
// Sự kiện submit form xóa lịch
$("#deleteForm").submit(function (e) {
  e.preventDefault();
  // Mở modal xác nhận
  $("#deleteModal").removeClass("hidden");

  // Lưu trữ dữ liệu form để sử dụng sau khi xác nhận
  const formData = new FormData(this);
  console.log("formData: " + formData);

  // Sự kiện khi người dùng xác nhận xóa
  $("#confirmDelete")
    .off("click")
    .on("click", function () {
      userService
        .sendAjaxWithAuthen(
          `http://${userService.apiurl}/api/schedule/delete`,
          "POST",
          formData
        )
        .then(function (response) {
          if (response.status === "OK") {
            showNotification(response.desc, "OK");
            $("#deleteModal").addClass("hidden");
            $("#viewEmployeeModal2").addClass("hidden");
            updateEvents(); // Refresh the events
          } else {
            showNotification(response.responseJSON.desc);
          }
        })
        .catch(function (err) {
          console.log(err);
          showNotification(err.responseJSON.desc);
        });
    });

  // Sự kiện khi người dùng hủy bỏ xóa
  $("#cancelDelete, #closeDelete")
    .off("click")
    .on("click", function () {
      $("#deleteModal").addClass("hidden");
    });
});

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

  // Hiển thị modal khi người dùng nhấn nút gửi lịch
  $("#sendMailToEmployee").on("click", function () {
    $("#confirmSendModal").removeClass("hidden");
  });

  // Đóng modal khi người dùng nhấn nút đóng hoặc hủy
  $("#closeSendModal, #cancelSendModal").on("click", function () {
    $("#confirmSendModal").addClass("hidden");
  });
  $("#confirmSendMail").on("click", async function () {
    await sendSchedule();
  });

  // Cập nhật sự kiện khi người dùng chọn tuần mới
  $("#week-dropdown").change(function () {
    updateEvents();
    // compareDates();
  });

  // Xử lý gửi form để tạo lịch mới
  $("#form-insert").submit(function (event) {
    event.preventDefault();

    const formData = $(this).serialize();

    sendScheduleFormData(formData);
  });
});

async function sendSchedule() {
  const selectedWeek = $("#week-dropdown").val();
  const selectedYear = $("#year-dropdown").val();
  const [startDate, endDate] = selectedWeek.split(" to ");
  const startDateStr = startDate.trim() + "/" + selectedYear;
  const endDateStr = endDate.trim() + "/" + selectedYear;

  // Hiển thị overlay
  $("#overlay-sendmail").removeClass("hidden");

  try {
    const response = await userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/schedule/sendSchedule`,
      "POST",
      $.param({
        startDateStr: startDateStr,
        endDateStr: endDateStr,
      })
    );

    if (response.status === "OK") {
      showNotification(response.desc, "OK");
      $("#confirmSendModal").addClass("hidden");
    } else {
      console.error("Error creating schedule:", response.desc);
      showNotification(response.desc);
    }
  } catch (err) {
    console.error("Error creating schedule:", err);
    showNotification(err.responseJSON.desc);
    $("#confirmSendModal").addClass("hidden");
  } finally {
    // Ẩn overlay
    $("#overlay-sendmail").addClass("hidden");
  }
}
function sendScheduleFormData(formData) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/schedule/assign`,
      "POST",
      formData
    )
    .then(function (response) {
      if (response.status === "OK") {
        showNotification(response.desc, "OK");
        $("#create-modal").addClass("hidden");
        updateEvents();
      } else {
        console.error("Error creating schedule:", response.desc);
      }
    })
    .catch(function (err) {
      console.error("Error creating schedule:", err);
    });
}

// Hàm để load danh sách employee và counter
function loadEmployeeAndCounters() {
  // Load danh sách employee
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/employee/getstaff`,
      "GET",
      null
    )
    .then(function (response) {
      if (response.status === "OK") {
        const employees = response.data;
        const $employeeSelect = $("#employeeId");
        $employeeSelect.empty();
        employees.forEach((employee) => {
          $employeeSelect.append(
            `<option value="${employee.id}">${employee.firstName} ${employee.lastName} - ${employee.id}</option>`
          );
        });
      } else {
        console.error("Error fetching employees:", response.desc);
      }
    })
    .catch(function (err) {
      console.error("Error fetching employees:", err);
    });

  // Load danh sách counter
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/counter/allactivecounter`,
      "GET",
      null
    )
    .then(function (response) {
      if (response.status === "OK") {
        const counters = response.data;
        const $counterSelect = $("#counterId");
        $counterSelect.empty();
        counters.forEach((counter) => {
          if (counter.id == 1) {
            return;
          }
          $counterSelect.append(
            `<option value="${counter.id}">${counter.name}</option>`
          );
        });
      } else {
        console.error("Error fetching counters:", response.desc);
      }
    })
    .catch(function (err) {
      console.error("Error fetching counters:", err);
    });
}
