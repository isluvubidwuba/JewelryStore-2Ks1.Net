$(document).ready(function () {
  const token = localStorage.getItem("token");
  var selectedInvoiceType = "";

  // Toggle dropdown with animation
  $("#dropdown-button").click(function () {
    $("#dropdown").toggle(); // Toggle visibility of the dropdown
    // Force re-animation by triggering reflow
    $("#dropdown ul li").each(function () {
      var el = $(this);
      el.css("animation", "none");
      el.height(); // Trigger reflow
      el.css("animation", "");
    });
  });

  // Dropdown item click handling
  $(".invoice-option").click(function () {
    selectedInvoiceType = $(this).data("value"); // Save the selected invoice type
    var text = $(this).text();
    $("#dropdown-button")
      .text(text)
      .append(
        '<svg class="w-4 h-4 ml-2" aria-hidden="true" fill="none" viewBox="0 0 10 6"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 4 4 4-4"/></svg>'
      );
    $("#dropdown").slideUp(300); // Close the dropdown with animation
  });

  // Handle search button click
  $("#searchBtn").click(function () {
    var barcode = $("#barcode").val();
    console.log("Check invoice type passed back : " + selectedInvoiceType);
    if (barcode.trim() !== "" || selectedInvoiceType !== "") {
      $.ajax({
        url: `http://${apiurl}/invoice/create-detail`,
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
          barcode: barcode,
          quantity: 1,
          invoiceTypeId: selectedInvoiceType,
        }),
        headers: {
          Authorization: `Bearer ${token}`,
        },
        success: function (response) {
          console.log("Success:", response);
          displayProductDetails(response.data);
        },
        error: function (xhr, status, error) {
          console.error("Error:", error);
          alert("Failed to send data: " + error);
        },
      });
    } else {
      alert(
        "Please select an invoice type or enter a product barcode before searching."
      );
    }
  });

  // Function to display product details in the UI
  function displayProductDetails(data) {
    var detailsHtml = '<div class="flex flex-wrap -mx-2 fade-in">';
    detailsHtml += createDetailColumn(data, "first");
    detailsHtml += createDetailColumn(data, "second");
    detailsHtml += "</div>";
    $("#displayContainer").html(detailsHtml);
  }

  // Helper function to create HTML content for each column
  function createDetailColumn(data, column) {
    if (column === "first") {
      return `
                <div class="w-1/2 px-2 flex product-info fade-in">
                
                    <div class="w-4/12 p-2">
                        <img src="${
                          data.productDTO.imgPath
                        }" alt="Product Image" class="max-w-full h-auto rounded">
                        <div class="mt-2 space-y-1">
                            <span class="tag">Status: ${
                              data.productDTO.status ? "Active" : "Inactive"
                            }</span>
                            <span class="tag">Weight: ${
                              data.productDTO.weight
                            }g</span>
                            <span class="tag">Material: ${
                              data.productDTO.materialDTO.name
                            }</span>
                            <span class="tag">Purity: ${
                              data.productDTO.materialDTO.purity
                            }</span>
                            <span class="tag">Category: ${
                              data.productDTO.productCategoryDTO.name
                            }</span>
                        </div>
                    </div>
                    <div class="w-8/12 p-2">
                        <p class="text-lg font-semibold">Product Code: ${
                          data.productDTO.productCode
                        }</p>
                        <p class="text-lg font-semibold">Bar Code: ${
                          data.productDTO.barCode
                        }</p>
                        <p class="text-lg font-semibold">Name: ${
                          data.productDTO.name
                        }</p>
                        <p class="text-lg font-semibold">Fee: ${formatCurrency(
                          data.productDTO.fee
                        )}</p>
                        <p class="text-lg font-semibold">Price at Time: ${formatCurrency(
                          data.productDTO.materialDTO.priceAtTime
                        )}</p>
                        <p class="text-lg font-semibold">Counter: ${
                          data.productDTO.counterDTO.name
                        }</p>
                        <p class="text-lg font-semibold">Inventory Quantity: ${
                          data.productDTO.inventoryDTO.quantity
                        }</p>
                        <p class="text-lg font-semibold">Price: ${formatCurrency(
                          data.price
                        )}</p>
                        <p class="text-lg font-semibold">Total Price: ${formatCurrency(
                          data.totalPrice
                        )}</p>
                    </div>
                </div>
            `;
    } else {
      return `
                <div class="w-1/2 px-2 container-scroll fade-in">
                    ${formatPromotions(data.listPromotion)}
                </div>
            `;
    }
  }

  function formatPromotions(promotions) {
    if (!promotions.length) return "<p>None</p>";
    return promotions
      .map(function (promo) {
        return `
                <div class="flex mb-4 promotion-item fade-in">
                    <div class="w-4/12 p-2">
                        <img src="${promo.image}" alt="Promotion Image" class="max-w-full h-auto rounded">
                    </div>
                    <div class="w-8/12 p-2">
                        <p class="text-lg font-semibold">${promo.name} (${promo.value}% off)</p>
                        <p>Valid from ${promo.startDate} to ${promo.endDate}</p>
                    </div>
                </div>
            `;
      })
      .join("");
  }

  function formatCurrency(amount) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  }
});
