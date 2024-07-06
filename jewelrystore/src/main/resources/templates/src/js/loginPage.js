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
  const pincode = $("#pincode").val();

  $.ajax({
    url: `http://${apiurl}/authentication/signup`,
    type: "POST",
    data: { id: id, pinCode: pincode },
    success: ({ status, data, desc }) => {
      if (status === "OK" && data) {
        const { at } = data;

        if (at) {
          const { sub, role } = parseJwt(at);
          localStorage.setItem("userRole", at);
          localStorage.setItem("userId", sub);
          localStorage.setItem("role", role);

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
    },
    error: (error) => {
      console.error("Error:", error);
    },
  });
};
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
  $.ajax({
    url: `http://${apiurl}/mail/sendOtp/${idEmploy}`,
    type: "POST",
    contentType: "application/json",
    success: ({ status, desc }) => {
      if (status !== "OK") {
        alert(desc);
      }
    },
    error: (error) => {
      if (error.responseJSON) {
        alert(error.responseJSON.desc);
      } else {
        console.error("Error while send OTP to employee email : ", error);
        alert("Error while send OTP to employee email!");
      }
    },
  });
};
const validateOtp = (idEmploy, otp) => {
  $.ajax({
    url: `http://${apiurl}/employee/validateOtp`,
    type: "POST",
    data: { otp: otp, idEmployee: idEmploy },
    success: ({ status, desc, data }) => {
      _token = data;
      toggleForms("#otpForm", "#changePassForm");
      if (status !== "OK") {
        alert(desc);
      }
    },
    error: (error) => {
      if (error.responseJSON) {
        alert(error.responseJSON.desc);
      } else {
        console.error("Error while validate OTP : ", error);
        alert("Error validate OTP!");
      }
    },
  });
};

const changePasss = (password) => {
  $.ajax({
    url: `http://${apiurl}/employee/changePass`,
    type: "POST",
    data: {
      pwd: password,
      token: _token,
      idEmploy: $("#resendButton").attr("data-idEm"),
    },
    success: ({ status, desc, data }) => {
      _token = null;
      $("#resendButton").attr("data-idEm", "");
      if (status == "OK") {
        alert("Change password successfully");
        toggleForms("#changePassForm", "#loginForm");
      }
      if (status !== "OK") {
        alert(desc);
      }
    },
    error: (error) => {
      if (error.responseJSON) {
        alert(error.responseJSON.desc);
      } else {
        console.error("Error while change password : ", error);
        alert("Error while change password!");
      }
    },
  });
};

const validPromotion = () => {
  $.ajax({
    url: `http://${apiurl}/promotion/valid`,
    type: "GET",
    contentType: "application/json",
    error: (error) => {
      if (error.responseJSON) {
        alert(error.responseJSON.desc);
      } else {
        console.error("Error valid promo ", error);
      }
    },
  });
};
