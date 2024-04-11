package com.modules.payment.application.service;

import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.NotFoundProductsException;
import com.modules.payment.application.exceptions.exceptions.NullAgencyIdSiteIdException;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.out.load.LoadAgencyDataPort;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.AgencyInfoKey;
import com.modules.payment.domain.Product;
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
        saveAgencyDataPort.registerAgency(agency);
    }

    @Override
    public Optional<Agency> getAgencyInfo(Agency agency) {
        if (agency.validateIdFields()) {
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        return loadAgencyDataPort.getAgencyInfo(agency);
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
                Optional<Product> agencyProductsOpt = loadAgencyProductDataPort.getAgencyProductByRateSel(productType);
                Product agencyProducts = agencyProductsOpt.orElseThrow(() -> {
                    System.out.println("제휴사에 상품이 등록되지 않았습니다 : " + Arrays.toString(productTypes) + " 제휴사 관리 상품 리스트 확인 필요!");
                    return new NotFoundProductsException(EnumResultCode.ReadyProducts);
                });
                productsList.add(agencyProducts.productMap());
            }
        }
        return productsList;
    }
}
