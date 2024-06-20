package com.modules.link.infrastructure.hectofinancial.api;

import com.modules.link.infrastructure.hectofinancial.api.mapper.NotiMapper;
import com.modules.link.infrastructure.hectofinancial.dto.HFDtos.*;
import com.modules.link.infrastructure.hectofinancial.service.HFService;
import com.modules.link.application.service.agency.AgencyService;
import com.modules.link.infrastructure.hectofinancial.service.HectoFinancialAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping(value = {"/agency/payment/api/result", "/payment/api/result"})
public class HFController {

    private final HFService hfService;
    private final AgencyService agencyService;
    private final HectoFinancialAdapter hectoFinancialAdapter;

    public HFController(HFService hfService, AgencyService agencyService, HectoFinancialAdapter hectoFinancialAdapter) {
        this.hfService = hfService;
        this.agencyService = agencyService;
        this.hectoFinancialAdapter = hectoFinancialAdapter;
    }

    @PostMapping(value = "/noti")
    public String requestNotification(HttpServletRequest request) {
        Set<String> keySet = request.getParameterMap().keySet();
        for (String key : keySet) {
            System.out.println("[Noti] : " + key + ": " + request.getParameter(key));
        }

        String method = request.getParameter("method");
        if ("CA".equals(method)) {
            NotiCADto notiCADto = NotiMapper.buildNotiCADto(request);
            return hectoFinancialAdapter.notiCAData(notiCADto);
        } else if ("VA".equals(method)) {
            NotiVADto notiVADto = NotiMapper.buildNotiVADto(request);
            return hectoFinancialAdapter.notiVAData(notiVADto);
        }

        return "FAIL";
    }
}
