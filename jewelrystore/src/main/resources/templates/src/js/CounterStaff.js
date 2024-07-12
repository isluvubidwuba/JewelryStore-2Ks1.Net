import UserService from "./userService.js";

const userService = new UserService();

$(document).ready(function () {
  fetchCounters();
});

const token = localStorage.getItem("token");

function fetchCounters() {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/counter/allactivecounter`,
      "GET",
      null
    )
    .then((response) => {
      if (response.status === "OK") {
        const counters = response.data;
        generateTabs(counters);
        generateTabContents(counters);

        if (counters.length > 0) {
          const firstCounterId = counters[1].id;
          switchToTab(firstCounterId);
          fetchProductsByCounter(firstCounterId);
        }
      }
    })
    .catch((error) => {
      console.error("Error fetching counters:", error);
    });
}

function generateTabs(counters) {
  const tabsContainer = $("#counter-tabs").empty();
  counters.forEach((counter, index) => {
    if (counter.id === 1) return; // Bỏ qua quầy có id là 1

    const tab = $("<li>", { class: "mr-2 flex items-center" });

    const tabLinkContainer = $("<div>", {
      class: "flex items-center rounded-lg bg-black",
    });

    const tabLink = $("<a>", {
      href: "#",
      class: `inline-block py-3 px-4 rounded-lg ${
        index === 0
          ? "text-white bg-black active"
          : "text-gray-300 bg-black hover:bg-gray-700"
      }`,
      text: counter.name,
      "data-tab": `tab-${counter.id}`,
    });

    tabLinkContainer.append(tabLink);
    tab.append(tabLinkContainer);
    tabsContainer.append(tab);

    tabLink.on("click", function (e) {
      e.preventDefault();
      $("#counter-tabs a").removeClass("text-white bg-white active");
      $("#counter-tabs a").addClass("text-gray-300 bg-black hover:bg-gray-700");
      $("#tab-contents > div").addClass("hidden");

      tabLink.removeClass("text-gray-300 bg-gray-600 hover:bg-gray-700");
      tabLink.addClass("text-white bg-gray-900 active");
      $(`#${tabLink.data("tab")}`).removeClass("hidden");

      // Load products for the selected counter
      fetchProductsByCounter(counter.id);
    });
  });
}

function generateTabContents(counters) {
  const contentsContainer = $("#tab-contents").empty();
  counters.forEach((counter, index) => {
    if (counter.id === 1) return; // Bỏ qua nội dung của quầy có id là 1

    const content = $("<div>", {
      id: `tab-${counter.id}`,
      class: `${index !== 0 ? "hidden" : ""}`,
    });

    // Thêm bảng hiển thị danh sách sản phẩm
    const table = $("<table>", {
      class: "min-w-full divide-y divide-gray-200",
    }).append(
      $("<thead>", { class: "bg-gray-200" }).append(
        $("<tr>").append(
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Product Code",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Barcode",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Name",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Fee",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Weight",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Material",
          }),
          $("<th>", {
            class:
              "px-6 py-3 text-left text-xs font-lagre text-gray-500 uppercase tracking-wider",
            text: "Category",
          })
        )
      ),
      $("<tbody>", {
        id: `table-body-${counter.id}`,
        class: "bg-white divide-y divide-gray-200",
      })
    );

    // Thêm phần điều hướng trang
    const pagination = $("<div>", {
      class: "flex justify-between items-center mt-4",
    }).append(
      $("<button>", {
        class:
          "prev-page px-4 py-2 bg-black hover:bg-gray-700 text-white rounded",
        text: "Previous",
        "data-counter-id": counter.id,
      }),
      $("<span>", { id: `page-info-${counter.id}`, text: "Page 1" }),
      $("<button>", {
        class:
          "next-page px-4 py-2 bg-black hover:bg-gray-700 text-white rounded",
        text: "Next",
        "data-counter-id": counter.id,
      })
    );

    content.append(table).append(pagination);
    contentsContainer.append(content);
  });

  // Sự kiện click cho các nút điều hướng
  $(".prev-page").on("click", function () {
    const counterId = $(this).data("counter-id");
    const currentPage = parseInt(
      $(`#page-info-${counterId}`).text().split(" ")[1]
    );
    if (currentPage > 1) {
      fetchProductsByCounter(counterId, currentPage - 1);
    }
  });

  $(".next-page").on("click", function () {
    const counterId = $(this).data("counter-id");
    const currentPage = parseInt(
      $(`#page-info-${counterId}`).text().split(" ")[1]
    );
    fetchProductsByCounter(counterId, currentPage + 1);
  });
}

function fetchProductsByCounter(counterId, page = 1) {
  userService
    .sendAjaxWithAuthen(
      `http://${userService.getApiUrl()}/api/counter/listproductsbycounter?counterId=${counterId}&page=${
        page - 1
      }`,
      "GET",
      null
    )
    .then((response) => {
      const products = response.products;
      const tableBody = $(`#table-body-${counterId}`);
      tableBody.empty();
      products.forEach((product) => {
        const row = $("<tr>").append(
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.productCode,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.barCode,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.name,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.fee,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.weight,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.materialDTO.name,
          }),
          $("<td>", {
            class: "px-6 py-4 whitespace-nowrap",
            text: product.productCategoryDTO.name,
          })
        );
        tableBody.append(row);
      });

      // Cập nhật thông tin trang
      const totalPages = response.totalPages;
      const currentPage = response.currentPage + 1;
      $(`#page-info-${counterId}`).text(`Page ${currentPage} of ${totalPages}`);

      // Vô hiệu hóa các nút khi cần thiết
      if (currentPage === 1) {
        $(`button.prev-page[data-counter-id=${counterId}]`)
          .prop("disabled", true)
          .addClass("opacity-50 cursor-not-allowed");
      } else {
        $(`button.prev-page[data-counter-id=${counterId}]`)
          .prop("disabled", false)
          .removeClass("opacity-50 cursor-not-allowed");
      }
      if (currentPage === totalPages) {
        $(`button.next-page[data-counter-id=${counterId}]`)
          .prop("disabled", true)
          .addClass("opacity-50 cursor-not-allowed");
      } else {
        $(`button.next-page[data-counter-id=${counterId}]`)
          .prop("disabled", false)
          .removeClass("opacity-50 cursor-not-allowed");
      }
    })
    .catch((error) => {
      console.error("Error fetching products:", error);
    });
}

function switchToTab(counterId) {
  $(`#counter-tabs a[data-tab="tab-${counterId}"]`).trigger("click");
}
