package se.quedro.challenge.data.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    private final String city;

    private final String street;

    private final Integer floor;

    @JsonCreator
    public Address(@JsonProperty("city") final String city,
                   @JsonProperty("street") final String street,
                   @JsonProperty("floor") final Integer floor) {
        this.city = city;
        this.street = street;
        this.floor = floor;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public Integer getFloor() {
        return floor;
    }
}
