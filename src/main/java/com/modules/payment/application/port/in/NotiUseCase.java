package com.modules.payment.application.port.in;

public interface NotiUseCase {
     String sendNotification(String targetUrl, String responseData);
     String getAgencyUrlByAgencyInfoKey(String agencyId, String type);
}
