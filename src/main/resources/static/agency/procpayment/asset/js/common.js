// 퍼블리싱 이벤트, ui 관련 스크립트 파일
let focusItem;
// 1,2,3 @@@@@@@@@@@@@@@
// 1페이지 input 태그 이벤트
function requestServiceInputInit() {
    const inputList = document.querySelectorAll(".info-item-input");
    inputList.forEach((itm) => {
        itm.addEventListener("focusin", function () {
            // this.classList.remove("invalid");
            document.querySelector(`label[for=${itm.id}]`).classList.remove("invalid");
        });
    });
    inputList.forEach((itm) => {
        itm.addEventListener("focusout", function () {
            if (checkPattern(itm)) {
                this.classList.remove("invalid");
                document.querySelector(`label[for=${itm.id}]`).classList.remove("invalid");
            } else {
                this.classList.add("invalid");
                document.querySelector(`label[for=${itm.id}]`).classList.add("invalid");
            }
        });
    });
}
// 1 - 2 페이지간 이동 로직
function moveInfoCheckpage(boolean) {
    let formPage = document.querySelectorAll(".info-form-page");
    let checkPage = document.querySelectorAll(".info-check-page");
    if (boolean) {
        for (let i = 0; i < formPage.length; i++) {
            formPage[i].classList.remove("active");
        }
        for (let i = 0; i < checkPage.length; i++) {
            checkPage[i].classList.add("active");
            setCheckpage();
        }
    } else {
        for (let i = 0; i < formPage.length; i++) {
            formPage[i].classList.add("active");
        }
        for (let i = 0; i < checkPage.length; i++) {
            checkPage[i].classList.remove("active");
        }
    }
}
function setCheckpage() {
    requestService2.setText("confirm", "companyName", requestService1.getValue("info", "companyName"));
    requestService2.setText("confirm", "bizNum", requestService1.getValue("info", "bizNum"));
    requestService2.setText("confirm", "name", requestService1.getValue("info", "name"));
    requestService2.setText("confirm", "email", requestService1.getValue("info", "email"));
    requestService2.setText("confirm", "number", requestService1.getValue("info", "number"));
    requestService2.setText("confirm", "address", requestService1.getValue("info", "address1") + " " + requestService1.getValue("info", "address2"));
    requestService2.setText("confirm", "managerName", requestService1.getValue("manager", "name"));
    requestService2.setText("confirm", "managerPhone", requestService1.getValue("manager", "phone"));
    requestService2.setText("confirm", "managerTel", requestService1.getValue("manager", "tel"));
    requestService2.setText("confirm", "managerEmail", requestService1.getValue("manager", "email"));
    requestService2.setText("confirm", "productName", "상품명이 들어오는곳");
    requestService2.setText("confirm", "autopay", "자동결제여부");
}
// 1->2페이지 이동 조건확인
function requestServiceBtn() {
    const inputList = document.querySelectorAll(".info-item-input");
    const agreeList = document.querySelectorAll("input[name=agree]");
    const productList = document.querySelectorAll("input[name=product_select]");

    let shouldBreakcheckbox = false;
    let shouldBreakproduct = false;
    let isChecked = false;

    for (let i = 0; i < inputList.length; i++) {
        if (!checkPattern(inputList[i])) {
            focusItem = inputList[i];
            popupOpen(3);
            setPopup("requiredInfo");
            shouldBreakcheckbox = true;
            break;
        }
    }
    if (!shouldBreakcheckbox) {
        for (let i = 0; i < agreeList.length; i++) {
            if (!agreeList[i].checked) {
                focusItem = agreeList[i];
                popupOpen(3);
                setPopup("agree");
                shouldBreakproduct = true;
                break;
            }
        }
    }
    if (!shouldBreakproduct && !shouldBreakcheckbox) {
        if (!getSelectedProduct()) {
            focusItem = productList[0];
            popupOpen(3);
            setPopup("product");
            isChecked = true;
        } else {
            requestData.product = getSelectedProduct();
        }
    }
    if (!shouldBreakproduct && !shouldBreakcheckbox && !isChecked) {
        moveInfoCheckpage(true);
    }
}

// 공통 @@@@@@@@@@@@@@@@@@@@@@@@
// 번호, 주소등 입력값 체크
function checkPattern(elemnt) {
    let boolean = false;
    switch (elemnt.id) {
        case "manager_email": {
            let pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            {
                pattern.test(elemnt.value) ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "manager_tel": {
            let pattern = /^\+?\d{1,4}[-.\s]?\(?\d{1,4}\)?[-.\s]?\d{1,9}[-.\s]?\d{1,9}$/;
            {
                pattern.test(elemnt.value) ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "manager_phone": {
            let pattern = /^\+?\d{1,4}[-.\s]?\(?\d{1,4}\)?[-.\s]?\d{1,9}[-.\s]?\d{1,9}$/;
            {
                pattern.test(elemnt.value) ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "input_service_url": {
            let pattern = /[^\s/$.?#].[^\s]*$/;
            {
                pattern.test(elemnt.value) ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "info_email": {
            let pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            {
                pattern.test(elemnt.value) || elemnt.value.length == 0 ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "info_number": {
            let pattern = /^\+?\d{1,4}[-.\s]?\(?\d{1,4}\)?[-.\s]?\d{1,9}[-.\s]?\d{1,9}$/;
            {
                pattern.test(elemnt.value) ? (boolean = true) : (boolean = false);
            }
            break;
        }
        case "info_bizNum": {
            {
                (elemnt.value + "").length === 10 ? (boolean = true) : (boolean = false);
            }
            break;
        }
        default: {
            {
                elemnt.value > 0 || elemnt.value.length > 0 ? (boolean = true) : (boolean = false);
            }
        }
    }
    return boolean;
}
// 사용자가 선택한 상품항목 가져오기
function getSelectedProduct() {
    let index = document.querySelector("input[name=product_select]:checked").dataset.index;
    console.log(productDatalist);
    return productDatalist[index];
}
// 페이지 반응형
function responsiveLayout() {
    if (window.innerWidth > 768) {
        contentBoxList.forEach((item, index) => {
            item.classList.add("content-grid");
            item.classList.remove("content-flex");
        });
    } else {
        contentBoxList.forEach((item, index) => {
            item.classList.add("content-flex");
            item.classList.remove("content-grid");
        });
    }
}

//
window.addEventListener("resize", function () {
    responsiveLayout();
});
