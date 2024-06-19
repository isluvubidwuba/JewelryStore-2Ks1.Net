$(document).ready(function () {
    const modal = $('#productSelectionModal');

    function toggleModal() {
        modal.toggleClass('hidden');
    }

    $('#openProductModal, #closeProductModal, #cancelProductSelection, #submitProductSelection').on('click', toggleModal);
});
