package se.quedro.challenge.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Contains one value for now but can be extended when needed.
 */
public enum ActionType {
    SEND_TO_PARTNERS("sendToPartners");

    ActionType(final String commandText) {
        this.commandText = commandText;
    }

    private final String commandText;

    private final static Map<String, ActionType> ACTION_TYPE_BY_COMMAND = Arrays.stream(values())
            .collect(Collectors.toMap(type -> type.commandText, Function.identity()));

    public String getCommandText() {
        return commandText;
    }

    public static ActionType getActionTypeByCommand(final String commandText) {
        return ACTION_TYPE_BY_COMMAND.get(commandText);
    }
}
