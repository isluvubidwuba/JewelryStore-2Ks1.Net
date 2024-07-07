var token = localStorage.getItem("token");
var userId = localStorage.getItem("userId");
var userRole = localStorage.getItem("userRole");
var apiurl = process.env.API_URL;

function parseJwt(token) {
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
function setUpAjax() {
  $.ajaxSetup({
    xhrFields: {
      withCredentials: true, // Ensures cookies are included for all AJAX calls
    },
    crossDomain: true,
  });
  if (token) {
    $.ajaxSetup({
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
function getCookie(name) {
  let cookieArr = document.cookie.split(";");

  for (let i = 0; i < cookieArr.length; i++) {
    let cookiePair = cookieArr[i].split("=");

    // Removing whitespace at the beginning of the cookie name and compare it with the given string
    if (name == cookiePair[0].trim()) {
      // Decode the cookie value and return
      return decodeURIComponent(cookiePair[1]);
    }
  }

  // Return null if not found
  return null;
}
function setCookie(name, value, days) {
  let expires = "";
  if (days) {
    let date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie =
    name + "=" + (value || "") + expires + "; path=/ ; SameSite=Strict";
}
$(document).ready(function () {
  setUpAjax();
  console.log(getCookie("rt"));
  if ($("#loginForm").length === 0 && !token) {
    window.location.href = "Login.html";
  }
  logout();
});

function logout() {
  $("#logout").click(function (event) {
    event.preventDefault(); // Prevent the default link behavior
    $.ajax({
      url: `http://${apiurl}/authentication/logout`, // Replace with your API endpoint
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
