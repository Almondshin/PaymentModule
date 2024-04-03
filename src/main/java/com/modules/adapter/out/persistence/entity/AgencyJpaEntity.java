package com.modules.adapter.out.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "AGENCY_INFO")
@Data
public class AgencyJpaEntity {

	@Id
	@Column(name = "SITE_ID")
	private String siteId;

	@NotBlank
	@Column(name = "AGENCY_ID", nullable = false)
	private String agencyId;

	@Column(name = "SITE_NAME")
	private String siteName;
	@Column(name = "COMPANY_NAME")
	private String companyName;
	@Column(name = "BUSINESS_TYPE")
	private String businessType;
	@NotBlank
	@Column(name = "BIZ_NUMBER", nullable = false)
	private String bizNumber;
	@Column(name = "CEO_NAME")
	private String ceoName;
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	@Column(name = "ADDRESS")
	private String address;
	@Column(name = "COMPANY_SITE")
	private String companySite;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "RATE_SEL")
	private String rateSel;
	@Column(name = "SCHEDULED_RATE_SEL")
	private String scheduledRateSel;
	@Column(name = "SITE_STATUS")
	private String siteStatus;

	@Column(name = "EXTENSION_STATUS")
	private String extensionStatus;
	@Column(name = "EXCESS_COUNT")
	private String excessCount;

	@Column(name = "START_DATE")
	private Date startDate;
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "SETTLE_MANAGER_NAME")
	private String settleManagerName;
	@Column(name = "SETTLE_MANAGER_PHONE_NUMBER")
	private String settleManagerPhoneNumber;
	@Column(name = "SETTLE_MANAGER_TEL_NUMBER")
	private String settleManagerTelNumber;
	@Column(name = "SETTLE_MANAGER_EMAIL")
	private String settleManagerEmail;

	@Column(name = "SERVICE_USE_AGREE")
	private String serviceUseAgree;
}
