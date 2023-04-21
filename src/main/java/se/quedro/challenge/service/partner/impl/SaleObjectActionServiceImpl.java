package se.quedro.challenge.service.partner.impl;

import com.google.inject.Inject;
import se.quedro.challenge.data.sales.SaleObject;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.partner.SaleObjectActionService;
import se.quedro.challenge.service.partner.SaleObjectConsumer;
import se.quedro.challenge.service.partner.SaleObjectParserService;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Can be enriched with other sale object actions.
 */
public class SaleObjectActionServiceImpl implements SaleObjectActionService {

    private final static DecimalFormat DECIMAL_FORMAT;

    private final static String DECIMAL_SEPARATOR;

    public final static Long TIMEOUT_IN_SEC = 3L;

    static {
        DECIMAL_FORMAT = new DecimalFormat();
        DECIMAL_FORMAT.setMinimumFractionDigits(3);
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_SEPARATOR = String.valueOf(DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator());
    }

    private final SaleObjectParserService saleObjectParserService;

    private final Set<SaleObjectConsumer> saleObjectConsumers;

    // number of threads depends on how many potential consumers and parallel executions there might be
    final ExecutorService executor = Executors.newFixedThreadPool(16);

    @Inject
    public SaleObjectActionServiceImpl(final SaleObjectParserService saleObjectParserService,
                                       final SaleObjectConsumerHolder saleObjectConsumerHolder) {
        this.saleObjectParserService = saleObjectParserService;
        this.saleObjectConsumers = saleObjectConsumerHolder.SALE_OBJECT_CONSUMERS;

        // same validation as ActionHelperServiceImpl's constructor one
        for (final SaleObjectConsumer.PriorityOrderAttribute orderAttribute : SaleObjectConsumer.PriorityOrderAttribute.values()) {
            getComparator(orderAttribute);
        }
    }

    @Override
    public void sendToPartners(final String[] input) {
        if (saleObjectConsumers.isEmpty()) {
            return;
        }

        final List<SaleObject> saleObjects = saleObjectParserService.parse(input);

        if (saleObjects.isEmpty()) {
            return;
        }

        /* for each consumer we either synchronize till sorting and reporting are finished so sale objects collection is not altered by
           other sale consumer threads or we copy all sale objects so they are reported in requested order

           both approaches are presented with restrictions either by memory or throughput so instead we prepare sale objects for all
           existing consumer's attribute orders before processing */
        final Map<SaleObjectConsumer.PriorityOrderAttribute, List<SaleObject>> saleObjectsByOrderAttribute = getSaleObjectsMap(saleObjects);

        try {
            final List<Callable<Void>> tasks = saleObjectConsumers.stream()
                    .map(saleObjectConsumer -> getTask(saleObjectConsumer, saleObjectsByOrderAttribute))
                    .collect(Collectors.toList());
            final List<Future<Void>> futures = executor.invokeAll(tasks, TIMEOUT_IN_SEC, TimeUnit.SECONDS);

            for (final Future<Void> future : futures) {
                future.get(0, TimeUnit.MILLISECONDS); // so exception is thrown in case worker thread was terminated
            }

            // I assume that we must report all sale objects to all partners or not report anything at all
            for (final SaleObjectConsumer saleObjectConsumer : saleObjectConsumers) {
                /* commit might end up with an exception but since no rollback interface is provided I assume it's handled on the client
                   side for the purpose of this task */
                saleObjectConsumer.commitSaleObjectTransaction();
            }
        } catch (final Exception ex) {
            if (ex.getCause() instanceof SaleObjectConsumer.TechnicalException) {
                throw new ChallengeException(((SaleObjectConsumer.TechnicalException) ex.getCause()).getReason());
            }
            throw new ChallengeException(ex.getMessage());
        } finally {
            /* I would not shutdown executor in real app but since it's a CLI app shutdown is required so all thread are terminated
               by the time main returns */
            executor.shutdown();
        }
    }

    private Callable<Void> getTask(final SaleObjectConsumer saleObjectConsumer,
                                   final Map<SaleObjectConsumer.PriorityOrderAttribute, List<SaleObject>> saleObjectsByOrderAttribute) {
        return () -> {
            saleObjectConsumer.startSaleObjectTransaction();

            final var orderedSaleObjects = saleObjectsByOrderAttribute.get(saleObjectConsumer.getPriorityOrderAttribute());

            orderedSaleObjects.forEach(saleObject -> {
                final String formattedPricePerSqm = DECIMAL_FORMAT.format(saleObject.getPricePerSqm()).replace(DECIMAL_SEPARATOR, "");

                saleObjectConsumer.reportSaleObject(saleObject.getSizeSqm(), formattedPricePerSqm,
                        saleObject.getAddress().getCity(), saleObject.getAddress().getStreet(), saleObject.getAddress().getFloor());
            });

            return null;
        };
    }

    private Comparator<SaleObject> getComparator(final SaleObjectConsumer.PriorityOrderAttribute orderAttribute) {
        final Comparator<SaleObject> comparator;
        switch (orderAttribute) {
            case CITY:
                comparator = Comparator.<SaleObject, String>comparing(obj -> obj.getAddress().getCity(), String::compareToIgnoreCase)
                        .thenComparing(obj -> obj.getAddress().getStreet(), String::compareToIgnoreCase);
                break;
            case SQUARE_METERS:
                comparator = Comparator.comparing(SaleObject::getSizeSqm).thenComparing(SaleObject::getStartingPrice);
                break;
            case PRICE_PER_SQUARE_METER:
                comparator = Comparator.comparing(SaleObject::getPricePerSqm);
                break;
            default:
                throw new IllegalArgumentException("Order implementation is not set for " + orderAttribute
                        + ". Contact your program provider");
        }

        return comparator;
    }

    private Map<SaleObjectConsumer.PriorityOrderAttribute, List<SaleObject>> getSaleObjectsMap(final List<SaleObject> saleObjects) {
        return saleObjectConsumers.stream()
                .map(SaleObjectConsumer::getPriorityOrderAttribute)
                .distinct()
                .collect(Collectors.toMap(Function.identity(), orderAttribute -> {
                    final List<SaleObject> sortedSaleObjects = new ArrayList<>(saleObjects);
                    sortedSaleObjects.sort(getComparator(orderAttribute));

                    return sortedSaleObjects;
                }));
    }

    public static class SaleObjectConsumerHolder {
        @Inject(optional = true)
        public Set<SaleObjectConsumer> SALE_OBJECT_CONSUMERS = Collections.emptySet();
    }
}
