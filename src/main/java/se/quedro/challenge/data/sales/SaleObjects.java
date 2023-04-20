package se.quedro.challenge.data.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleObjects {

    private final List<SaleObject> saleObjects;

    @JsonCreator
    public SaleObjects(@JsonProperty("saleObject") final List<SaleObject> saleObjects) {
        this.saleObjects = saleObjects;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SaleObject")
    public List<SaleObject> getSaleObjects() {
        return saleObjects;
    }
}
