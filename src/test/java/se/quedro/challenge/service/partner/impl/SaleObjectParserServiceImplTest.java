package se.quedro.challenge.service.partner.impl;

import org.junit.jupiter.api.Test;
import se.quedro.challenge.data.sales.Address;
import se.quedro.challenge.data.sales.SaleObject;
import se.quedro.challenge.data.sales.SaleObjects;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.partner.SaleObjectParserService;
import se.quedro.challenge.service.reader.FileReaderManager;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


class SaleObjectParserServiceImplTest {

    private final static SaleObject SALE_OBJECT1 = new SaleObject(SaleObject.SaleObjectType.APT.name(), 1L, 28,
            BigDecimal.valueOf(3905999), new Address("New York", "Crampedstreet 9A", 18));

    private final static SaleObject SALE_OBJECT2 = new SaleObject(SaleObject.SaleObjectType.APT.name(), 2L, 55,
            BigDecimal.valueOf(7805999), new Address("New York", "Crampedstreet 5", 18));

    private final static SaleObject SALE_OBJECT3 = new SaleObject(SaleObject.SaleObjectType.HOUSE.name(), 3L, 622,
            BigDecimal.valueOf(39904472.2), new Address("Helsinki", "Notticompriheensiple 5", null));

    private final FileReaderManager fileReaderManager = mock(FileReaderManager.class);

    private final SaleObjectParserService saleObjectParserService = new SaleObjectParserServiceImpl(fileReaderManager);

    @Test
    void parse_ok() {
        final SaleObjects saleObjects1 = new SaleObjects(List.of(SALE_OBJECT1, SALE_OBJECT2));
        final SaleObjects saleObjects2 = new SaleObjects(List.of(SALE_OBJECT3));
        final String filePath1 = "./src/test/resources/someFile.xml";
        final String filePath2 = "./src/test/resources/someAnotherFIle.xml";

        doReturn(saleObjects1).when(fileReaderManager).read(eq(filePath1), eq(SaleObjects.class));
        doReturn(saleObjects2).when(fileReaderManager).read(eq(filePath2), eq(SaleObjects.class));

        final List<SaleObject> saleObjects = saleObjectParserService.parse(new String[] {filePath1, filePath2});

        assertEquals(3, saleObjects.size());
        final List<Long> saleObjectsIds = saleObjects.stream()
                .map(SaleObject::getId)
                .collect(Collectors.toList());
        assertTrue(saleObjectsIds.containsAll(List.of(SALE_OBJECT1.getId(), SALE_OBJECT2.getId(), SALE_OBJECT3.getId())));
    }

    @Test
    void parse_emptySaleObjects() {
        final String filePath1 = "./src/test/resources/someFile.xml";

        doReturn(new SaleObjects(Collections.emptyList())).when(fileReaderManager).read(eq(filePath1), eq(SaleObjects.class));

        final List<SaleObject> saleObjects = saleObjectParserService.parse(new String[] {filePath1});

        assertTrue(saleObjects.isEmpty());
    }

    @Test
    void parse_fileDoesNotExist() {
        assertThrows(ChallengeException.class,
                () -> saleObjectParserService.parse(new String[] {"./src/test/resources/nonExisting.xml"}));
    }

    @Test
    void parse_fileIsDirectory() {
        assertThrows(ChallengeException.class,
                () -> saleObjectParserService.parse(new String[] {"./src/test/resources/someFIle.json"}));
    }
}