$('#datepickerStartDate').datepicker({
    uiLibrary: 'bootstrap',
    format: 'dd/mm/yyyy'
});

$('#datepickerEndDate').datepicker({
    uiLibrary: 'bootstrap',
    format: 'dd/mm/yyyy'
});

$("#scope").change(function () {
    if (this.value === "historical"){
        document.getElementById("datepickerStartDate").removeAttribute("hidden");
        document.getElementById("datepickerEndDate").removeAttribute("hidden");
    } else {
        document.getElementById("datepickerStartDate").setAttribute("hidden", true);
        document.getElementById("datepickerEndDate").setAttribute("hidden", true);
    }
});