package se.quedro.challenge.data.sales;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import se.quedro.challenge.exception.ChallengeException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;


public class StartingPriceDeserializer extends JsonDeserializer {

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setGroupingSeparator('.');
        DECIMAL_FORMAT.setGroupingUsed(true);
        DECIMAL_FORMAT.setDecimalFormatSymbols(dfs);
    }

    @Override
    public Object deserialize(final JsonParser p,
                              final DeserializationContext ctxt) throws IOException, JacksonException {
        final String value = p.getValueAsString();

        try {
            return new BigDecimal(DECIMAL_FORMAT.parse(value).doubleValue());
        } catch (final ParseException ex) {
            throw new ChallengeException(ex.getMessage());
        }
    }
}
