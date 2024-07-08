import UserService from "./userService.js";

const userService = new UserService();

// logout
function Logout() {
  $("#logout").click(function (event) {
    event.preventDefault();
    userService.sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/authentication/logout`,
      "POST",
      handleSuccessLogout,
      handleErrorLogout,
      null
    );
  });
}
function handleSuccessLogout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("userRole");
  window.location.href = "Login.html";
}
function handleErrorLogout(xhr) {
  showNotification("Logout failed:" + xhr.responseJSON?.desc);
}

function authenticate() {
  if ($("#loginForm").length === 0 && userService.authenticate.authenticated) {
    window.location.href = "Login.html";
  }
}
authenticate();
$(document).ready(function () {
  Logout();
});
