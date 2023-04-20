package se.quedro.challenge.service.reader.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.reader.FileReader;

import java.io.IOException;
import java.io.InputStream;


public class JsonFileReader implements FileReader {

    private static final ObjectMapper JSON_OBJECT_MAPPER = JsonMapper.builder()
            .build();

    @Override
    public <T> T read(final InputStream inputStream, final Class<T> clazz) {
        try {
            return JSON_OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (final IOException ex) {
            throw new ChallengeException(ex.getMessage());
        }
    }
}
