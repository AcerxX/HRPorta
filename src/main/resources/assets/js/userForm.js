$(document).ready(function () {
    $('#responsibleUser').select2({
        theme: 'bootstrap4'
    });
    $('#role').select2({
        theme: 'bootstrap4'
    });
    $('#job').select2({
        theme: 'bootstrap4',
        tags: true
    });
    $('#team').select2({
        theme: 'bootstrap4',
        tags: true
    });

    $('#hireDate').daterangepicker({
        singleDatePicker: true,
        showDropdowns: true,
        minYear: 1970,
        maxYear: moment().format('YYYY'),
        locale: 'ro'
    });
});