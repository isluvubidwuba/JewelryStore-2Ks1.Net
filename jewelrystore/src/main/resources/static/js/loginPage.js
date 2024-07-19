import UserService from "./userService.js";

const userService = new UserService();

$(document).ready(function () {
  $("#loginButton").click(() => handleLogin());
  $("#forgetPassword").click(() => {
    toggleForms("#loginForm", "#forgetPasswordForm");
    $("#sendOtp")
      .off("click")
      .on("click", () => {
        handleSendOtp();
      }); // Prevent multiple event attachments
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
        showNotification("Passwords do not match!");
        return false;
      }
      return true;
    };

    if (checkPasswordMatch()) {
      changePass($("#passswordChange").val()); // Fixed function name
    }
  });
  $("[name='backToLoginButton']").click(function () {
    toggleForms("#forgetPasswordForm, #otpForm, #changePassForm", "#loginForm");
  });
});

async function handleLogin() {
  const id = $("#name").val();
  const pinCode = $("#pincode").val();
  try {
    const response = await userService.sendAjax(
      `http://${userService.getApiUrl()}/api/authentication/signup`,
      "POST",
      { id: id, pinCode: pinCode }
    );
    const { status, data, desc } = response;
    if (status === "OK" && data) {
      const { at } = data;
      if (at) {
        const { sub, role } = userService.parseJwt(at);
        userService.setToken(at);
        userService.setUserId(sub);
        userService.setUserRole(role);
        await validPromotion();
        let role_1 = userService.getUserRole();
        const redirectUrl =
          role_1 === "ADMIN" || role_1 === "MANAGER"
            ? "/dashboard"
            : role_1 === "STAFF"
            ? "/home"
            : null;

        if (redirectUrl)
          window.location.href =
            `http://${userService.getApiUrl()}` + redirectUrl;
      }
    }
  } catch (response_2) {
    showNotification(
      "Login failed: " + (response_2.responseJSON?.desc || "Unknown error"),
      "error"
    );
  }
}

async function validPromotion() {
  try {
    return await userService.sendAjax(
      `http://${userService.getApiUrl()}/api/promotion/valid`,
      "GET",
      null
    );
  } catch (error) {
    if (error.responseJSON) {
      showNotification(error.responseJSON.desc, "error");
    } else {
      showNotification("Error validating promotion!", "error");
    }
  }
}
async function handleSendOtp() {
  const idEmploy = $("#idEmployyee").val();
  if (!idEmploy) return;

  // Show the loader
  $("#loader-overlay").removeClass("hidden");

  $("#resendButton").attr("data-idEm", idEmploy);
  var check = await sendOtp(idEmploy);
  $("#loader-overlay").addClass("hidden");

  if (check) {
    toggleForms("#forgetPasswordForm", "#otpForm");
    countDownResend();
  }
}

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
    clearInterval(interval); // Clear the previous interval before starting a new one
    setInterval(() => {
      // Start a new interval
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
  });
};

function sendOtp(idEmploy) {
  return userService
    .sendAjax(
      `http://${userService.getApiUrl()}/api/authentication/sendOtp/${idEmploy}`,
      "POST"
    )
    .then(() => true)
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else {
        showNotification("Error while sending OTP to employee email!", "error");
      }
      return false;
    });
}

const validateOtp = (idEmploy, otp) => {
  userService
    .sendAjax(
      `http://${userService.getApiUrl()}/api/authentication/validateOtp`,
      "POST",
      $.param({ otp: otp, idEmployee: idEmploy })
    )
    .then(({ status, desc, data }) => {
      if (data.at) userService.setToken(data.at);
      toggleForms("#otpForm", "#changePassForm");
      if (status !== "OK") {
        showNotification(desc, "error");
      }
    })
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else {
        showNotification("Error validating OTP!", "error");
      }
    });
};

const changePass = (password) => {
  // Fixed function name
  userService
    .sendAjax(
      `http://${userService.getApiUrl()}/api/employee/changePass`,
      "POST",
      $.param({
        pwd: password,
      })
    )
    .then(({ status, desc, data }) => {
      userService.setToken(null);
      $("#resendButton").attr("data-idEm", "");
      if (status == "OK") {
        showNotification("Password changed successfully", "OK");
        toggleForms("#changePassForm", "#loginForm");
      } else {
        showNotification(desc, "error");
      }
    })
    .catch((error) => {
      if (error.responseJSON) {
        showNotification(error.responseJSON.desc, "error");
      } else {
        showNotification("Error while changing password!", "error");
      }
    });
};
