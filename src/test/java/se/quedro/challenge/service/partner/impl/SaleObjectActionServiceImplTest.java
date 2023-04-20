package se.quedro.challenge.service.partner.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import se.quedro.challenge.data.sales.Address;
import se.quedro.challenge.data.sales.SaleObject;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.partner.SaleObjectActionService;
import se.quedro.challenge.service.partner.SaleObjectConsumer;
import se.quedro.challenge.service.partner.SaleObjectParserService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


class SaleObjectActionServiceImplTest {

    private final static SaleObject SALE_OBJECT1 = new SaleObject(SaleObject.SaleObjectType.APT.name(), 1L, 28,
            BigDecimal.valueOf(3905999), new Address("New York", "Crampedstreet 9A", 18));

    private final static SaleObject SALE_OBJECT2 = new SaleObject(SaleObject.SaleObjectType.APT.name(), 2L, 55,
            BigDecimal.valueOf(7805999), new Address("New York", "Crampedstreet 5", 18));

    private final static SaleObject SALE_OBJECT3 = new SaleObject(SaleObject.SaleObjectType.HOUSE.name(), 3L, 622,
            BigDecimal.valueOf(39904472.2), new Address("Helsinki", "Notticompriheensiple 5", null));

    private final static List<SaleObject> SALE_OBJECTS = List.of(SALE_OBJECT1, SALE_OBJECT2, SALE_OBJECT3);

    private final Map<Long, String> pricePerSqmBySaleObjectId = Map.of(
            SALE_OBJECT1.getId(), "139499964",
            SALE_OBJECT2.getId(), "141927255",
            SALE_OBJECT3.getId(), "64155100"
    );

    private final SaleObjectParserService saleObjectParserService = mock(SaleObjectParserService.class);

    private final SaleObjectConsumer saleObjectConsumer1 = mock(SaleObjectConsumer.class);

    private final SaleObjectConsumer saleObjectConsumer2 = mock(SaleObjectConsumer.class);

    private final InOrder inOrder1 = inOrder(saleObjectConsumer1);

    private final InOrder inOrder2 = inOrder(saleObjectConsumer2);

    private final SaleObjectActionServiceImpl.SaleObjectConsumerHolder saleObjectConsumerHolder =
            mock(SaleObjectActionServiceImpl.SaleObjectConsumerHolder.class);

    private SaleObjectActionService saleObjectActionService;

    @BeforeEach
    void setUp() {
        saleObjectConsumerHolder.SALE_OBJECT_CONSUMERS = Set.of(saleObjectConsumer1, saleObjectConsumer2);
        saleObjectActionService = new SaleObjectActionServiceImpl(saleObjectParserService, saleObjectConsumerHolder);

        doReturn(SALE_OBJECTS).when(saleObjectParserService).parse(any());
        doReturn(SaleObjectConsumer.PriorityOrderAttribute.CITY).when(saleObjectConsumer1).getPriorityOrderAttribute();
        doReturn(SaleObjectConsumer.PriorityOrderAttribute.PRICE_PER_SQUARE_METER).when(saleObjectConsumer2).getPriorityOrderAttribute();
        doNothing().when(saleObjectConsumer1).reportSaleObject(any(int.class), any(String.class), any(String.class), any(String.class),
                any(Integer.class));
        doNothing().when(saleObjectConsumer2).reportSaleObject(any(int.class), any(String.class), any(String.class), any(String.class),
                any(Integer.class));
    }

    @Test
    void sendToPartners_ok() {
        saleObjectActionService.sendToPartners(new String[] {"./someFileName.xml"});

        verify(saleObjectConsumer1).startSaleObjectTransaction();
        verify(saleObjectConsumer2).startSaleObjectTransaction();

        inOrder1.verify(saleObjectConsumer1).reportSaleObject(eq(SALE_OBJECT3.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT3.getId())), eq(SALE_OBJECT3.getAddress().getCity()),
                eq(SALE_OBJECT3.getAddress().getStreet()), eq(SALE_OBJECT3.getAddress().getFloor()));

