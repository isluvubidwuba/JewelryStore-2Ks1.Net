let selectedProducts = [];
let totalPrice = 0;
const token = localStorage.getItem("token");

function updateSelectedProductsTable() {
    var tableBody = $('#selected-products');
    tableBody.empty();
    selectedProducts.forEach(product => {
        var row = `
      <tr>
        <td class="border px-4 py-2">${product.invoiceId}</td>
        <td class="border px-4 py-2">${product.name}</td>
        <td class="border px-4 py-2">${product.productCode}</td>
        <td class="border px-4 py-2">${product.totalPrice}</td>
        <td class="border px-4 py-2">
          <button class="bg-gray-200 text-gray-700 px-2" onclick="decreaseQuantity('${product.barCode}', '${product.invoiceId}')">-</button>
          <span>${product.quantity}</span>
          <button class="bg-gray-200 text-gray-700 px-2" onclick="increaseQuantity('${product.barCode}', '${product.invoiceId}')">+</button>
        </td>
        <td class="border px-4 py-2"><button class="bg-red-500 text-white p-2" onclick="removeProduct('${product.barCode}', '${product.invoiceId}')">Remove</button></td>
      </tr>
    `;
        tableBody.append(row);
    });
    $('#total-price').text(totalPrice);
}

function addProduct(product, invoiceId) {
    const existingProduct = selectedProducts.find(p => p.barCode === product.barCode && p.invoiceId === invoiceId);
    if (existingProduct) {
        increaseQuantity(product.barCode, invoiceId);
    } else {
        var requestData = {
            "barcode": product.barCode,
            "invoiceTypeId": 3,
            "quantity": 1
        };

        $.ajax({
            url: 'http://localhost:8080/invoice/create-detail',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            headers: {
                Authorization: `Bearer ${token}`,
            },
            success: function (response) {
                if (response.status === 'OK') {
                    var createdProduct = response.data.productDTO;
                    createdProduct.invoiceId = invoiceId;
                    createdProduct.totalPrice = response.data.totalPrice;
                    createdProduct.quantity = response.data.quantity;

                    selectedProducts.push(createdProduct);
                    totalPrice += createdProduct.totalPrice;
                    updateSelectedProductsTable();
                } else {
                    alert('Failed to create invoice detail');
                }
            },
            error: function (error) {
                console.error('Error:', error);
            }
        });
    }
}

function removeProduct(barCode, invoiceId) {
    const index = selectedProducts.findIndex(product => product.barCode === barCode && product.invoiceId === invoiceId);
    if (index !== -1) {
        totalPrice -= selectedProducts[index].totalPrice;
        selectedProducts.splice(index, 1);
        updateSelectedProductsTable();
    }
}

function increaseQuantity(barCode, invoiceId) {
    const product = selectedProducts.find(p => p.barCode === barCode && p.invoiceId === invoiceId);
    if (product && product.quantity < product.inventoryDTO.quantity) {
        product.quantity++;
        product.totalPrice += product.price;
        totalPrice += product.price;
        updateSelectedProductsTable();
    }
}

function decreaseQuantity(barCode, invoiceId) {
    const product = selectedProducts.find(p => p.barCode === barCode && p.invoiceId === invoiceId);
    if (product && product.quantity > 1) {
        product.quantity--;
        product.totalPrice -= product.price;
        totalPrice -= product.price;
        updateSelectedProductsTable();
    }
}

function fetchInvoiceDetails(invoiceId) {
    var formData = new FormData();
    formData.append('invoice', invoiceId);

    $.ajax({
        url: 'http://localhost:8080/invoice/view-invoice',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        headers: {
            Authorization: `Bearer ${token}`,
        },
        success: function (response) {
            if (response.status === 'OK') {
                var products = response.data.listOrderInvoiceDetail;
                var tableBody = $('#product-table-body');
                tableBody.empty(); // Clear existing rows
                $.each(products, function (index, item) {
                    var productWithInvoiceId = { ...item.productDTO, invoiceId: invoiceId };
                    var row = `
            <tr>
              <td class="border px-4 py-2">${invoiceId}</td>
              <td class="border px-4 py-2">${item.productDTO.productCode}</td>
              <td class="border px-4 py-2">${item.productDTO.name}</td>
              <td class="border px-4 py-2">${item.totalPrice}</td>
              <td class="border px-4 py-2"><button class="bg-green-500 text-white p-2" onclick='addProduct(${JSON.stringify(productWithInvoiceId)}, "${invoiceId}")'>Add</button></td>
            </tr>
          `;
                    tableBody.append(row);
                });

                // Display user info
                $('#selected-user-info').show();
                var userInfo = response.data.userInfoDTO;
                $('#user-details').html(`
          <p>ID: ${userInfo.id}</p>
          <p>Name: ${userInfo.fullName}</p>
        `);
            } else {
                alert('Invoice not found');
            }
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });
}

$(function () {
    $('#add-invoiceid-button').click(function () {
        var invoiceId = $('#invoiceid-input').val();
        fetchInvoiceDetails(invoiceId);
    });

    $('#create-invoice-button').click(function () {
        // Handle create invoice logic
    });
});