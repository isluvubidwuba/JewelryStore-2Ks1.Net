const token = localStorage.getItem("token");
let currentPage = 0;
const itemsPerPage = 5;
let promotions = [];

function fetchPolicies() {
  $.ajax({
    url:
      "http://localhost:8080/promotion/viewPolicyByInvoiceType/" + invoicetype,
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    success: function (response) {
      if (response.status === "OK") {
        promotions = response.data;
        renderPromotions(currentPage, promotions);
        updatePagination(promotions);
      } else {
        alert("Failed to fetch data");
      }
    },
    error: function (xhr, status, error) {
      alert("Error fetching data");
    },
  });
}

function searchPolicies(keyword) {
  const filteredPolicies = promotions.filter((policy) => {
    return (
      policy.name.toLowerCase().includes(keyword.toLowerCase()) ||
      policy.promotionType.toLowerCase().includes(keyword.toLowerCase()) ||
      policy.invoiceTypeDTO.name
        .toLowerCase()
        .includes(keyword.toLowerCase()) ||
      (policy.status ? "active" : "inactive")
        .toLowerCase()
        .includes(keyword.toLowerCase())
    );
  });
  renderPromotions(0, filteredPolicies);
  updatePagination(filteredPolicies);
}

function renderPromotions(page, policies) {
  $("#table-body").empty();
  const start = page * itemsPerPage;
  const end = start + itemsPerPage;
  const promotionsToRender = policies.slice(start, end);

  promotionsToRender.forEach((data, index) => {
    let applicableButton = "";
    switch (data.promotionType) {
      case "category":
      case "material":
      case "customer":
        applicableButton = `<button
          type="button"
          class="bg-white hover:bg-gray-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
          data-promotion-id="${data.id}"
          data-promotion-name="${data.name}"
          data-promotion-type="${data.promotionType.toUpperCase()}"
          style="width: 100%"
        >
          View applied
        </button>`;
        break;
      case "product":
        applicableButton = `<button
          type="button"
          class="bg-white hover:bg-gray-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow"
          data-promotion-id="${data.id}"
          data-promotion-name="${data.name}"
          data-promotion-type="${data.promotionType.toUpperCase()}"
          style="width: 100%"
        >
          View applied
        </button>`;
        break;
    }

    $("#table-body").append(`
      <tr class="${index % 2 === 0 ? "bg-gray-100" : "bg-white"}" data-id="${
      data.id
    }">
        <td class="text-left py-3 px-4">${data.name}</td>
        <td class="text-center py-3 px-4">${data.value}</td>
        <td class="text-center py-3 px-4">${
          data.status ? "Active" : "Inactive"
        }</td>
        <td class="text-center py-3 px-4">${new Date(
          data.startDate
        ).toLocaleDateString()}</td>
        <td class="text-center py-3 px-4">${new Date(
          data.endDate
        ).toLocaleDateString()}</td>
        <td class="text-center py-3 px-4">${new Date(
          data.lastModified
        ).toLocaleDateString()}</td>
        <td class="text-center py-3 px-4">${data.invoiceTypeDTO.name}</td>
        <td class="text-center py-3 px-4">${data.promotionType}</td>
        <td class="text-center py-3 px-4">${applicableButton}</td>
      </tr>
    `);
  });

  $("#entries-info").text(
    `Showing ${start + 1} to ${Math.min(end, policies.length)} of ${
      policies.length
    } entries`
  );

  attachModalHandlers();
}

