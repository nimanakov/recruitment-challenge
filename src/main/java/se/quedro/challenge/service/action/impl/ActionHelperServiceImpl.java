package se.quedro.challenge.service.action.impl;

import com.google.inject.Inject;
import se.quedro.challenge.enums.ActionType;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.action.ActionHelperService;
import se.quedro.challenge.service.partner.SaleObjectActionService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ActionHelperServiceImpl implements ActionHelperService {

    private final SaleObjectActionService saleObjectActionService;

    @Inject
    public ActionHelperServiceImpl(final SaleObjectActionService saleObjectActionService) {
        this.saleObjectActionService = saleObjectActionService;

        // check all actions are implemented so error won't happen during execution but rather during initialization, fail-fast principle
        // it's a silly check for "toy" applications like this one but rather useful in real business apps so exception is not silently
        // waiting to be thrown during actual app invocation
        for (final ActionType actionType : ActionType.values()) {
            getAction(actionType);
        }
    }

    @Override
    public void performAction(final String command, final String[] arguments) throws ChallengeException {
        final ActionType actionType = ActionType.getActionTypeByCommand(command);

        if (actionType != null) {
            getAction(actionType).accept(arguments);
            return;
        }

        final String knownCommands = Arrays.stream(ActionType.values())
                .map(ActionType::getCommandText)
                .collect(Collectors.joining(", "));
        throw new ChallengeException(String.format("Unknown command %s. List of known commands: %s", command, knownCommands));
    }

    private Consumer<String[]> getAction(final ActionType actionType) {
        final Consumer<String[]> actionConsumer;

        // Can be replaced with "if statement but switch is used for exhaustion for future action types.
        //noinspection SwitchStatementWithTooFewBranches
        switch (actionType) {
            case SEND_TO_PARTNERS:
                actionConsumer = saleObjectActionService::sendToPartners;
                break;
            default:
                final String message = String.format("Action must be implemented for \"%s\" command. Contact your program provider",
                        actionType.getCommandText());
                throw new ChallengeException(message);
        }

        return actionConsumer;
    }
}
