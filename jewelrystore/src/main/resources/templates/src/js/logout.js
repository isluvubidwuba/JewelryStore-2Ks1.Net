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
        window.location.href = "Login.html";
      }
      if (
        $("#loginForm").length === 0 &&
        $("#adminPage").length !== 0 &&
        userRole === "STAFF"
      )
        window.location.href = "InvoiceDefault.html";
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
    window.location.href = "Login.html";
  }
}
authenticate();
function setupDashboardButton() {
  if (userRole === "ADMIN" || userRole === "MANAGER") {
    $("#changeScreen").show();
  }

  $("#changeScreen").on("click", function () {
    if (userRole === "ADMIN" || userRole === "MANAGER") {
      window.location.href = "DashboardAdmin.html";
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
    });

    $("#closeProfileModal").click(() => {
      $("#viewProfileModal").addClass("hidden");
      $("#changePASS").addClass("hidden");
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
    $("#updateProfileBtn").click(async () => {
      var formData = new FormData($("#viewProfileForm")[0]);
      var fileInput = $("#profileImgUpdate")[0];

      formData.append("file", fileInput.files[0]);

      // In ra console để kiểm tra FormData
      for (var pair of formData.entries()) {
        console.log(pair[0] + ": " + pair[1]);
      }

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
