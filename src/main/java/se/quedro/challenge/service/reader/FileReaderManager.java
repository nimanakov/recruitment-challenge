package se.quedro.challenge.service.reader;

public interface FileReaderManager {

    <T> T read(String filePath, Class<T> clazz);
}
