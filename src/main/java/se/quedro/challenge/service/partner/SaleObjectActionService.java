package se.quedro.challenge.service.partner;

import se.quedro.challenge.exception.ChallengeException;


public interface SaleObjectActionService {

    void sendToPartners(String[] input) throws ChallengeException;
}
