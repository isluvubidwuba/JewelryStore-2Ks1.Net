import UserService from "./userService.js";

const userService = new UserService();
const userRole = userService.getUserRole();
// logout
function Logout() {
  $("#logout").click(async function (event) {
    event.preventDefault();
    await LogoutApi();
  });
}

export async function LogoutApi() {
  return await userService
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
}

async function authenticate() {
  try {
    if ($("#loginForm").length === 0) {
      let userAuthenticated = await userService.authenticate();
      if (!userAuthenticated.authenticated) {
        console.log("Authenticate ne :" + userAuthenticated);
        window.location.href = "Login.html";
      }
      // if(userService.getUserRole === "STAFF" && "check checin chua")
    }
  } catch (error) {
    console.error("Authentication failed:", error);
    window.location.href = "Login.html";
  }
}
authenticate();
function setupDashboardButton() {
  if (userRole === 'ADMIN' || userRole === 'MANAGER') {
    $('#changeScreen').show();
  }

  $('#changeScreen').on('click', function () {
    if (userRole === 'ADMIN' || userRole === 'MANAGER') {
      window.location.href = 'DashboardAdmin.html';
    } else {
      showNotification("You can not access this page", "Error");
    }
  });
}

$(document).ready(function () {
  Logout();
  setupDashboardButton();
});
