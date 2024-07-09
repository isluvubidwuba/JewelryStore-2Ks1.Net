import UserService from "./userService.js";
let fileInput = document.getElementById("employeeImageInput"); // Khai báo biến fileInput ở phạm vi toàn cục
const userService = new UserService();

$(document).ready(function () {
  // Xử lý sự kiện khi click vào vùng hình ảnh
  document
    .getElementById("employeeImage")
    .addEventListener("click", function () {
      document.getElementById("employeeImageInput").click();
    });

  // Xử lý sự kiện khi thay đổi hình ảnh
  document
    .getElementById("employeeImageInput")
    .addEventListener("change", function (event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
          document.querySelector("#employeeImage img").src = e.target.result;
        };
        reader.readAsDataURL(file);
        document.querySelector(".edit-buttons").style.display = "flex";
      }
    });
});

function editField(fieldId) {
  const fieldElement = document.getElementById(fieldId);
  const fieldValue = fieldElement.querySelector(".value").textContent;
  const inputField = document.getElementById(fieldId + "Input");
  inputField.value = fieldValue;

  fieldElement.style.display = "none";
  inputField.style.display = "block";
  document.querySelector(".edit-buttons").style.display = "flex";
}

function saveChanges() {
  const fileInput = document.getElementById("employeeImageInput");
  const formData = new FormData();

  const fullName = document.getElementById("employeeNameInput").value.trim();
  const firstSpaceIndex = fullName.indexOf(" ");
  let firstName, lastName;

  if (firstSpaceIndex === -1) {
    // Không tìm thấy khoảng trắng, giả sử fullName chỉ có first name
    firstName = fullName;
    lastName = "";
  } else {
    firstName = fullName.substring(0, firstSpaceIndex);
    lastName = fullName.substring(firstSpaceIndex + 1);
  }

  formData.append("id", localStorage.getItem("userId"));
  formData.append("firstName", firstName);
  formData.append("lastName", lastName);
  formData.append(
    "phoneNumber",
    document.getElementById("employeePhoneNumberInput").value
  );
  formData.append("email", document.getElementById("employeeEmailInput").value);
  formData.append(
    "address",
    document.getElementById("employeeAddressInput").value
  );

  if (fileInput.files[0]) {
    formData.append("file", fileInput.files[0]);
  }
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/employee/update2`,
    "POST",
    handleSuccess,
    handleError,
    formData
  );
}

function cancelChanges() {
  document.querySelectorAll(".edit-mode").forEach((element) => {
    element.style.display = "none";
  });
  document.querySelectorAll(".value").forEach((element) => {
    element.parentElement.style.display = "block";
  });
  document.querySelector(".edit-buttons").style.display = "none";
}

function handleSuccess(data) {
  if (data.status === "OK") {
    document.querySelector(
      "#employeeName .value"
    ).textContent = `${data.data.firstName} ${data.data.lastName}`;
    document.querySelector("#employeePhoneNumber .value").textContent =
      data.data.phoneNumber;
    document.querySelector("#employeeEmail .value").textContent =
      data.data.email;
    document.querySelector("#employeeAddress .value").textContent =
      data.data.address;

    if (fileInput.files[0]) {
      document.querySelector("#employeeImage img").src = URL.createObjectURL(
        fileInput.files[0]
      );
    }
    showNotification(data.desc, "OK");
    cancelChanges();
  } else {
    showNotification("Update failed: " + data.desc, "Error");
  }
}

function handleError() {
  showNotification("Error when call API.", "Error");
}
// Gán hàm vào đối tượng window để có thể truy cập từ HTML
window.editField = editField;
window.saveChanges = saveChanges;
window.cancelChanges = cancelChanges;
