package com.modules.link.domain.agency;

import com.modules.base.domain.DomainEntity;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "AGENCY_INFO_KEY")
public class AgencyKey extends DomainEntity<AgencyKey, AgencyId> {

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID")
    private AgencyId id;

    @Column(name = "AGENCY_KEY")
    private String agencyKey;
    @Column(name = "AGENCY_IV")
    private String agencyIv;

    @Column(name = "AGENCY_URL")
    private String agencyUrl;
    @Column(name = "AGENCY_PRODUCT_TYPE")
    private String productList;
    @Column(name = "BILLING_BASE")
    private String billingBase;


    public String keyString() {
        return this.id.toString();
    }

    public String getKey() {
        return this.agencyKey;
    }

    public String getIv() {
        return this.agencyIv;
    }

//    public List<String> getProductList(String agencyId) {
//        List<String> productList = new ArrayList<>();
//        for (String product : this.productList.split(",")) {
//            if (product.startsWith(agencyId)) {
//                productList.add(product);
//            }
//        }
//        return productList;
//    }


//    public String getAgencyURL(String type) {
//        Map<String, String> agencyMap = Utils.jsonStringToObject(this.agencyUrl, Map.class);
//        if (agencyMap == null || agencyMap.isEmpty()) {
//            throw new IllegalStateException("Agency URL is null or empty");
//        }
//        switch (type) {
//            case "PAYMENT":
//                return agencyMap.get("NotifyPaymentSiteInfo");
//            case "STATUS":
//                return agencyMap.get("NotifyStatusSite");
//            default:
//                throw new IllegalStateException("'" + type + "'는 존재하지 않는 타입 입니다.");
//        }
//    }


}