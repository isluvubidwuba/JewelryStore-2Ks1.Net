$(document).ready(function () {
    fetchCounters();
    createCounterModal();
    setupAddProductModal();
    setupFilters();
});

function fetchCounters() {
    $.ajax({
        url: 'http://localhost:8080/counter/all',
        method: 'GET',
        success: function (response) {
            if (response.status === 'OK') {
                const counters = response.data;
                generateTabs(counters);
                generateTabContents(counters);
                populateCounterSelect(counters);

                if (counters.length > 0) {
                    const firstCounterId = counters[1].id;
                    switchToTab(firstCounterId);
                    fetchProductsByCounter(firstCounterId);
                }
            }
        },
        error: function (error) {
            console.error('Error fetching counters:', error);
        }
    });
}
function generateTabs(counters) {
    const tabsContainer = $('#counter-tabs');
    counters.forEach((counter, index) => {
        if (counter.id === 1) return; // Bỏ qua quầy có id là 1

        const tab = $('<li>', { class: 'mr-2 flex items-center' });

        const tabLinkContainer = $('<div>', {
            class: 'flex items-center bg-blue-100 hover:bg-blue-200 rounded-lg'
        });

        const tabLink = $('<a>', {
            href: '#',
            class: `inline-block py-3 px-4 rounded-lg ${index === 0 ? 'text-white bg-blue-300 active' : 'text-gray-900 bg-blue-100 hover:bg-blue-200'}`,
            text: counter.name,
            'data-tab': `tab-${counter.id}`
        });

        const deleteIcon = $('<button>', {
            class: 'ml-2 text-white-500 hover:text-red-700 p-1',
            html: '<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>'
        }).on('click', function (e) {
            e.stopPropagation(); // Ngăn chặn sự kiện click của tab
            deleteCounter(counter.id);
        });

        tabLinkContainer.append(tabLink, deleteIcon);
        tab.append(tabLinkContainer);
        tabsContainer.append(tab);

        tabLink.on('click', function (e) {
            e.preventDefault();
            $('#counter-tabs a').removeClass('text-white bg-blue-600 active');
            $('#counter-tabs a').addClass('text-gray-900 bg-blue-100 hover:bg-blue-200');
            $('#tab-contents > div').addClass('hidden');

            tabLink.removeClass('text-gray-900 bg-blue-100 hover:bg-blue-200');
            tabLink.addClass('text-white bg-blue-600 active');
            $(`#${tabLink.data('tab')}`).removeClass('hidden');

            // Load products for the selected counter
            fetchProductsByCounter(counter.id);
        });
    });
}

