const preInputParameter = {
    agencyId : "",
    siteId   : "",
    startDate: "",
    rateSel  : "",
    openType : "",
}

//변경부분
// let productDatalist = []; 추가
let productDatalist = [];

const init = function () {
    document.querySelector("#selectProductPopUpOpen").addEventListener("click", function () {
        popupOpen(2)
    })
    document.querySelector("#selectProduct").addEventListener("click", function () {
        process.selectProduct();
    })
    document.querySelector("#payment").addEventListener("click", function () {
        payProgress()
        process.setPaymentSiteInfo();
    })
    document.querySelector("#cancelSelectProduct").addEventListener("click", function () {
        popupClose()
    })
    document.querySelector("#cancelPayment").addEventListener("click", function () {
        popupClose()
    })

    process.data.agencyId = utils.getParam("agencyId");
    process.data.siteId = utils.getParam("siteId");
    process.data.startDate = utils.getParam("startDate");
    process.data.rateSel = utils.getParam("rateSel");

    for (let key in preInputParameter) {
        preInputParameter[key] = utils.getParam(key)
    }

    process.getPaymentInfo();
}

const process = {
    data                : {
        agencyId    : "",
        siteId      : "",
        startDate   : "",
        endDate     : "",
        rateSel     : "",
        salesPrice  : "",
        method      : "",
        excessCount : "",
        excessAmount: ""
    }
    , selectedProduct   : {}
    , getPaymentInfo    : function () {
        const request = new XMLHttpRequest();
        request.open("POST", "/agency/payment/getPaymentInfo", false);
        request.setRequestHeader("Content-type", "application/json");

        request.onreadystatechange = function () {
            let response = {
                listSel          : "",
                clientInfo       : "",
                profileUrl       : "",
                profilePaymentUrl: "",
                resultCode       : "",
                excessAmount     : "",
                extensionStatus  : "",
            }
            response = JSON.parse(this.response);
            console.log(response);
            if (response.resultCode !== "2000") {
                process.alert({
                    main: response.resultMsg,
                    sub : "오류코드 : " + response.resultCode,
                    btn : "확인"
                })
                return;
            }

            [companyName, bizNumber, ceoName] = response.clientInfo.split(",");

            payment.setText("info", "companyName", companyName);
            payment.setValue("info", "companyName", companyName);
            payment.setText("info", "bizName", bizNumber);
            payment.setValue("info", "bizName", bizNumber);
            payment.setText("info", "name", ceoName);
            payment.setValue("info", "name", ceoName);

            //변경부분
            // let productDatalist = []; 제거

            Array.prototype.forEach.call(response.listSel, function (el, index) {
                productDatalist[index] = {
                    //TODO 변경 부분
                    // index                  : index,
                    index                  : index,
                    productCode            : el.type,
                    productName            : el.name,
                    productPrice           : el.price,
                    productDuration        : el.month,
                    productCount           : el.basicOffer,
                    productFeePerCase      : el.feePerCase,
                    productExcessFeePerCase: el.excessFeePerCase,
                    productAutopay         : el.type.indexOf("autopay") > -1 ? true : false
                }
            })

            console.log("response.startDate :" + response.startDate);

            if (response.profileUrl != null) {
                profileSpecificUrl = response.profileUrl;
            }
            if (response.profilePaymentUrl != null) {
                profileSpecificPaymentUrl = response.profilePaymentUrl;
            }

            if (response.excessAmount != null) {
                process.data.excessAmount = response.excessAmount;
            }

            if (response.excessCount != null) {
                process.data.excessCount = response.excessCount;
            }

            if (response.extensionStatus != null) {
                payment.toggleContainer("startDate", false);
            }

            if (response.startDate !== "" && response.startDate != null) {
                document.querySelector("#datepicker").value = response.startDate;
            }

            if (preInputParameter.startDate !== "" && preInputParameter.startDate != null) {
                document.querySelector("#datepicker").value = preInputParameter.startDate;
            }

            //변경부분
            // setProductTable(productDatalist)
            productDatalist.forEach(e => createProductTableRow(e))


            if (response.rateSel != null && response.rateSel !== "") {
                let matchedProduct = productDatalist.find(e => e.productCode === response.rateSel); // 해당하는 객체를 찾음
                if (matchedProduct !== undefined) {
                    let index = matchedProduct.index; // data-index 값을 찾음
                    let radioButton = document.querySelector(`input[name='product_select'][data-index='${index}']`);
                    if (radioButton) {
                        radioButton.checked = true;
                    }
                    process.selectProduct();
                    setSelectedValue(index);
                }
            }

            if (preInputParameter.rateSel !== "") {
                let matchedProduct = productDatalist.find(e => e.productCode === preInputParameter.rateSel); // 해당하는 객체를 찾음
                if (matchedProduct !== undefined) {
                    let index = matchedProduct.index; // data-index 값을 찾음
                    let radioButton = document.querySelector(`input[name='product_select'][data-index='${index}']`);
                    if (radioButton) {
                        radioButton.checked = true;
                    }
                    process.selectProduct();
                    setSelectedValue(index);
                }
            }
        }
        let data = {
            agencyId : process.data.agencyId,
            siteId   : process.data.siteId,
            startDate: process.data.startDate,
            rateSel  : process.data.rateSel
        }
        try {
            request.send(JSON.stringify(data))
        } catch (error) {
            alert("서버 통신 불가")
        }
    }
    , setPaymentSiteInfo: function () {
        let request = new XMLHttpRequest();
        request.open("POST", profileSpecificPaymentUrl + "/agency/payment/setPaymentSiteInfo", false);
        request.setRequestHeader("Content-type", "application/json");
        request.onreadystatechange = function () {
            process.HFPayment(JSON.parse(this.response))
        }

        process.data.method = document.querySelector("input[name=howtopay]:checked").value;
        process.data.startDate = document.querySelector("#datepicker").value;
        process.data.rateSel = process.selectedProduct.productCode;
        console.log(JSON.stringify(process.data))

        if (JSON.stringify(process.data.rateSel) == null) {
            process.alert({
                main: "상품을 선택해주세요.",
                sub : "",
                btn : "확인"
            })
            return;
        }
        request.send(JSON.stringify(process.data));
    }
    , HFPayment         : function (response) {
        if (response.resultCode === "2000") {
            var mchtName = "상점이름"
            var mchtEName = "gyMerchantId"
            var type = "popup"

            switch (preInputParameter.openType) {
                case "redirect" : {
                    type = "self";
                    break;
                }
                case "popup" : {
                    type = "popup";
                    break;
                }
                default : {
                    type = "popup";
                    break;
                }
            }

            const autopayText = payment.getText("payment", "autopay")
            let autopayYN;
            switch (autopayText.value) {
                case "사용" : {
                    autopayYN = "Y"
                    break;
                }
                case "사용" : {
                    autopayYN = "N"
                    break;
                }
            }

            const mchtParams =
                "agencyId=" + process.data.agencyId +
                "&siteId=" + process.data.siteId +
                "&startDate=" + process.data.startDate +
                "&endDate=" + process.data.endDate +
                "&rateSel=" + this.selectedProduct.productCode +
                "&offer=" + process.data.offer +
                "&productName=" + this.selectedProduct.productName +
                "&autopayYN=" + autopayYN +
                "&companyName=" + companyName +
                "&bizNumber=" + bizNumber;

            console.log(mchtParams)


            SETTLE_PG.pay({
                env: "https://tbnpg.settlebank.co.kr",   //결제서버 URL
                // env       : "https://npg.settlebank.co.kr",   //결제서버 URL
                mchtName : mchtName,
                mchtEName: mchtEName,
                mchtParam: mchtParams,
                pmtPrdtNm: this.selectedProduct.productName + "( " + process.data.startDate + " - " + process.data.endDate + " )",
                mchtId   : response.mchtId,
                method   : response.method,
                trdDt    : response.trdDt,
                trdTm    : response.trdTm,
                mchtTrdNo: response.mchtTrdNo,
                trdAmt   : response.encParams.trdAmt,
                pktHash  : response.hashCipher,
                notiUrl  : profileSpecificUrl + "/agency/payment/api/result/noti",
                nextUrl  : profileSpecificUrl + "/agency/payment/api/result/next",
                cancUrl  : profileSpecificUrl + "/agency/payment/api/result/cancel",
                ui       : {
                    type  : type,   //popup, iframe, self, blank
                    width : "430",   //popup창의 너비
                    height: "660"   //popup창의 높이
                }
            }, function (rsp) {
                //iframe인 경우 전달된 결제 완료 후 응답 파라미터 처리
                console.log(rsp);
            });
        } else {
            process.alert({
                main: response.resultMsg,
                sub : "오류코드 : " + response.resultCode,
                btn : "확인"
            })

            console.log(JSON.stringify(response));
        }
    }
    , select            : function () {
        var price = parseInt(process.selectedProduct.productPrice)
        var duration = parseInt(process.selectedProduct.productDuration);
        var offer = parseInt(process.selectedProduct.productCount);

        var startDate = document.querySelector("#datepicker").value;
        startDate = new Date(startDate);
        var currentDate = new Date(startDate.getTime());
        var currentLastDate = new Date(startDate.getTime());
        currentLastDate = new Date(currentLastDate.setDate(1));
        currentLastDate = new Date(currentLastDate.setMonth(currentLastDate.getMonth() + 1));
        currentLastDate = new Date(currentLastDate.setDate(0));
        var endDate = new Date(startDate.getTime());

        var remainDate = currentLastDate.getDate() - currentDate.getDate() + 1
        if (duration === 1) {
            if (remainDate <= 14) {
                endDate = new Date(endDate.setMonth(endDate.getMonth() + 2));
                endDate = new Date(endDate.setDate(0));
            } else {
                endDate = new Date(endDate.setMonth(endDate.getMonth() + 1));
                endDate = new Date(endDate.setDate(0));
            }
        } else {
            endDate = new Date(endDate.setMonth(endDate.getMonth() + duration));
            endDate = new Date(endDate.setDate(0));

        }

        endDate = endDate.toISOString().slice(0, 10);

        process.data.endDate = endDate;

        // document.querySelector("#endDate").value = last.getFullYear() + "-" + ((last.getMonth() + 1) < 10 ? "0" + (last.getMonth() + 1) : (last.getMonth() + 1)) + "-" + last.getDate();

        if (duration === 1) {
            if (remainDate <= 14) {
                duration = (remainDate / currentLastDate.getDate()) + 1;
            } else {
                duration = (remainDate / currentLastDate.getDate());
            }
        } else {
            duration = ((remainDate / currentLastDate.getDate()) + (duration - 1)) / duration;
        }
        process.data.salesPrice = (Math.floor(price * duration * 1.1) + Math.floor(process.data.excessAmount)).toString()
        process.data.offer = Math.floor(offer * duration).toString()

        console.log(`선택일 : ${currentDate.toISOString().slice(0, 10)} 선택월 말일 : ${currentLastDate.toISOString().slice(0, 10)} 종료일 : ${endDate}
                    \n가격 : ${Math.floor(price * duration * 1.1)} 제공건수 : ${Math.floor(offer * duration)}  초과건수 : ${Math.floor(process.data.excessCount)}  초과금액 : ${Math.floor(process.data.excessAmount)}`)

        // return `${returnPrice.toLocaleString()}원 (${process.data.offer.toLocaleString()}건)`
    }
    , selectProduct     : function () {
        if (!document.querySelector("input[name=product_select]:checked")) {
            process.alert({
                main: "상품을 선택해주세요.",
                sub : "",
                btn : "확인"
            })
            // alert("상품을 선택해주세요.")
            return;
        }
        let index = document.querySelector("input[name=product_select]:checked").dataset.index
        process.selectedProduct = productDatalist[index];
        //변경부분
        // setPayment()
        getSelectedProduct()
        setSelectedValue(index)
        popupClose()
    }
    , alert             : function (obj) {
        popupTextlist["custom"] = obj
        setPopup("custom")
        popupOpen(1);
    }
}

