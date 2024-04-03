let popupTextlist = {
    requiredInfo: {
        main: "필수 정보를 모두 입력해주세요.",
        sub: "",
        btn: "",
    },
    agree: {
        main: "서비스 이용을 위해 약관에 동의해주세요.",
        sub: "",
        btn: "",
    },
    product: {
        main: "상품을 선택해주세요.",
        sub: "",
        btn: "",
    },
    date: {
        main: "정확한 서비스 희망 개시일을 선택해주세요.",
        sub: "",
        btn: "",
    },
};



function popupOpen (popNum) {
    popupClose();
    $("body").addClass("stop-scrolling");
    document.querySelector(".container > *").setAttribute("tabIndex", "-1");
    $(".popup_overlay").addClass("active");
    switch (popNum) {
        case 1: {
            $("#popup_cancel").addClass("active");
            break;
        }
        case 2: {
            $("#modal_products").addClass("active");
            break;
        }
        case 3: {
            $("#popup_alert").addClass("active");
            break;
        }
    }
}

function popupClose (popNum) {
    $("body").removeClass("stop-scrolling");
    $(".popup_overlay").removeClass("active");
    $(".popup_wrap").removeClass("active");
    $(".modal_wrap").removeClass("active");
    switch (popNum) {
        case 1: {
            $("#datepicker").focus();
            break;
        }
        case 2: {
            setPayment(getSelectedProduct());
            setProductCode(getSelectedProduct().productCode);
            break;
        }
        case 3: {
            focusItem.focus();
            break;
    }
}
}
function setPopup(key) {
    console.log(key);
    let popupMainText = document.querySelector(".popup_main_text");
    let popupSubText = document.querySelector(".popup_sub_text");
    let popupBtnText = document.querySelector(".popup_btn");
    let popuptextObj = popupTextlist[key];

    popupMainText.innerText = `${popuptextObj.main}`;
    popupSubText.innerText = `${popuptextObj.sub}`;
    popupBtnText.innerText = `${popuptextObj.btn ? popuptextObj.btn : "확인"}`;
}
