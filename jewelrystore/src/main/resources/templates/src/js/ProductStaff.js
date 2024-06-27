$(document).ready(function () {
    const token = localStorage.getItem("token");
    var selectedInvoiceType = '';

    // Toggle dropdown with animation
    $('#dropdown-button').click(function () {
        $('#dropdown').toggle(); // Toggle visibility of the dropdown
        // Force re-animation by triggering reflow
        $('#dropdown ul li').each(function () {
            var el = $(this);
            el.css('animation', 'none');
            el.height(); // Trigger reflow
            el.css('animation', '');
        });
    });

    // Dropdown item click handling
    $('.invoice-option').click(function () {
        selectedInvoiceType = $(this).data('value'); // Save the selected invoice type
        var text = $(this).text();
        $('#dropdown-button').text(text).append('<svg class="w-4 h-4 ml-2" aria-hidden="true" fill="none" viewBox="0 0 10 6"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 4 4 4-4"/></svg>');
        $('#dropdown').slideUp(300); // Close the dropdown with animation
    });



});