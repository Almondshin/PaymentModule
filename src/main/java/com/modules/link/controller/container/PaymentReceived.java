package com.modules.link.controller.container;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
public class PaymentReceived {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @NotBlank(message = "Agency ID는 필수값 입니다.")
    private String agencyId;

    @NotBlank(message = "Site ID는 필수값 입니다.")
    @Size(max = 10, message = "Site ID는 10자리 이하 여야 합니다.")
    private String siteId;

    private String rateSel;
    private String startDate;


    public LocalDate getStartDate(){
        return LocalDate.parse(this.startDate, DATE_FORMATTER);
    }
}
