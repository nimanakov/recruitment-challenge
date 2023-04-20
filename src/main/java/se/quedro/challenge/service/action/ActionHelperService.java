package se.quedro.challenge.service.action;

import se.quedro.challenge.exception.ChallengeException;


public interface ActionHelperService {

    void performAction(String command, String[] arguments) throws ChallengeException;
}
