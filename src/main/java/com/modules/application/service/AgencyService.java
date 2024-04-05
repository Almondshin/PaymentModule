package com.modules.application.service;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.application.domain.*;
import com.modules.application.domain.model.*;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.NotFoundProductsException;
import com.modules.application.exceptions.exceptions.NullAgencyIdSiteIdException;
import com.modules.application.port.in.AgencyUseCase;
import com.modules.application.port.out.load.LoadAgencyDataPort;
import com.modules.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.application.port.out.load.LoadEncryptDataPort;
import com.modules.application.port.out.save.SaveAgencyDataPort;
import com.modules.domain.*;
import com.modules.domain.model.*;
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
    public void registerAgency(ClientDataContainer clientDataContainer) {
        if (clientDataContainer.getAgencyId() == null || clientDataContainer.getAgencyId().isEmpty() || clientDataContainer.getSiteId() == null || clientDataContainer.getSiteId().isEmpty()) {
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        ClientDataContainer checkAgencyId = new ClientDataContainer(clientDataContainer.getAgencyId(), clientDataContainer.getSiteId());
        saveAgencyDataPort.registerAgency(convertAgency(checkAgencyId), convertClient(clientDataContainer), convertSettleManager(clientDataContainer));
    }

    @Override
    public Optional<ClientDataContainer> getAgencyInfo(ClientDataContainer clientDataContainer) {
        if (clientDataContainer.getAgencyId() == null || clientDataContainer.getAgencyId().isEmpty() || clientDataContainer.getSiteId() == null || clientDataContainer.getSiteId().isEmpty()) {
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        return loadAgencyDataPort.getAgencyInfo(convertAgency(clientDataContainer), convertClient(clientDataContainer));
    }

    @Override
    public List<ClientDataContainer> selectAgencyInfo() {
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
//
//    private Agency convertAgency(ClientDataContainer clientDataContainer) {
//        return new Agency(clientDataContainer.getAgencyId(), clientDataContainer.getSiteId());
//    }
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
