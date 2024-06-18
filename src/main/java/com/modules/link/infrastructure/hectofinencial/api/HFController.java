package com.modules.link.infrastructure.hectofinencial.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/agency/payment/api/result", "/payment/api/result"})
public class HFController {


    @PostMapping(value = "/noti")
    public String requestNotification(){
        return "";
    }
}
