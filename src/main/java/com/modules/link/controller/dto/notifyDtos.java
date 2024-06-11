package com.modules.link.controller.dto;

import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.utils.Utils;

public class notifyDtos {

    public static class RegisterNotification {
        private SiteId siteId;
        private AgencyId agencyId;
        private String siteName;

        public RegisterNotification(SiteId siteId, AgencyId agencyId, String siteName) {
            this.siteId = siteId;
            this.agencyId = agencyId;
            this.siteName = siteName;
        }

        public String makeNotification() {
            return Utils.objectToJSONString(this);
        }
    }

    public static class CancelNotification {
        private SiteId siteId;
        private AgencyId agencyId;

        public CancelNotification(SiteId siteId, AgencyId agencyId) {
            this.siteId = siteId;
            this.agencyId = agencyId;
        }

        public String makeNotification() {
            return Utils.objectToJSONString(this);
        }
    }


}
