function initialize(id, numTiempos) {

    var t = $('#' + id).DataTable({
        "paging": false,
        "ordering": true,
        "info": false,
        "searching": false,
        "autoWidth": false,
        columnDefs: [{
            orderable: true,
            className: 'select-checkbox select-checkbox-all',
            targets: 0
        }],
        select: {
            style: 'multi',
            selector: 'td:first-child'
        }
    });

    t.columns(9).visible(false);
    t.columns(10).visible(false);

    if (numTiempos < 5) {
        t.columns(5).visible(false);
        t.columns(6).visible(false);
    }
    if (numTiempos < 3) {
        t.columns(3).visible(false);
        t.columns(4).visible(false);
        t.columns(7).visible(false);
        t.columns(8).visible(false);
        t.columns(9).visible(true);
        t.columns(10).visible(true);
    }

}