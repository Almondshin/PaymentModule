/**
    모든페이지의 정보를 세팅하는 함수를 관리하는 파일
*/
// 페이지 1 서비스 신청
let requestService1 = {
    info: {
        companyName: document.querySelector("#info_company_name"),
        bizName: document.querySelector("#info_bizName"),
        name: document.querySelector("#info_name"),
        bizNum: document.querySelector("#info_bizNum"),
        number: document.querySelector("#info_number"),
        email: document.querySelector("#info_email"),
        serviceUrl: document.querySelector("#info_serviceUrl"),
        address1: document.querySelector("#info_address1"),
        address2: document.querySelector("#info_address2"),
    },
    manager: {
        name: document.querySelector("#manager_name"),
        phone: document.querySelector("#manager_phone"),
        tel: document.querySelector("#manager_tel"),
        email: document.querySelector("#manager_email"),
    },
    setValue: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].value = newText;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    getValue: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].value;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    setText: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].innerHTML = newText;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
    getText: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].innerHTML;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
};
// 페이지 2 서비스 신청정보 확인
let requestService2 = {
    confirm: {
        companyName: document.querySelector("#confirm_info_company_name"),
        bizNum: document.querySelector("#confirm_info_biz_num"),
        name: document.querySelector("#confirm_info_name"),
        email: document.querySelector("#confirm_info_email"),
        number: document.querySelector("#confirm_info_number"),
        address: document.querySelector("#confirm_info_address"),
        managerName: document.querySelector("#confirm_manager_name"),
        managerPhone: document.querySelector("#confirm_manager_phone"),
        managerTel: document.querySelector("#confirm_manager_tel"),
        managerEmail: document.querySelector("#confirm_manager_email"),
        productName: document.querySelector("#confirm_product_name"),
        autopay: document.querySelector("#confirm_product_autopay"),
    },
    setValue: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].value = newText;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    getValue: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].value;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    setText: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].innerHTML = newText;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
    getText: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].innerHTML;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
};
// 페이지 1,2 공통
// 페이지 3 서비스 신청 감사 - 데이터 변경요소 없음
// 페이지 4 심사후 결제관련 ui
let payment = {
    info: {
        container: document.querySelector("#container_requestinfo"),
        companyName: document.querySelector("#info_company_name"),
        bizName: document.querySelector("#info_biz_num"),
        name: document.querySelector("#info_userName"),
    },
    startDate: {
        container: document.querySelector("#container_startdate"),
        datepicker: document.querySelector("#datepicker"),
    },
    payment: {
        container: document.querySelector("#container_payment"),
        productName: document.querySelector("#info_product_name"),
        autopay: document.querySelector("#info_text_autopay"),
        price: document.querySelector("#info_totalprice"),
        period: document.querySelector("#info_Period_of_use"),
        radioGroupWrapper: document.querySelectorAll(".info-item-howtopay"),
        radioGroup: document.querySelectorAll("input[name=howtopay]"),
        radioDeposit: document.querySelector("#deposit"),
        radioCreditcard: document.querySelector("#creditcard"),
    },
    setText: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].innerHTML = newText;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
    getText: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].innerHTML;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
    setValue: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].value = newText;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    getValue: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].value;
        } else {
            console.error("설정되지 않은 값이거나 value가 없습니다.");
        }
    },
    // 결제방식 라디오 그룹 전체 disable
    radioDisableAll: function (boolean) {
        if (boolean) {
            this.payment.radioGroup.forEach(function (item, index) {
                item.disabled = true;
            });
        } else {
            this.payment.radioGroup.forEach(function (item, index) {
                item.disabled = false;
            });
        }
    },
    // 결제방식 라디오 버튼 disable
    radioDisable: function (elem) {
        this.payment[elem].disabled = true;
    },
    // 라디오버튼 선택하기
    radioSelect: function (elem) {
        this.payment[elem].checked = true;
    },
    // 컨테이너 감추기 false-감추기 true-보여주기
    toggleContainer: function (elem, boolean) {
        if (!boolean) {
            this[elem].container.classList.add("disabled");
        } else {
            this[elem].container.classList.remove("disabled");
        }
    },
};
// 페이지 5 결제후 감사 페이지
let thankyou = {
    info: {
        companyName: document.querySelector("#info_company_name"),
        bizNum: document.querySelector("#info_biz_num"),
        productName: document.querySelector("#info_product_name"),
        autopay: document.querySelector("#info_autopay"),
        startDate: document.querySelector("#info_start_date"),
    },
    setText: function (tab, elem, newText) {
        if (this[tab] && this[tab][elem]) {
            this[tab][elem].innerHTML = newText;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
    getText: function (tab, elem) {
        if (this[tab] && this[tab][elem]) {
            return this[tab][elem].innerHTML;
        } else {
            console.error("설정되지 않은 값이거나 innerHTML이 없습니다.");
        }
    },
};

// 1,4 공용 (데이터 테이블 부분)
// @@ 데이터 테이블 라인 만들기
let productList = document.querySelector(".product_list");
productList.innerHTML = "";
function createProductTableRow(obj) {
    console.log(obj.productAutopay);
    let productItem = document.createElement("tr");
    productItem.classList.add("product_item");
    if (obj.productAutopay === true) {
        productItem.classList.add("autopay");
    }
    //TODO 변경 부분
    // data-index="${obj.index}" 추가
    productItem.innerHTML = `
            <td><input type="radio" name="product_select" id="${obj.productCode}" data-index="${obj.index}"/></td>
            <td class='product_name'> ${obj.productName} </td>
            <td class='product_price'> ${obj.productPrice}</td>
            <td class='product_count'> ${obj.productCount}</td>
            <td class='product_feePerCase'> ${obj.productFeePerCase} </td>
            <td class='product_ExcessFeePerCase'>${obj.productExcessFeePerCase} </td>
        `;
    productList.append(productItem);
}
// 정보 세팅 퍼블리싱 테스트
function page1setinfomation() {
    // page1setinfomation - page 1: 상품정보 리스트 테스트용 함수
    let obj1 = {
        productCode: "PB01",
        productName: "라이트",
        productAutopay: false,
        productPrice: 10000,
        productCount: 200,
        productFeePerCase: 50,
        productExcessFeePerCase: 50,
    };
    let obj2 = {
        productCode: "PB0A",
        productName: "라이트",
        productAutopay: true,
        productPrice: 10000,
        productCount: 200,
        productFeePerCase: 50,
        productExcessFeePerCase: 50,
    };
    createProductTableRow(obj1);
    createProductTableRow(obj2);
}
function page2setinfomation() {
    // page2setinfomation - page 2: 신청정보확인
    requestService2.setText("confirm", "companyName", requestService1.getValue("info", "companyName"));
    requestService2.setText("confirm", "bizNum", requestService1.getValue("info", "bizNum"));
    requestService2.setText("confirm", "name", requestService1.getValue("info", "name"));
    requestService2.setText("confirm", "email", requestService1.getValue("info", "email"));
    requestService2.setText("confirm", "number", requestService1.getValue("info", "number"));
    requestService2.setText("confirm", "address", requestService1.getValue("info", "address1") + requestService1.getValue("info", "address2"));
    requestService2.setText("confirm", "managerName", requestService1.getValue("manager", "name"));
    requestService2.setText("confirm", "managerPhone", requestService1.getValue("manager", "phone"));
    requestService2.setText("confirm", "managerTel", requestService1.getValue("manager", "tel"));
    requestService2.setText("confirm", "managerEmail", requestService1.getValue("manager", "email"));
    requestService2.setText("confirm", "productName", "상품명이 들어오는곳");
    requestService2.setText("confirm", "autopay", "자동결제여부");
}
function page4setinfomation() {
    payment.setText("info", "companyName", "1");
    payment.setText("info", "bizName", "2");
    payment.setText("info", "name", "3");
    payment.setText("payment", "productName", "1");
    payment.setText("payment", "autopay", "2");
    payment.setText("payment", "price", "3");
    payment.setText("payment", "period", "4");
    payment.setValue("startDate", "datepicker", "2024-01-01");
    console.log(payment.getValue("startDate", "datepicker", "2024-01-01"));
    // payment.disableContainer("startDate", true);
    // payment.disableContainer("startDate", false);
    payment.radioDisableAll(true);
    payment.radioSelect("radioDeposit");
}
function page5setinfomation() {
    thankyou.setText("info", "companyName", "1");
    thankyou.setText("info", "bizNum", "2");
    thankyou.setText("info", "productName", "3");
    thankyou.setText("info", "autopay", "4");
    thankyou.setText("info", "startDate", "5");
}
