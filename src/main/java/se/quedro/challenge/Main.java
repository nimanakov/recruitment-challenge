package se.quedro.challenge;

import com.google.inject.Guice;
import com.google.inject.Injector;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.action.ActionHelperService;


public class Main {

    private final static Injector INJECTOR = Guice.createInjector(new ChallengeModule());

    public static void main(final String[] arguments) {
        try {
            if (arguments.length == 0) {
                throw new ChallengeException("No arguments provided"); // instead of System.exit
            }

            final ActionHelperService actionHelperService = INJECTOR.getInstance(ActionHelperService.class);

            actionHelperService.performAction(arguments[0], getCommandArguments(arguments));
        } catch (final ChallengeException ex) {
            System.out.println(ex.getReason());
        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String[] getCommandArguments(final String[] input) {
        final int argumentsNumber = input.length - 1;
        final String[] arguments = new String[argumentsNumber];

        System.arraycopy(input, 1, arguments, 0, argumentsNumber);

        return arguments;
    }
}