function comma(text) {
    return text.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
}

function setSelectedValue(index) {
    payment.setText("payment", "productName", productDatalist[index].productName)
    payment.setValue("payment", "productName", productDatalist[index].productName)

    payment.setText("payment", "autopay", productDatalist[index].productAutopay ? "사용" : "미사용")
    payment.setValue("payment", "autopay", productDatalist[index].productAutopay)

    if (productDatalist[index].productAutopay) {
        payment.radioDisable("radioDeposit");
        payment.radioSelect("radioCreditcard");
    } else {
        payment.radioDisableAll();
    }
    process.select()

    if(process.data.excessCount === null || process.data.excessCount === "" ){
        process.data.excessCount = "0";
    }
    payment.setText("payment", "period", document.querySelector("#datepicker").value + "~" + process.data.endDate)
    payment.setText("payment", "price", `${comma(process.data.salesPrice)}원 (기본 제공건수 ${comma(process.data.offer)}건 + 이용 초과건수 ${comma(process.data.excessCount)}건)`)
    payment.setValue("payment", "price", `${process.data.salesPrice.toLocaleString()}`)
}

function payProgress() {
    let datepicker = document.querySelector("#datepicker").value;
    var datePattern = /^\d{4}-\d{2}-\d{2}$/;
    let today = getTodayDate();
    if (datePattern.test(datepicker) && today <= datepicker) {
    } else {
        setPopup("date");
        popupOpen(1);
    }
}

