$(document).ready(function () {
    loadProducts();
});
const token = localStorage.getItem("token");

function loadProducts() {
    $.ajax({
        url: "http://localhost:8080/product/all",
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            if (response && response.data) {
                const products = response.data.content; // assuming the data is in response.data.content
                products.forEach(product => {
                    $('#product-grid').append(`
                        <div class="max-w-sm rounded overflow-hidden shadow-lg">
                            <img class="w-full" src="data:image/jpeg;base64,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" alt="${product.name}">
                            <div class="px-6 py-4">
                                <div class="font-bold text-xl mb-2">${product.name}</div>
                                <p class="text-gray-700 text-base">
                                    Code: ${product.productCode}
                                </p>
                                <p class="text-base">
                                    Fee: ${product.fee}
                                </p>
                            </div>
                            <div class="px-6 pt-4 pb-2">
                                <span class="inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2">#${product.materialDTO.name}</span>
                                <span class="inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2">#${product.productCategoryDTO.name}</span>
                                <span class="inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2">#${product.counterDTO.name}</span>
                            </div>
                        </div>
                    `);
                });
            }
        },
        error: function (error) {
            console.log("Error loading products:", error);
        }
    });
}

