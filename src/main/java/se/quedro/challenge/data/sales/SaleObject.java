package se.quedro.challenge.data.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.math.RoundingMode;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleObject {

    private final SaleObjectType type;

    private final Long id;

    private final Integer sizeSqm;

    private final BigDecimal startingPrice;

    private final Address address;

    private final BigDecimal pricePerSqm;

    @JsonCreator
    public SaleObject(@JsonProperty("type") final String type,
                      @JsonProperty("id") final Long id,
                      @JsonProperty("sizeSqm") final Integer sizeSqm,
                      @JsonProperty("startingPrice")
                      @JsonDeserialize(using = StartingPriceDeserializer.class) final BigDecimal startingPrice,
                      @JsonProperty("postalAddress") final Address address) {
        this.type = SaleObjectType.valueOf(type.toUpperCase());
        this.id = id;
        this.sizeSqm = sizeSqm;
        this.startingPrice = startingPrice;
        this.address = address;

        this.pricePerSqm = startingPrice.setScale(10, RoundingMode.UNNECESSARY).divide(BigDecimal.valueOf(sizeSqm), RoundingMode.UP);
    }

    public SaleObjectType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public Integer getSizeSqm() {
        return sizeSqm;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public Address getAddress() {
        return address;
    }

    public BigDecimal getPricePerSqm() {
        return pricePerSqm;
    }

    public enum SaleObjectType {
        APT,
        HOUSE
    }
}
