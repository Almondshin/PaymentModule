package com.modules.link.domain.agency;


import com.modules.base.domain.Repository;

public interface AgencyRepository extends Repository<Agency, SiteId> {
    Agency find(SiteId siteId);
}
