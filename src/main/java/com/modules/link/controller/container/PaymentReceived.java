package com.modules.link.controller.container;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
public class PaymentReceived {
    @NotBlank(message = "Agency ID는 필수값 입니다.")
    private String agencyId;

    @NotBlank(message = "Site ID는 필수값 입니다.")
    @Size(max = 10, message = "Site ID는 10자리 이하 여야 합니다.")
    private String siteId;

    private String rateSel;
    private String startDate;

}
