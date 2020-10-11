$('document').ready(function () {
    $('#timeOff').select2({
        theme: 'bootstrap4'
    });

    $('#date').daterangepicker({
        showDropdowns: true,
        minYear: moment().format('YYYY'),
        maxYear: moment().format('YYYY') + 1,
        locale: 'ro',
        isCustomDate: function (date) {
            for (let i = 0; i < holidays.length; i++) {
                let holiday = holidays[i];

                if (date.year() === holiday.date.year && (date.month() + 1) === holiday.date.month && date.date() === holiday.date.day) {
                    if (holiday.suggestion) {
                        return 'bg-gradient-warning holiday' + date.format("YYYYMMDD");
                    } else {
                        return 'bg-gradient-danger holiday' + date.format("YYYYMMDD");
                    }
                }
            }

            for (let i = 0; i < userTimeOffLogs.length; i++) {
                let userTimeOffLog = userTimeOffLogs[i];

                if (date.format("YYYYMMDD") === userTimeOffLog.date) {
                    return 'userTimeOffLog' + date.format("YYYYMMDD");
                }
            }
        },
        isInvalidDate: function (date) {
            for (let i = 0; i < userTimeOffLogs.length; i++) {
                let userTimeOffLog = userTimeOffLogs[i];

                if (date.format("YYYYMMDD") === userTimeOffLog.date) {
                    return true;
                }
            }
        }
    });

    $('#saveNewUserTimeOff').on('click', function () {
        let data = {
            'timeOffType': $('#timeOff').val(),
            'startDate': $('#date').data('daterangepicker').startDate.format("DD.MM.YYYY"),
            'endDate': $('#date').data('daterangepicker').endDate.format("DD.MM.YYYY")
        };

        $.ajax({
            type: "POST",
            url: '/time-off/add',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (data) {
                Swal.fire(
                    'Concediu placut!',
                    'Cererea de concediu a fost adaugata si trimisa spre aprobare!',
                    'success'
                ).then(() => {
                    window.location.reload();
                });
            },
            error: function (data) {
                Swal.fire(
                    'Eroare',
                    data.responseJSON.message,
                    'error'
                );
            }
        });
    });
});