import UserService from "./userService.js";

const userService = new UserService();

$(document).ready(function () {
  validPromotion;
  handleLogin;
  handleSendOtp;

  $("#loginButton").click(handleLogin);
  $("#forgetPassword").click(() => {
    toggleForms("#loginForm", "#forgetPasswordForm");
    $("#sendOtp").click(handleSendOtp);
  });
  $("#validateOTP").click(function () {
    var idEmploy = $("#resendButton").attr("data-idEm");
    var otp = $("#OtpCode").val();
    validateOtp(idEmploy, otp);
  });
  $("#changePassButton").click(function () {
    const checkPasswordMatch = () => {
      const password = $("#passswordChange").val();
      const confirmPassword = $("#passswordChangeConfirm").val();

      if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return false;
      }
      return true;
    };

    if (checkPasswordMatch) {
      changePasss($("#passswordChange").val());
    }
  });
  $("[name='backToLoginButton']").click(function () {
    toggleForms("#forgetPasswordForm, #otpForm, #changePassForm", "#loginForm");
  });
});
const handleLogin = () => {
  const id = $("#name").val();
  const pinCode = $("#pincode").val();
  userService.sendAjax(
    `http://${userService.getApiUrl()}/api/authentication/signup`,
    "POST",
    handleLoginSuccess,
    handleLoginError,
    { id, pinCode }
  );
};
function handleLoginError(xhr) {
  showNotification(
    "Login failed: " + (xhr.responseJSON?.desc || "Unknown error"),
    "error"
  );
}
function handleLoginSuccess(response) {
  const { status, data, desc } = response;
  if (status === "OK" && data) {
    const { at } = data;

    if (at) {
      const { sub, role } = userService.parseJwt(at);
      localStorage.setItem("token", at);
      localStorage.setItem("userId", sub);
      localStorage.setItem("userRole", role);

      const redirectUrl =
        role === "ADMIN" || role === "MANAGER"
          ? "DashboardAdmin.html"
          : role === "STAFF"
          ? "StaffScreen.html"
          : null;

      if (redirectUrl) {
        window.location.href = redirectUrl;
      } else {
        alert("Role Not Available!");
      }
    } else {
      alert("Error Data From Server!");
    }
  } else {
    alert(desc);
  }
}
const handleSendOtp = () => {
  const idEmploy = $("#idEmployyee").val();
  if (!idEmploy) return;
  $("#resendButton").attr("data-idEm", idEmploy);
  sendOtp(idEmploy);
  toggleForms("#forgetPasswordForm", "#otpForm");
  countDownResend();
};
const toggleForms = (hideSelector, showSelector) => {
  $(hideSelector).addClass("hidden");
  $(showSelector).removeClass("hidden");
};

const countDownResend = () => {
  let countdown = 59;
  const countdownElement = $("#countdown");
  const resendButton = $("#resendButton");

  const interval = setInterval(() => {
    if (countdown > 0) {
      countdown--;
      countdownElement.text(
        `Request resend OTP in 00:${
          countdown < 10 ? "0" + countdown : countdown
        }`
      );
    } else {
      clearInterval(interval);
      countdownElement.addClass("hidden");
      resendButton.removeClass("hidden");
    }
  }, 1000);

  resendButton.click(() => {
    countdown = 59;
    resendButton.addClass("hidden");
    countdownElement
      .removeClass("hidden")
      .text(`Request resend OTP in 00:${countdown}`);
    sendOtp(resendButton.attr("data-idEm"));
    setInterval(interval);
  });
};

const sendOtp = (idEmploy) => {
  userService.sendAjax(
    `http://${userService.getApiUrl()}/mail/sendOtp/${idEmploy}`,
    "POST",
    ({ status, desc }) => {
      if (status !== "OK") {
        showNotification(desc, "error");
      }
    },
    (error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else {
        showNotification("Error while send OTP to employee email!", "error");
      }
    },
    { id, pinCode }
  );
};
const validateOtp = (idEmploy, otp) => {
  userService.sendAjax(
    `http://${userService.getApiUrl()}/employee/validateOtp`,
    "POST",
    ({ status, desc, data }) => {
      token = data;
      toggleForms("#otpForm", "#changePassForm");
      if (status !== "OK") {
        showNotification(desc, "error");
      }
    },
    (error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else {
        showNotification("Error validate OTP!", "error");
      }
    },
    { otp: otp, idEmployee: idEmploy }
  );
};

const changePasss = (password) => {
  userService.sendAjax(
    `http://${userService.getApiUrl()}/employee/changePass`,
    "POST",
    ({ status, desc, data }) => {
      token = null;
      $("#resendButton").attr("data-idEm", "");
      if (status == "OK") {
        showNotification("Change password successfully", "OK");
        toggleForms("#changePassForm", "#loginForm");
      }
      if (status !== "OK") {
        showNotification(desc, "Error");
      }
    },
    (error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc);
      } else {
        showNotification("Error while change password!");
      }
    },
    {
      pwd: password,
      token: token,
      idEmploy: $("#resendButton").attr("data-idEm"),
    }
  );
};

const validPromotion = () => {
  userService.sendAjax(
    `http://${userService.getApiUrl()}/promotion/valid`,
    "GET",
    null,
    (error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc);
      } else {
        showNotification("Error valid promo");
      }
    },
    null
  );
};
