package se.quedro.challenge.service.partner;

import se.quedro.challenge.data.sales.SaleObject;

import java.util.List;


public interface SaleObjectParserService {

    List<SaleObject> parse(String[] filePaths);
}
