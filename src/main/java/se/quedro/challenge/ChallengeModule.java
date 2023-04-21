package se.quedro.challenge;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import se.quedro.challenge.service.action.ActionHelperService;
import se.quedro.challenge.service.action.impl.ActionHelperServiceImpl;
import se.quedro.challenge.service.partner.SaleObjectActionService;
import se.quedro.challenge.service.partner.SaleObjectConsumer;
import se.quedro.challenge.service.partner.SaleObjectParserService;
import se.quedro.challenge.service.partner.impl.SaleObjectActionServiceImpl;
import se.quedro.challenge.service.partner.impl.SaleObjectConsumerImpl;
import se.quedro.challenge.service.partner.impl.SaleObjectParserServiceImpl;
import se.quedro.challenge.service.reader.FileReaderManager;
import se.quedro.challenge.service.reader.impl.FileReaderManagerImpl;


public class ChallengeModule extends AbstractModule {

    @Override
    public void configure() {
        bind(ActionHelperService.class).to(ActionHelperServiceImpl.class);
        bind(SaleObjectActionService.class).to(SaleObjectActionServiceImpl.class);
        bind(SaleObjectParserService.class).to(SaleObjectParserServiceImpl.class);
        bind(FileReaderManager.class).to(FileReaderManagerImpl.class);
        // Implementation created for showcase purposes
        final Multibinder<SaleObjectConsumer> saleObjectConsumerBinder =
                Multibinder.newSetBinder(binder(), SaleObjectConsumer.class);
        saleObjectConsumerBinder.addBinding().to(SaleObjectConsumerImpl.class);
    }
}