        inOrder1.verify(saleObjectConsumer1).reportSaleObject(eq(SALE_OBJECT2.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT2.getId())), eq(SALE_OBJECT2.getAddress().getCity()),
                eq(SALE_OBJECT2.getAddress().getStreet()), eq(SALE_OBJECT2.getAddress().getFloor()));

        inOrder1.verify(saleObjectConsumer1).reportSaleObject(eq(SALE_OBJECT1.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT1.getId())), eq(SALE_OBJECT1.getAddress().getCity()),
                eq(SALE_OBJECT1.getAddress().getStreet()), eq(SALE_OBJECT1.getAddress().getFloor()));

        inOrder2.verify(saleObjectConsumer2).reportSaleObject(eq(SALE_OBJECT3.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT3.getId())), eq(SALE_OBJECT3.getAddress().getCity()),
                eq(SALE_OBJECT3.getAddress().getStreet()), eq(SALE_OBJECT3.getAddress().getFloor()));

        inOrder2.verify(saleObjectConsumer2).reportSaleObject(eq(SALE_OBJECT1.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT1.getId())), eq(SALE_OBJECT1.getAddress().getCity()),
                eq(SALE_OBJECT1.getAddress().getStreet()), eq(SALE_OBJECT1.getAddress().getFloor()));

        inOrder2.verify(saleObjectConsumer2).reportSaleObject(eq(SALE_OBJECT2.getSizeSqm()),
                eq(pricePerSqmBySaleObjectId.get(SALE_OBJECT2.getId())), eq(SALE_OBJECT2.getAddress().getCity()),
                eq(SALE_OBJECT2.getAddress().getStreet()), eq(SALE_OBJECT2.getAddress().getFloor()));

        verify(saleObjectConsumer1).commitSaleObjectTransaction();
        verify(saleObjectConsumer2).commitSaleObjectTransaction();
    }

    @Test
    void sendToPartners_timeoutOnReport() {
        doAnswer((ignored) -> {
            Thread.sleep(SaleObjectActionServiceImpl.TIMEOUT_IN_SEC + 1_000L);
            return null;
        }).when(saleObjectConsumer1).reportSaleObject(any(int.class), any(String.class), any(String.class), any(String.class),
                any(Integer.class));

        assertThrows(ChallengeException.class, () -> saleObjectActionService.sendToPartners(new String[] {"./someFileName.xml"}));

        verify(saleObjectConsumer1, times(0)).commitSaleObjectTransaction();
        verify(saleObjectConsumer2, times(0)).commitSaleObjectTransaction();
    }

    @Test
    void sendToPartners_reportRaisesException() {
        doThrow(SaleObjectConsumer.TechnicalException.class).when(saleObjectConsumer1).reportSaleObject(any(int.class), any(String.class),
                any(String.class), any(String.class), any(Integer.class));

        assertThrows(ChallengeException.class, () -> saleObjectActionService.sendToPartners(new String[] {"./someFileName.xml"}));

        verify(saleObjectConsumer1, times(0)).commitSaleObjectTransaction();
        verify(saleObjectConsumer2, times(0)).commitSaleObjectTransaction();
    }

    @Test
    void sendToPartners_noSaleObjects() {
        doReturn(emptyList()).when(saleObjectParserService).parse(any());

        saleObjectActionService.sendToPartners(new String[] {"./someFileName.xml"});

        verifyZeroInteractions(saleObjectConsumer1, saleObjectConsumer2);
    }

    @Test
    void sendToPartners_noConsumers() {
        saleObjectConsumerHolder.SALE_OBJECT_CONSUMERS = Collections.emptySet();
        saleObjectActionService = new SaleObjectActionServiceImpl(saleObjectParserService, saleObjectConsumerHolder);

        saleObjectActionService.sendToPartners(new String[] {"./someFileName.xml"});

        verifyZeroInteractions(saleObjectParserService, saleObjectConsumer1, saleObjectConsumer2);
    }
}