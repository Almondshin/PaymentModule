package com.modules.link.controller.dto;

import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.utils.Utils;
import lombok.Getter;

public class notifyDtos {

    @Getter
    public static class RegisterNotification {
        private final SiteId siteId;
        private final AgencyId agencyId;
        private final String siteName;

        public RegisterNotification(SiteId siteId, AgencyId agencyId, String siteName) {
            this.siteId = siteId;
            this.agencyId = agencyId;
            this.siteName = siteName;
        }

        public String makeNotification() {
            return Utils.objectToJSONString(this);
        }
    }

    // 현재 미사용 결제 시 사용할 폼으로 변경 예정
    public static class CancelNotification {
        private SiteId siteId;
        private AgencyId agencyId;


        public String makeNotification() {
            return Utils.objectToJSONString(this);
        }
    }


}
