class UserService {
  constructor() {
    this.token = localStorage.getItem("token");
    this.userId = localStorage.getItem("userId");
    this.userRole = localStorage.getItem("userRole");
    this.apiurl = process.env.API_URL;
  }

  getToken() {
    return this.token;
  }

  getUserId() {
    return this.userId;
  }

  getUserRole() {
    return this.userRole;
  }

  getApiUrl() {
    return this.apiurl;
  }

  setToken(token) {
    this.token = token;
    localStorage.setItem("token", token);
  }

  setUserId(userId) {
    this.userId = userId;
    localStorage.setItem("userId", userId);
  }

  setUserRole(userRole) {
    this.userRole = userRole;
    localStorage.setItem("userRole", userRole);
  }

  parseJwt(token) {
    var base64Url = token.split(".")[1];
    var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    var jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  }
  setUpAjax() {
    $.ajaxSetup({
      xhrFields: {
        withCredentials: true, // Ensures cookies are included for all AJAX calls
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      crossDomain: true,
    });
  }
  setUp;
}
var token = localStorage.getItem("token");
var userId = localStorage.getItem("userId");
var userRole = localStorage.getItem("userRole");
var apiurl = process.env.API_URL;
$(document).ready(function () {
  //setUpAjax();
  if ($("#loginForm").length === 0 && !token) {
    window.location.href = "Login.html";
  }
  logout();
});

function logout() {
  $("#logout").click(function (event) {
    event.preventDefault(); // Prevent the default link behavior
    $.ajax({
      url: `http://${apiurl}/api/authentication/logout`, // Replace with your API endpoint
      type: "POST",
      success: function () {
        localStorage.removeItem("token");
        window.location.href = "Login.html";
      },
      error: function (xhr) {
        showNotification("Logout failed:" + xhr.responseJSON?.desc);
      },
    });
  });
}

// async function setupAjaxPost(
//   url,
//   successCallback,
//   errorCallback,
//   formToObject
// ) {
//   try {
//     const formData = await formToObject();
//     await $.ajax({
//       url,
//       type: "POST",
//       data: JSON.stringify(formData),
//       processData: false,
//       contentType: "application/json; charset=utf-8",
//       success: successCallback,
//       error: errorCallback,
//     });
//   } catch (error) {
//     console.error("Error setting up AJAX POST request:", error);
//     if (errorCallback) {
//       errorCallback(error);
//     }
//   }
// }