function updatePagination(promotionsToRender) {
  let pagination = $(".pagination");
  pagination.empty();

  const totalPages = Math.ceil(promotionsToRender.length / itemsPerPage);

  if (currentPage > 0) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          aria-label="backward"
          role="button"
          class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 p-1 flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 focus:outline-none mr-1 sm:mr-3"
          onclick="changePage(${currentPage - 1})"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z"/>
            <polyline points="15 6 9 12 15 18" />
          </svg>
        </span>
      </li>
    `);
  }

  let startPage = Math.max(0, currentPage - 1);
  let endPage = Math.min(totalPages, currentPage + 2);

  if (currentPage === 0) {
    endPage = Math.min(totalPages, currentPage + 2);
  } else if (currentPage === totalPages - 1) {
    startPage = Math.max(0, totalPages - 3);
  } else {
    startPage = Math.max(0, currentPage - 1);
    endPage = Math.min(totalPages, currentPage + 2);
  }

  for (let i = startPage; i < endPage; i++) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          class="focus:outline-none focus:bg-indigo-700 focus:text-white flex text-indigo-700 ${
            i === currentPage
              ? "bg-gray-400 text-white"
              : "bg-white hover:bg-indigo-600 hover:text-white"
          } text-base leading-tight font-bold cursor-pointer shadow transition duration-150 ease-in-out mx-2 sm:mx-4 rounded px-3 py-2"
          onclick="changePage(${i})"
        >
          ${i + 1}
        </span>
      </li>
    `);
  }

  if (currentPage < totalPages - 1) {
    pagination.append(`
      <li>
        <span
          tabindex="0"
          aria-label="forward"
          role="button"
          class="focus:ring-2 focus:ring-offset-2 focus:ring-indigo-700 focus:outline-none flex rounded transition duration-150 ease-in-out text-base leading-tight font-bold text-gray-500 hover:text-indigo-700 p-1 focus:outline-none ml-1 sm:ml-3"
          onclick="changePage(${currentPage + 1})"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z"/>
            <polyline points="9 6 15 12 9 18" />
          </svg>
        </span>
      </li>
    `);
  }
}

function changePage(page) {
  currentPage = page;
  const keyword = $("#keyword").val().toLowerCase();
  const promotionsToRender = keyword
    ? promotions.filter((promotion) => {
        const invoiceTypeName = promotion.invoiceTypeDTO
          ? promotion.invoiceTypeDTO.name
          : "";
        return (
          promotion.name.toLowerCase().includes(keyword) ||
          promotion.startDate.toLowerCase().includes(keyword) ||
          promotion.endDate.toLowerCase().includes(keyword) ||
          promotion.promotionType.toLowerCase().includes(keyword) ||
          invoiceTypeName.toLowerCase().includes(keyword)
        );
      })
    : promotions;
  renderPromotions(page, promotionsToRender);
  updatePagination(promotionsToRender);
}

function attachModalHandlers() {
  $("button[data-promotion-type]").click(function () {
    const promotionId = $(this).data("promotion-id");
    const promotionType = $(this).data("promotion-type");
    const promotionName = $(this).data("promotion-name");
    $("#ListApply").text(
      `List ${promotionType.toLowerCase()} apply: ${promotionName}`
    );
    $.ajax({
      url: `http://localhost:8080/promotion-generic/in-promotion/${promotionType}/${promotionId}`,
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      success: function (response) {
        if (response.status === "OK") {
          $("#category-apply-promotion").empty();
          response.data.forEach((item, index) => {
            let name;
            if (promotionType.toLowerCase() === "customer") {
              name = item.customerTypeDTO.type;
            } else {
              name = item[`${promotionType.toLowerCase()}DTO`].name;
            }
            $("#category-apply-promotion").append(`
              <tr>
                <td class="px-6 py-3">${index + 1}</td>
                <td class="px-6 py-3">${name}</td>
              </tr>
            `);
          });
          $("#detail-modal_CategoryApply").removeClass("hidden");
        } else {
          alert("Failed to fetch applied data");
        }
      },
      error: function (xhr, status, error) {
        alert("Error fetching applied data");
      },
    });
  });

  $("#modalClose_CategoryApply").click(function () {
    $("#detail-modal_CategoryApply").addClass("hidden");
  });
}

$(document).ready(function () {
  fetchPolicies();
  $("#exchange-search-button").click(function () {
    const keyword = $("#keyword").val();
    searchPolicies(keyword);
  });
});
