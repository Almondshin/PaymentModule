## 작업일지

### 퍼블리싱 ui및 데이터 수정 함수관련하여 분리작업진행

#### html상의 데이터를 변경하는 함수가 다수 묶여있어서 1,2개를 변경할때마다 전체데이터를 변경하기도하고 불필요한동작이 추가되고 수정소요가 다수 발생할것으로 보여서 수정

> 퍼블리싱 스크립트파일을 common.js와 setUi.js로 분리하여 작업진행
> common.js는 최대한 ui,이벤트,페이지 이동관련 로직등
> setUi.js는 input,p등 컨텐츠의 데이터값 수정, container의 요소 토글 등의 동작구현

##### setui.js 설명

컨텐츠는 페이지 별로 분리하여 관리중입니다
페이지3은 단순 안내페이지 이기때문에 별도의 변수가 없습니다.

// 페이지 1 서비스 신청 = requestService1
// 페이지 2 서비스 신청정보 확인 = requestService2
// 페이지 4 심사후 결제관련 ui = payment
// 페이지 5 결제후 감사 페이지 = thankyou

컨텐츠요소 접근
예시)
requestService1.info.companyName

setValue & setText
// 텍스트를 세팅할때 사용 혹은 인풋값의 초기값을 세팅하기위해 사용
예시)
payment.setText("info", "companyName", "1");

getValue & getText
// 사용자가 입력한 값을 가져오거나, 텍스트값을 반환할때 사용
예시)
requestService1.getValue("manager", "name")

disableContainer
// 특정 컨테이너를 안보이도록 할때 사용
payment.disableContainer("startDate", true);

radioDisableAll
// 결제방식 선택 라디오버튼 전체를 disable하도록 변경하는 함수
payment.radioDisableAll(true);

// 결제방식 선택 라디오버튼을 disable하도록 변경하는 함수
payment.radioDisable("radioDeposit");

// 결제방식 라디오버튼을 선택하는함수
payment.radioSelect("radioDeposit");
