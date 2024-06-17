package com.modules.link.domain.payment;

import com.modules.link.domain.agency.SiteId;

import java.io.Serializable;
import java.util.Objects;

public class StatDayCompositeId implements Serializable {

    private SiteId id;
    private String fromDate;

    public StatDayCompositeId() {
    }

    public StatDayCompositeId(SiteId siteId, String fromDate) {
        this.id = siteId;
        this.fromDate = fromDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatDayCompositeId that = (StatDayCompositeId) o;
        return Objects.equals(id, that.id) && Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromDate);
    }
}