function getTodayDate() {
    var today = new Date();
    var year = today.getFullYear();
    var month = String(today.getMonth() + 1).padStart(2, "0");
    var day = String(today.getDate()).padStart(2, "0");
    return year + "-" + month + "-" + day;
}

// function calcPrice() {
//     let obj = getSelectedProduct();
//     let datepickerValue = document.querySelector("#datepicker").value.split("-");
//     let year = parseInt(datepickerValue[0]);
//     let month = parseInt(datepickerValue[1]);
//     let day = parseInt(datepickerValue[2]) - 1;
//     let lastDay = new Date(year, month, 0).getDate();
//     let price = Math.floor(obj.productPrice * ((lastDay - day) / lastDay));
//     let count = Math.floor(obj.productCount * ((lastDay - day) / lastDay));
//     console.log("datepickerValue : " + datepickerValue)
//     return price + "원 (" + count + "건)";
// }


const utils = {
    getParam: function (name) {
        // let search = location.search.substring(location.search.indexOf("?") + 1);
        // let value;

        // search = search.split("&");

        // for (let i = 0; i < search.length; i++) {
        //     let temp = search[i].split("=");
        //     if (temp[0] === name) {
        //         value = temp[1];
        //     }
        // }

        // for (let item of search) {
        //     let temp = item.split("=")
        //     value = temp[0] === name ? temp[1] : ""
        // }

        // return value !== undefined ? decodeURIComponent(value) : value;

        let params = new URLSearchParams(location.search);
        let value = params.get(name)
        return value == null ? "" : decodeURIComponent(value);
    }
}

const thankYouParameter = {
    productName: "", bizNumber: "", companyName: "", startDate: "", autoPay: "",
}

const thankYouInit = function () {
    for (let key in thankYouParameter) {
        thankYouParameter[key] = utils.getParam(key)
    }

    setInformation_5({
        bizNum     : thankYouParameter.bizNumber,
        companyName: thankYouParameter.companyName,
        startDate  : thankYouParameter.startDate,
        autoPay    : thankYouParameter.autoPay === "Y" ? "사용" : "미사용",
        productName: thankYouParameter.productName
    });
}

