import UserService from "./userService.js";
import { LogoutApi } from "./logout.js"; // Chú ý cặp ngoặc nhọn
const userService = new UserService();
const userRole = sessionStorage.getItem("userRole");

$(document).ready(function () {
  if (userRole === "STAFF") {
    CheckInStatus();
  }
  // Mở modal khi click vào nút Logout
  $("#logoutButton").click(function () {
    if (userRole === "STAFF") {
      $("#logoutModal").removeClass("hidden");
    }
    LogoutApi();
  });

  // Đóng modal khi click vào nút close
  $("#closeDelete, #cancelLogOut").click(function () {
    $("#logoutModal").addClass("hidden");
  });
  // Đóng modal khi click vào nút close
  $("#confirmNoShift, #confirmCheckOut").click(function () {
    LogoutApi();
  });
  $("#confirmCheckIn").click(function () {
    checkInApi();
  });

  // Xử lý khi click vào nút Yes, I'm sure
  $("#confirmLogOut").click(function () {
    if ($("#checkout").is(":checked")) {
      userService
        .sendAjaxWithAuthen(
          `http://${userService.getApiUrl()}/api/schedule/checkin-checkout`,
          "POST",
          null
        )
        .then(async () => {
          await LogoutApi();
        })
        .catch((response) => {
          showNotification("Logout failed:" + response.responseJSON?.desc);
        });
    } else {
      LogoutApi();
    }
  });
});

function checkInApi() {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/schedule/checkin-checkout`,
      "POST",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        showNotification(response.desc, "OK");
        $("#modalCheckIn").addClass("hidden");
      }
    })
    .catch((response) => {
      showNotification(response.desc, "ERROR");
    });
}

function CheckInStatus() {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/schedule/checkin-status`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        if (response.data === false) {
          // Sử dụng dấu bằng ba để so sánh
          $("#modalCheckIn").removeClass("hidden");
        } else if (response.data === true) {
          return;
        } else if (response.data === "NOT_FOUND") {
          $("#modalNoShift").removeClass("hidden");
        } else if (response.data === "BAD_REQUEST") {
          $("#modalCheckOut").removeClass("hidden");
        }
      }
    })
    .catch((response) => {
      console.log(response);
    });
}
