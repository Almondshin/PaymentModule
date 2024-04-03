$.datepicker.monthpicker = {
    closeText: "닫기",
    nextText: "다음 달",
    prevText: "이전 달",
    currentText: "오늘",
    changeMonth: true,
    changeYear: true,
    monthNames: ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
    monthNamesShort: ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
    dayNames: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
    dayNamesShort: ["일", "월", "화", "수", "목", "금", "토"],
    dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"],
    weekHeader: "주",
    firstDay: 0,
    isRTL: false,
    showMonthAfterYear: true,
    yearSuffix: "",
    showOn: "both",
    // buttonText: "달력",
    showButtonPanel: true,
    dateFormat: "yy-mm-dd",
    yearRange: "-10:+0",
};

$.datepicker.setDefaults($.datepicker.monthpicker);

var datepicker_default = {
    showOn: "both",
    buttonText: "달력",
    currentText: "이번달",
    changeMonth: true,
    changeYear: true,
    showButtonPanel: false,
    yearRange: "c-99:c+99",
    showOtherMonths: true,
    selectOtherMonths: true,
    onSelect: function (dateString) {
        let index = document.querySelector("input[name=product_select]:checked").dataset.index
        if (process.selectedProduct.productCode !== "") {
            getSelectedProduct()
            setSelectedValue(index)
        }
    },
};

datepicker_default.closeText = "선택";
datepicker_default.dateFormat = "yy-mm-dd";
$("#datepicker").datepicker(datepicker_default);
$("#datepicker").val(`${new Date().toISOString().slice(0, 10)}`);
