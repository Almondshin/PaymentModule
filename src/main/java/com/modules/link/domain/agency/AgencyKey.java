package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.modules.base.domain.DomainEntity;
import com.modules.link.domain.payment.Product;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "AGENCY_INFO_KEY")
public class AgencyKey extends AggregateRoot<AgencyKey, AgencyId> {

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

    @Getter
    @Column(name = "AGENCY_PRODUCT_TYPE")
    private String productList;
    @Getter
    @Column(name = "BILLING_BASE")
    private String billingBase;

    @Getter
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENCY_ID", insertable = false, updatable = false)
    private List<Product> products = new ArrayList<>();

//    //      ManyToOne테스트용도
//    @Getter
//    @BatchSize(size = 1000)
//    @OneToMany(mappedBy = "agencyKey", fetch = FetchType.EAGER)
//    private Set<Product> products;

    public String keyString() {
        return this.id.toString();
    }

    public String getKey() {
        return this.agencyKey;
    }

    public String getIv() {
        return this.agencyIv;
    }

    public List<String> getActiveProductList() {
        return Arrays.stream(productList.split(","))
                .collect(Collectors.toList());
    }


}