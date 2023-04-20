package se.quedro.challenge.service.reader;


import com.fasterxml.jackson.databind.JavaType;

import java.io.InputStream;
import java.util.List;


public interface FileReader {

    <T> T read(InputStream inputStream, Class<T> clazz);
}
