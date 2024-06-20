$(document).ready(function () {
  const handleLogin = () => {
    const id = $("#name").val();
    const pincode = $("#pincode").val();

    $.ajax({
      url: "http://localhost:8080/authentication/signup",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({ id, pinCode: pincode }),
      success: ({ status, data, desc }) => {
        if (status === "OK" && data) {
          const { token, id, role } = data;

          if (token && id && role) {
            localStorage.setItem("token", token);
            localStorage.setItem("userId", id);
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
      },
      error: (error) => {
        console.error("Error:", error);
      },
    });
  };
  var _token = null;
  const handleSendOtp = () => {
    const idEmploy = $("#idEmployyee").val();
    if (!idEmploy) return;
    $("#resendButton").attr("data-idEm", idEmploy);
    sendOtp(idEmploy);
    toggleForms("#forgetPasswordForm", "#otpForm");
    countDownResend();
  };

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
    url: `http://localhost:8080/mail/sendOtp/${idEmploy}`,
    type: "POST",
    contentType: "application/json",
    success: ({ status, desc }) => {
      if (status !== "OK") {
        alert(desc);
      }
    },
    error: (error) => {
      console.error("Error:", error);
    },
  });
};
const validateOtp = (idEmploy, otp) => {
  $.ajax({
    url: `http://localhost:8080/employee/validateOtp`,
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
      console.error("Error:", error);
    },
  });
};

const changePasss = (password) => {
  $.ajax({
    url: `http://localhost:8080/employee/changePass`,
    type: "POST",
    data: {
      pwd: password,
      token: _token,
      idEmploy: $("#resendButton").attr("data-idEm"),
    },
    success: ({ status, desc, data }) => {
      _token = null;
      $("#resendButton").attr("data-idEm", "");
      if (status == "OK") toggleForms("#changePassForm", "#loginForm");
      if (status !== "OK") {
        alert(desc);
      }
    },
    error: (error) => {
      console.error("Error:", error);
    },
  });
};
