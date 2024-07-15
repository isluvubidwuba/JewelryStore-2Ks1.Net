import UserService from "./userService.js";

const userService = new UserService();

// logout
function Logout() {
  $("#logout").click(function (event) {
    event.preventDefault();
    userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/authentication/logout`,
        "POST",
        null
      )
      .then(() => {
        sessionStorage.clear();
        window.location.href = "Login.html";
      })
      .catch((response) => {
        showNotification("Logout failed:" + response.responseJSON?.desc);
      });
  });
}

async function authenticate() {
  try {
    if ($("#loginForm").length === 0) {
      let userAuthenticated = await userService.authenticate();
      if (!userAuthenticated.authenticated) {
        console.log("Authenticate ne :" + userAuthenticated);
        window.location.href = "Login.html";
      }
    }
  } catch (error) {
    console.error("Authentication failed:", error);
    window.location.href = "Login.html";
  }
}
authenticate();
$(document).ready(function () {
  Logout();
});
