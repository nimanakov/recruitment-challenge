package se.quedro.challenge.service.partner.impl;

import com.google.inject.Inject;
import se.quedro.challenge.data.sales.SaleObject;
import se.quedro.challenge.data.sales.SaleObjects;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.partner.SaleObjectParserService;
import se.quedro.challenge.service.reader.FileReaderManager;
import se.quedro.challenge.service.reader.impl.FileReaderManagerImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class SaleObjectParserServiceImpl implements SaleObjectParserService {

    private final FileReaderManager fileReaderManager;

    @Inject
    public SaleObjectParserServiceImpl(final FileReaderManager fileReaderManager) {
        this.fileReaderManager = fileReaderManager;
    }

    @Override
    public List<SaleObject> parse(final String[] filePaths) {
        validateInputFiles(filePaths);

        return Arrays.stream(filePaths)
                .map(filePath -> fileReaderManager.read(filePath, SaleObjects.class))
                .map(SaleObjects::getSaleObjects)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void validateInputFiles(final String[] filePaths) {
        for (final String filePath : filePaths) {
            final Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                throw new ChallengeException("Provided path " + filePath + " does not exist");
            }
            if (Files.isDirectory(path)) {
                throw new ChallengeException("Provided path " + filePath + " is not a file");
            }
        }
    }
}
