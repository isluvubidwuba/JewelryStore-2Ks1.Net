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

async function authenticate() {
  try {
    let userAuthenticated = await userService.authenticate();
    if ($("#loginForm").length === 0 && !userAuthenticated) {
      window.location.href = "Login.html";
    }
  } catch (error) {
    console.error("Authentication failed:", error);
    // Optionally, redirect to the login page or display an error message
    window.location.href = "Login.html";
  }
}

authenticate();
$(document).ready(function () {
  Logout();
});
