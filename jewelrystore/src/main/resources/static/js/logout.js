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
      window.location.href = "/login";
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
        window.location.href = "/login";
      }
      if (
        $("#loginForm").length === 0 &&
        $("#adminPage").length !== 0 &&
        userRole === "STAFF"
      )
        window.location.href = "/defaultInvoice";
      else {
        $(document).ready(function () {
          Logout();
          setupDashboardButton();
          adminProfile();
          updateGoldPrice();
        });
      }
    }
  } catch (error) {
    console.error("Authentication failed:", error);
    window.location.href = "/login";
  }
}
await authenticate();
function setupDashboardButton() {
  if (userRole === "ADMIN" || userRole === "MANAGER") {
    $("#changeScreen").show();
  }

  $("#changeScreen").on("click", function () {
    if (userRole === "ADMIN" || userRole === "MANAGER") {
      window.location.href = "/dashboard";
    } else {
      showNotification("You can not access this page", "Error");
    }
  });
}

function adminProfile() {
  if (
    $("#adminPage").length !== 0 &&
    (userRole === "ADMIN" || userRole === "MANAGER")
  ) {
    $("#changePincodeProfileBtn").click(() => {
      $("#changePASS").removeClass("hidden");
      $("#ConfirmchangePASS").removeClass("hidden");
    });

    $("#closeProfileModal").click(() => {
      $("#viewProfileModal").addClass("hidden");
      $("#changePASS").addClass("hidden");
      $("#ConfirmchangePASS").addClass("hidden");
      $("#viewProfileForm")[0].reset();
      const fileInput = document.getElementById("profileImgUpdate");
      if (fileInput) {
        fileInput.value = "";
      }
    });
    $("#profileAdmin").click(async () => {
      await userService
        .sendAjaxWithAuthen(
          `http://${userService.getApiUrl()}/api/employee/myProfile`,
          "GET",
          null
        )
        .then((response) => {
          if (response.status === "OK") {
            const employee = response.data;
            const imageUrl = employee.image;

            $("#imgProfileDetail").attr("src", imageUrl);
            $("#profileIDemp").text(employee.role.name + ": " + employee.id);
            $("#viewProfileId").val(employee.id);
            $("#viewProfileFirstName").val(employee.firstName);
            $("#viewProfileLastName").val(employee.lastName);
            $("#viewProfilePhoneNumber").val(employee.phoneNumber);
            $("#viewProfileEmail").val(employee.email);

            $("#viewProfileModal").removeClass("hidden");
          } else {
            showNotification(
              "Failed to load profile details." + response.desc,
              "error"
            );
          }
        })
        .catch((error) => {
          showNotification("Error while fetching employee details", "error");
          if (error.responseJSON) {
            showNotification(
              "Error while fetching employee details" + error.responseJSON.desc,
              "error"
            );
          } else {
            console.error("Error while fetching employee details : ", error);
            showNotification("Error fetching employee details :!", "error");
          }
        });
    });
    $("#profileImgUpdate").change(function () {
      previewImage(
        "#profileImagePlaceHolder",
        $(this).prop("files")[0],
        "#imgProfileDetail"
      );
    });

    const checkPasswordMatch = () => {
      const password = $("#viewProfilePincode").val();
      const confirmPassword = $("#viewConfirmProfilePincode").val();
      if ($("#changePASS").hasClass("hidden")) {
        $("#viewProfilePincode").val(null);
        return true;
      }
      if (password == "" || confirmPassword == "") {
        showNotification("PinCode do not empty!");
        return false;
      }
      if (password !== confirmPassword) {
        showNotification("PinCode do not match!");
        return false;
      }
      return true;
    };
    $("#updateProfileBtn").click(async () => {
      if (!checkPasswordMatch()) return;
      var formData = new FormData($("#viewProfileForm")[0]);
      var fileInput = $("#profileImgUpdate")[0];

      formData.append("file", fileInput.files[0]);

      await userService
        .sendAjaxWithAuthen(
          `http://${userService.getApiUrl()}/api/employee/updateProfile`,
          "POST",
          formData
        )
        .then((response) => {
          if (response.status === "OK") {
            $("#viewProfileModal").addClass("hidden");
            $("#viewProfileForm")[0].reset();
            const fileInput = document.getElementById("profileImgUpdate");
            if (fileInput) {
              fileInput.value = "";
            }
            showNotification(response.desc, "OK");
          } else {
            showNotification(
              "Failed to update employee: " + response.desc,
              "error"
            );
          }
        })
        .catch((error) => {
          showNotification("Error update employee!", "error");
          if (error.responseJSON) {
            showNotification(
              "Failed while update employee: " + error.responseJSON.desc,
              "error"
            );
          } else {
            console.error("Error while update employee: ", error);
            showNotification("Error update employee!", "error");
          }
        });
    });
  }
}

function updateGoldPrice() {
  $("#updateGoldPrice").click(async () => {
    await userService
      .sendAjaxWithAuthen(
        `http://${userService.getApiUrl()}/api/material/update-gold-prices`,
        "GET",
        null
      )
      .then((response) => {
        showNotification(response.desc, "OK");
      })
      .catch((response) => {
        showNotification(response.responseJSON?.desc);
      });
  });
}
function previewImage(idDisplay, file, placeholder) {
  if (file) {
    $(idDisplay).empty();
    const reader = new FileReader();
    reader.onload = function (e) {
      const img = document.createElement("img");
      img.src = e.target.result;
      img.className =
        "flex flex-col items-center justify-center w-full h-3/6 border-2";
      $(idDisplay).append(img);
      $(placeholder).addClass("hidden").removeClass("flex");
    };
    reader.readAsDataURL(file);
  }
}

// Define the inactivity timeout in milliseconds (e.g., 5 minutes)
const inactivityTimeout = 30 * 1000;

// Variable to store the timeout ID
let inactivityTimer;

// Function to add the iframe and overlay
function addIframe() {
  const iframe = document.createElement("iframe");
  iframe.id = "goldPriceIframe";
  iframe.src = "/goldPrice";
  iframe.width = "100%";
  iframe.height = "100%";
  iframe.style.position = "fixed";
  iframe.style.top = "0";
  iframe.style.left = "0";
  iframe.style.zIndex = "1000";
  document.body.appendChild(iframe);

  const overlay = document.createElement("div");
  overlay.id = "activityOverlay";
  overlay.style.position = "fixed";
  overlay.style.top = "0";
  overlay.style.left = "0";
  overlay.style.width = "100%";
  overlay.style.height = "100%";
  overlay.style.zIndex = "1001";
  document.body.appendChild(overlay);

  // Add event listeners to the overlay to detect user activity
  ["click", "mousemove", "keypress", "scroll", "touchstart"].forEach(
    (event) => {
      overlay.addEventListener(event, () => {
        removeIframeAndOverlay();
        resetInactivityTimer(); // Reset timer after removing iframe and overlay
      });
    }
  );
}

// Function to remove the iframe and overlay
function removeIframeAndOverlay() {
  const iframe = document.getElementById("goldPriceIframe");
  if (iframe) {
    iframe.remove();
  }
  const overlay = document.getElementById("activityOverlay");
  if (overlay) {
    overlay.remove();
  }
}

// Function to reset the inactivity timer
function resetInactivityTimer() {
  clearTimeout(inactivityTimer);
  inactivityTimer = setTimeout(addIframe, inactivityTimeout);
}

// Add event listeners for user activity
["click", "mousemove", "keypress", "scroll", "touchstart"].forEach((event) => {
  window.addEventListener(event, resetInactivityTimer);
});

// Initialize the inactivity timer on page load
resetInactivityTimer();
