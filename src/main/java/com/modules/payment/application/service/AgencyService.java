package com.modules.payment.application.service;

import com.modules.application.domain.model.*;
import com.modules.domain.*;
import com.modules.domain.model.*;
import com.modules.payment.domain.Agency;
import com.modules.payment.application.domain.AgencyInfoKey;
import com.modules.payment.application.domain.AgencyProducts;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.NotFoundProductsException;
import com.modules.payment.application.exceptions.exceptions.NullAgencyIdSiteIdException;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.out.load.LoadAgencyDataPort;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AgencyService implements AgencyUseCase {
    private final LoadAgencyDataPort loadAgencyDataPort;
    private final SaveAgencyDataPort saveAgencyDataPort;
    private final LoadEncryptDataPort loadEncryptDataPort;
    private final LoadAgencyProductDataPort loadAgencyProductDataPort;

    public AgencyService(LoadAgencyDataPort loadAgencyDataPort, SaveAgencyDataPort saveAgencyDataPort, LoadEncryptDataPort loadEncryptDataPort, LoadAgencyProductDataPort loadAgencyProductDataPort) {
        this.loadAgencyDataPort = loadAgencyDataPort;
        this.saveAgencyDataPort = saveAgencyDataPort;
        this.loadEncryptDataPort = loadEncryptDataPort;
        this.loadAgencyProductDataPort = loadAgencyProductDataPort;
    }

    @Override
    public void registerAgency(Agency agency) {
        String nullField = agency.checkRequiredFields();
        if (nullField != null) {
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }

        saveAgencyDataPort.registerAgency(convertAgency(checkAgencyId), convertClient(agency), convertSettleManager(agency));
    }

    @Override
    public Optional<Agency> getAgencyInfo(Agency agency) {
        if (agency.getAgencyId() == null || agency.getAgencyId().isEmpty() || agency.getSiteId() == null || agency.getSiteId().isEmpty()) {
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        return loadAgencyDataPort.getAgencyInfo(convertAgency(agency), convertClient(agency));
    }

    @Override
    public List<Agency> selectAgencyInfo() {
        return loadAgencyDataPort.selectAgencyInfo();
    }

    @Override
    public List<Map<String, String>> getProductTypes(String agencyId) {
        Optional<AgencyInfoKey> optAgencyInfoKey = loadEncryptDataPort.getAgencyInfoKey(agencyId);
        List<Map<String, String>> productsList = new ArrayList<>();

        if (optAgencyInfoKey.isPresent()) {
            AgencyInfoKey agencyInfoKey = optAgencyInfoKey.get();
            String[] productTypes = agencyInfoKey.getAgencyProductType().split(",");
            System.out.println(Arrays.toString(productTypes));
            for (String productType : productTypes) {
                Map<String, String> enumData = new HashMap<>();
                AgencyProducts agencyProducts = loadAgencyProductDataPort.getAgencyProductByRateSel(productType);
                if(agencyProducts == null){
                    System.out.println("제휴사에 상품이 등록되지 않았습니다 : " +Arrays.toString(productTypes) + " 제휴사 관리 상품 리스트 확인 필요!");
                    throw new NotFoundProductsException(EnumResultCode.ReadyProducts);
                }
                enumData.put("type", agencyProducts.getRateSel());
                enumData.put("name", agencyProducts.getName());
                enumData.put("price", agencyProducts.getPrice());
                enumData.put("basicOffer", agencyProducts.getOffer());
                enumData.put("month", agencyProducts.getMonth());
                enumData.put("feePerCase", agencyProducts.getFeePerCase());
                enumData.put("excessFeePerCase", agencyProducts.getExcessPerCase());
                productsList.add(enumData);
            }
        }
        return productsList;
    }

    private com.modules.payment.application.domain.Agency convertAgency(Agency agency) {
        return new com.modules.payment.application.domain.Agency(agency.getAgencyId(), agency.getSiteId());
    }
//
//    private Client convertClient(ClientDataContainer clientDataContainer) {
//        return new Client(
//                clientDataContainer.getSiteName(),
//                clientDataContainer.getCompanyName(),
//                clientDataContainer.getBusinessType(),
//                clientDataContainer.getBizNumber(),
//                clientDataContainer.getCeoName(),
//                clientDataContainer.getPhoneNumber(),
//                clientDataContainer.getAddress(),
//                clientDataContainer.getCompanySite(),
//                clientDataContainer.getEmail(),
//                clientDataContainer.getRateSel(),
//                clientDataContainer.getScheduledRateSel(),
//                clientDataContainer.getSiteStatus(),
//                clientDataContainer.getExtensionStatus(),
//                clientDataContainer.getStartDate(),
//                clientDataContainer.getEndDate(),
//                clientDataContainer.getServiceUseAgree()
//        );
//    }
//
//    private SettleManager convertSettleManager(ClientDataContainer clientDataContainer) {
//        return new SettleManager(
//                clientDataContainer.getSettleManagerName(),
//                clientDataContainer.getSettleManagerPhoneNumber(),
//                clientDataContainer.getSettleManagerTelNumber(),
//                clientDataContainer.getSettleManagerEmail()
//        );
//    }




}
