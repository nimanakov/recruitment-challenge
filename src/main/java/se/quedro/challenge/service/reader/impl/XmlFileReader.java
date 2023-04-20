package se.quedro.challenge.service.reader.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.reader.FileReader;

import java.io.IOException;
import java.io.InputStream;


public class XmlFileReader implements FileReader {

    private static final XmlMapper XML_OBJECT_MAPPER = XmlMapper.builder()
            .build();

    @Override
    public <T> T read(final InputStream inputStream, final Class<T> clazz) {
        try {
            return XML_OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (final IOException ex) {
            throw new ChallengeException(ex.getMessage());
        }
    }
}
