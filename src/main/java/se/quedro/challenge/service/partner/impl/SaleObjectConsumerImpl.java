package se.quedro.challenge.service.partner.impl;

import se.quedro.challenge.service.partner.SaleObjectConsumer;

public class SaleObjectConsumerImpl implements SaleObjectConsumer {
    @Override
    public PriorityOrderAttribute getPriorityOrderAttribute() {
        return PriorityOrderAttribute.CITY;
    }

    @Override
    public void startSaleObjectTransaction() {
        // transaction implementation
    }

    @Override
    public void reportSaleObject(int squareMeters, String pricePerSquareMeter, String city, String street, Integer floor) throws TechnicalException {
        try {
            Thread.sleep(100L); // simulate reporting
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitSaleObjectTransaction() {
        // commit implementation
    }
}
