package se.quedro.challenge.service.reader.impl;

import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.reader.FileReader;
import se.quedro.challenge.service.reader.FileReaderManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class FileReaderManagerImpl implements FileReaderManager {

    private final Pattern FILE_EXTENSION_PATTERN = Pattern.compile(".*\\.(\\w+)$");

    @Override
    public <T> T read(final String filePath, final Class<T> clazz) {
        final FileReader fileReader = getFileReader(filePath);

        try (final InputStream inputStream = Files.newInputStream(Path.of(filePath))) {
            return fileReader.read(inputStream, clazz);
        } catch (final IOException ex) {
            throw new ChallengeException(ex.getMessage()); // should not happen
        }
    }

    private FileReader getFileReader(final String filePath) {
        final Matcher matcher = FILE_EXTENSION_PATTERN.matcher(filePath);

        if (matcher.matches()) {
            final String fileExtension = matcher.group(1);

            if (FileType.FILE_READER_BY_FILE_EXTENSION.containsKey(fileExtension)) {
                return FileType.FILE_READER_BY_FILE_EXTENSION.get(fileExtension);
            }

            final String supportedFileExtensions = String.join(", ", FileType.FILE_READER_BY_FILE_EXTENSION.keySet());
            throw new ChallengeException(String.format("Unsupported file extension %s provided for file path %s. Supported file " +
                    "extensions are %s", fileExtension, filePath, String.join(", ", supportedFileExtensions)));
        }

        throw new ChallengeException("File extension is not provided for file path " + filePath);
    }

    private enum FileType {
        JSON("json", new JsonFileReader()),
        XML("xml", new XmlFileReader());

        private final static Map<String, FileReader> FILE_READER_BY_FILE_EXTENSION = Arrays.stream(values())
                .collect(Collectors.toMap(fileType -> fileType.fileExtension, fileType -> fileType.fileReader));

        private final String fileExtension;

        private final FileReader fileReader;

        FileType(final String fileExtension, final FileReader fileReader) {
            this.fileExtension = fileExtension;
            this.fileReader = fileReader;
        }
    }
}