function generateTabContents(counters) {
    const contentsContainer = $('#tab-contents');
    counters.forEach((counter, index) => {
        if (counter.id === 1) return; // Bỏ qua nội dung của quầy có id là 1

        const content = $('<div>', {
            id: `tab-${counter.id}`,
            class: `${index !== 0 ? 'hidden' : ''}`
        });

        // Thêm bảng hiển thị danh sách sản phẩm
        const table = $('<table>', { class: 'min-w-full divide-y divide-gray-200' }).append(
            $('<thead>', { class: 'bg-gray-50' }).append(
                $('<tr>').append(
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Product Code' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Barcode' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Image' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Name' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Fee' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Weight' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Material' }),
                    $('<th>', { class: 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider', text: 'Category' })
                )
            ),
            $('<tbody>', { id: `table-body-${counter.id}`, class: 'bg-white divide-y divide-gray-200' })
        );

        // Thêm phần điều hướng trang
        const pagination = $('<div>', { class: 'flex justify-between items-center mt-4' }).append(
            $('<button>', { class: 'prev-page px-4 py-2 bg-blue-500 text-white rounded', text: 'Previous', 'data-counter-id': counter.id }),
            $('<span>', { id: `page-info-${counter.id}`, text: 'Page 1' }),
            $('<button>', { class: 'next-page px-4 py-2 bg-blue-500 text-white rounded', text: 'Next', 'data-counter-id': counter.id })
        );

        content.append(table).append(pagination);
        contentsContainer.append(content);
    });

    // Sự kiện click cho các nút điều hướng
    $('.prev-page').on('click', function () {
        const counterId = $(this).data('counter-id');
        const currentPage = parseInt($(`#page-info-${counterId}`).text().split(' ')[1]);
        if (currentPage > 1) {
            fetchProductsByCounter(counterId, currentPage - 1);
        }
    });

    $('.next-page').on('click', function () {
        const counterId = $(this).data('counter-id');
        const currentPage = parseInt($(`#page-info-${counterId}`).text().split(' ')[1]);
        fetchProductsByCounter(counterId, currentPage + 1);
    });
}


function fetchProductsByCounter(counterId, page = 1) {
    $.ajax({
        url: `http://localhost:8080/counter/listproductsbycounter?counterId=${counterId}&page=${page - 1}`,
        method: 'GET',
        success: function (response) {
            const products = response.products;
            const tableBody = $(`#table-body-${counterId}`);
            tableBody.empty();
            products.forEach(product => {
                const row = $('<tr>').append(
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.productCode }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.barCode }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', html: product.img ? `<img src="${product.img}" alt="${product.name}" class="h-10 w-10 rounded-full">` : '' }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.name }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.fee }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.weight }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.materialDTO.name }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.productCategoryDTO.name })
                );
                tableBody.append(row);
            });

            // Cập nhật thông tin trang
            const totalPages = response.totalPages;
            const currentPage = response.currentPage + 1;
            $(`#page-info-${counterId}`).text(`Page ${currentPage} of ${totalPages}`);

            // Vô hiệu hóa các nút khi cần thiết
            if (currentPage === 1) {
                $(`button.prev-page[data-counter-id=${counterId}]`).prop('disabled', true).addClass('opacity-50 cursor-not-allowed');
            } else {
                $(`button.prev-page[data-counter-id=${counterId}]`).prop('disabled', false).removeClass('opacity-50 cursor-not-allowed');
            }
            if (currentPage === totalPages) {
                $(`button.next-page[data-counter-id=${counterId}]`).prop('disabled', true).addClass('opacity-50 cursor-not-allowed');
            } else {
                $(`button.next-page[data-counter-id=${counterId}]`).prop('disabled', false).removeClass('opacity-50 cursor-not-allowed');
            }
        },
        error: function (error) {
            console.error('Error fetching products:', error);
        }
    });
}


function createCounterModal() {
    $('#openCreateCounterModal').on('click', function () {
        $('#createCounterModal').removeClass('hidden');
    });

    $('#closeCreateCounterModal, #cancelCreateCounter').on('click', function () {
        $('#createCounterModal').addClass('hidden');
    });

    $('#createCounterForm').on('submit', function (e) {
        e.preventDefault();

        const counterName = $('#counterName').val();

        $.ajax({
            url: 'http://localhost:8080/counter/insert',
            method: 'POST',
            data: { name: counterName },
            success: function (response) {
                if (response.status === 'OK') {
                    alert('Counter created successfully');
                    location.reload(); // Reload the page to update the counter list
                } else {
                    alert('Error creating counter');
                }
            },
            error: function (error) {
                console.error('Error:', error);
                alert('Error creating counter');
            }
        });

        $('#createCounterModal').addClass('hidden');
    });
}







function fetchProductsForCounter() {
    $.ajax({
        url: 'http://localhost:8080/counter/products/counter1',
        method: 'GET',
        success: function (response) {
            const productTableBody = $('#productTableBody');
            productTableBody.empty();

            // Thu thập các danh mục duy nhất từ sản phẩm
            const categories = [...new Set(response.map(product => product.productCategoryDTO.name))];
            const materials = [...new Set(response.map(product => product.materialDTO.name))];

            populateCategoryFilter(categories);
            populateMaterialFilter(materials);

            response.forEach(product => {
                const row = $('<tr>').append(
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.productCode }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.name }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.productCategoryDTO.name }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap', text: product.materialDTO.name }),
                    $('<td>', { class: 'px-6 py-4 whitespace-nowrap' }).append(
                        $('<input>', { type: 'checkbox', value: product.id })
                    )
                );

                // Thêm sự kiện click vào hàng
                row.on('click', function () {
                    const checkbox = $(this).find('input[type="checkbox"]');
                    checkbox.prop('checked', !checkbox.prop('checked'));
                });

                // Ngăn chặn sự kiện click vào checkbox không kích hoạt sự kiện của hàng
                row.find('input[type="checkbox"]').on('click', function (e) {
                    e.stopPropagation();
                });

                productTableBody.append(row);
            });
        },
        error: function (error) {
            console.error('Error fetching products:', error);
        }
    });
}



