package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.Pricing;

import java.math.BigDecimal;

/**
 * Fluent builder for {@link Pricing}.
 */
public final class PricingBuilder {

    private BigDecimal priceAmount;
    private String priceCurrency;
    private String priceUnit;

    public PricingBuilder priceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
        return this;
    }

    public PricingBuilder priceAmount(double priceAmount) {
        this.priceAmount = BigDecimal.valueOf(priceAmount);
        return this;
    }

    public PricingBuilder priceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
        return this;
    }

    public PricingBuilder priceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
        return this;
    }

    public Pricing build() {
        return new Pricing(priceAmount, priceCurrency, priceUnit);
    }
}
