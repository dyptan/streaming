package com.dyptan.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import javax.persistence.Transient;

@Embeddable
@Table(name = "Filters")
public class Filter {

    private @Transient short limit = 100;
    private String models ;
    private String brands ;
    private @Transient Period periodMultiplier ;
    private @Transient int periodRange ;
    private int yearFrom ;
    private int yearTo ;
    private int priceFrom;
    private int priceTo ;

    public Filter() {
    }

    @Override
    public String toString(){
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    public short getLimit() {
        return this.limit;
    }

    public String getModels() {
        return this.models;
    }

    public String getBrands() {
        return this.brands;
    }

    public Period getPeriodMultiplier() {
        return this.periodMultiplier;
    }

    public int getPeriodRange() {
        return this.periodRange;
    }

    public int getYearFrom() {
        return this.yearFrom;
    }

    public int getYearTo() {
        return this.yearTo;
    }

    public int getPriceFrom() {
        return this.priceFrom;
    }

    public int getPriceTo() {
        return this.priceTo;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    public void setModels(String models) {
        this.models = models;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public void setPeriodMultiplier(Period periodMultiplier) {
        this.periodMultiplier = periodMultiplier;
    }

    public void setPeriodRange(int periodRange) {
        this.periodRange = periodRange;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void setPriceFrom(int priceFrom) {
        this.priceFrom = priceFrom;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Filter)) return false;
        final Filter other = (Filter) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getLimit() != other.getLimit()) return false;
        final Object this$models = this.getModels();
        final Object other$models = other.getModels();
        if (this$models == null ? other$models != null : !this$models.equals(other$models)) return false;
        final Object this$brands = this.getBrands();
        final Object other$brands = other.getBrands();
        if (this$brands == null ? other$brands != null : !this$brands.equals(other$brands)) return false;
        final Object this$periodMultiplier = this.getPeriodMultiplier();
        final Object other$periodMultiplier = other.getPeriodMultiplier();
        if (this$periodMultiplier == null ? other$periodMultiplier != null : !this$periodMultiplier.equals(other$periodMultiplier))
            return false;
        if (this.getPeriodRange() != other.getPeriodRange()) return false;
        if (this.getYearFrom() != other.getYearFrom()) return false;
        if (this.getYearTo() != other.getYearTo()) return false;
        if (this.getPriceFrom() != other.getPriceFrom()) return false;
        if (this.getPriceTo() != other.getPriceTo()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Filter;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getLimit();
        final Object $models = this.getModels();
        result = result * PRIME + ($models == null ? 43 : $models.hashCode());
        final Object $brands = this.getBrands();
        result = result * PRIME + ($brands == null ? 43 : $brands.hashCode());
        final Object $periodMultiplier = this.getPeriodMultiplier();
        result = result * PRIME + ($periodMultiplier == null ? 43 : $periodMultiplier.hashCode());
        result = result * PRIME + this.getPeriodRange();
        result = result * PRIME + this.getYearFrom();
        result = result * PRIME + this.getYearTo();
        result = result * PRIME + this.getPriceFrom();
        result = result * PRIME + this.getPriceTo();
        return result;
    }

    public enum Period{
        DAYS ("d"),
        WEEKS("w"),
        MONTHS("M");

        private final String abbreviation;

        Period(String abbreviation){
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {return abbreviation;}
    }
}