function setupAddProductModal() {
    $('#openAddProductModal').on('click', function () {
        $('#modalTitle').text('Add Products to Counter');
        $('#addProductSection').removeClass('hidden');
        $('#selectCounterSection').addClass('hidden');
        $('#combinedModal').removeClass('hidden');
        fetchProductsForCounter();
        setupCategoryFilter(); // Gọi hàm setupCategoryFilter tại đây
    });

    $('#closeCombinedModal, #cancelAddProduct, #cancelSelectCounter').on('click', function () {
        $('#combinedModal').addClass('hidden');
    });

    $('#submitAddProductToCounter').on('click', function () {
        const counterId = $('#counterSelect').val();
        const selectedProducts = [];
        $('#productTableBody input:checked').each(function () {
            selectedProducts.push({ id: $(this).val() });
        });

        $.ajax({
            url: `http://localhost:8080/counter/addproductsforcounter?counterId=${counterId}`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(selectedProducts),
            success: function (response) {
                if (response.status === 'OK') {
                    alert('Products added to counter successfully');
                    $('#combinedModal').addClass('hidden');
                    switchToTab(counterId); // Chuyển tới tab theo counterId
                } else {
                    alert('Error adding products to counter');
                }
            },
            error: function (error) {
                console.error('Error:', error);
                alert('Error adding products to counter');
            }
        });
        $('#combinedModal').addClass('hidden');
    });
}



function fetchCountersForSelect() {
    $.ajax({
        url: 'http://localhost:8080/counter/all',
        method: 'GET',
        success: function (response) {
            if (response.status === 'OK') {
                const counters = response.data;
                populateCounterSelect(counters);
            }
        },
        error: function (error) {
            console.error('Error fetching counters:', error);
        }
    });
}

function populateCounterSelect(counters) {
    const counterSelect = $('#counterSelect');
    counterSelect.empty(); // Xóa các tùy chọn cũ
    const defaultOption = $('<option>', {
        value: '',
        text: 'Choose counter',
        disabled: true,
        selected: true
    });
    counterSelect.append(defaultOption);
    counters.forEach(counter => {
        if (counter.id !== 1) { // Bỏ qua quầy có id là 1
            const option = $('<option>', {
                value: counter.id,
                text: counter.name
            });
            counterSelect.append(option);
        }
    });
}


function setupFilters() {
    $('#categoryFilter').on('change', function () {
        const selectedCategory = $(this).val();
        filterProducts();
    });

    $('#materialFilter').on('change', function () {
        const selectedMaterial = $(this).val();
        filterProducts();
    });
}



function populateCategoryFilter(categories) {
    const categoryFilter = $('#categoryFilter');
    categoryFilter.empty();
    categoryFilter.append($('<option>', {
        value: 'all',
        text: 'All Categories'
    }));
    categories.forEach(category => {
        const option = $('<option>', {
            value: category,
            text: category
        });
        categoryFilter.append(option);
    });
}

function populateMaterialFilter(materials) {
    const materialFilter = $('#materialFilter');
    materialFilter.empty();
    materialFilter.append($('<option>', { value: 'all', text: 'All Materials' }));
    materials.forEach(material => {
        materialFilter.append($('<option>', { value: material, text: material }));
    });
}

function filterProducts() {
    const selectedCategory = $('#categoryFilter').val();
    const selectedMaterial = $('#materialFilter').val();
    const rows = $('#productTableBody tr');

    rows.each(function () {
        const row = $(this);
        const productCategory = row.find('td').eq(2).text();
        const productMaterial = row.find('td').eq(3).text();

        if ((selectedCategory === 'all' || productCategory === selectedCategory) &&
            (selectedMaterial === 'all' || productMaterial === selectedMaterial)) {
            row.show();
        } else {
            row.hide();
        }
    });
}



function switchToTab(counterId) {
    $(`#counter-tabs a[data-tab="tab-${counterId}"]`).trigger('click');
}


function deleteCounter(counterId) {
    $.ajax({
        url: `http://localhost:8080/counter/delete/${counterId}`,
        method: 'DELETE',
        success: function (response) {
            if (response.status === 'OK') {
                alert('Counter deleted successfully');
                // Xóa tab và nội dung tab ngay lập tức
                $(`#counter-tabs a[data-tab="tab-${counterId}"]`).closest('li').remove();
                $(`#tab-${counterId}`).remove();
                fetchCountersForSelect();
                switchToTab(2); // Chuyển đến tab có counter id = 2
            } else {
                alert('Error deleting counter');
            }
        },
        error: function (error) {
            console.error('Error:', error);
            alert('Error deleting counter');
        }
    });
}