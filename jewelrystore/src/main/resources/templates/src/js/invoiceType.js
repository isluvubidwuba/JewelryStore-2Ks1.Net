import UserService from "./userService.js";

const userService = new UserService();
$(document).ready(function () {
  // Sự kiện click cho nút mở modal
  $("#openManagerInvoiceType").click(function () {
    openInvoiceTypeModal();
  });

  // Sự kiện click cho nút đóng modal
  $("#closeModalRate, #closeCustomerTypeModal").click(function () {
    $("#invoiceTypeModal").addClass("hidden");
  });
});

// Hàm để mở modal và load dữ liệu
function openInvoiceTypeModal() {
  userService.sendAjaxWithAuthen(
    `http://${userService.getApiUrl()}/api/invoice-type`,
    "GET",
    function (response) {
      if (response.status === "OK") {
        var data = response.data;
        var tableBody = $("#rateTable");
        tableBody.empty(); // Xóa dữ liệu cũ trong bảng

        // Thêm dữ liệu mới vào bảng
        data.forEach(function (item) {
          var row =
            "<tr>" +
            '<td class="px-4 py-2 border">' +
            item.name +
            "</td>" +
            '<td class="px-4 py-2 border">' +
            '<input type="number" step="0.1" min="0.1" max="2" class="rate-input px-2 py-1 w-20" value="' +
            item.rate +
            '" readonly data-id="' +
            item.id +
            '"/>' +
            "</td>" +
            '<td class="px-4 py-2 border edit-cell" data-id="' +
            item.id +
            '"></td>' +
            "</tr>";
          tableBody.append(row);
        });

        // Sự kiện click để chỉnh sửa rate
        $(".rate-input").click(function () {
          var rateInput = $(this);
          var id = rateInput.data("id");

          // Hủy các thay đổi trước đó
          $(".rate-input").each(function () {
            $(this).attr("readonly", true);
            $(this).removeClass("border");
            var cancelId = $(this).data("id");
            $(".edit-cell[data-id='" + cancelId + "']").html("");
          });

          // Kích hoạt input hiện tại
          rateInput.removeAttr("readonly");
          rateInput.addClass("border");
          var editCell = $(".edit-cell[data-id='" + id + "']");
          editCell.html(
            '<div class="flex space-x-2 justify-center">' +
              '<button class="cancel-btn px-2 py-1 bg-red-500 text-white rounded text-sm">Cancel</button>' +
              '<button class="update-btn px-2 py-1 bg-green-500 text-white rounded text-sm">Update</button>' +
              "</div>"
          );

          // Sự kiện click để hủy chỉnh sửa
          $(".cancel-btn").click(function () {
            rateInput.attr("readonly", true);
            rateInput.removeClass("border");
            editCell.html("");
          });

          // Sự kiện click để cập nhật rate
          $(".update-btn").click(function () {
            var newRate = rateInput.val();
            rateInput.removeClass("border");
            userService.sendAjaxWithAuthen(
              `http://${userService.getApiUrl()}/api/invoice-type/update`,
              "GET",
              function (response) {
                if (response.status === "OK") {
                  rateInput.val(newRate).attr("readonly", true);
                  editCell.html("");
                  showNotification(response.desc, "OK");
                } else {
                  showNotification("Update fail", "Error");
                }
              },
              function () {
                showNotification("Update fail", "Error");
              },
              $.param({
                id: id,
                rate: newRate,
              })
            );
          });
        });
      }
    },
    function () {
      showNotification("Load fail", "Error");
    },
    null
  );

  // Hiển thị modal
  $("#invoiceTypeModal").removeClass("hidden");
}
