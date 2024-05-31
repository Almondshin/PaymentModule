package com.modules.link.domain.agency;

import com.modules.base.domain.DomainEntity;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.utils.AuthUtils;
import com.modules.link.utils.SecurityUtils;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Table(name = "AGENCY_INFO_KEY")
public class AgencyKey extends DomainEntity<AgencyKey, AgencyId> {

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

    public Optional<EnumResultCode> validateHmacAndMsgType(String receivedMessageType, String encryptedData, String verifyInfo) {
        if (!AuthUtils.verifyHmacSHA256(originalMessage(encryptedData), verifyInfo, keyString())) {
            return Optional.of(EnumResultCode.HmacError);
        }
        if (!AuthUtils.verifyMessageType(receivedMessageType, keyString())){
            return Optional.of(EnumResultCode.MsgTypeError);
        }
        return Optional.empty();
    }

    public String originalMessage(String encryptData) {
        return new String(SecurityUtils.decryptData(encryptData, this.agencyKey, this.agencyIv));
    }

    public String encryptData(String targetEncode) {
        return SecurityUtils.encryptData(targetEncode, this.agencyKey, this.agencyIv);
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