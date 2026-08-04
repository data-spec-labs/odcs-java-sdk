package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Pricing information for the data product / contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Pricing(
        @JsonProperty("priceAmount") BigDecimal priceAmount,
        @JsonProperty("priceCurrency") String priceCurrency,
        @JsonProperty("priceUnit") String priceUnit
) {
    @JsonCreator
    public Pricing {
    }
}
