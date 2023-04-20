package se.quedro.challenge.service.action.impl;

import org.junit.jupiter.api.Test;
import se.quedro.challenge.enums.ActionType;
import se.quedro.challenge.exception.ChallengeException;
import se.quedro.challenge.service.action.ActionHelperService;
import se.quedro.challenge.service.partner.SaleObjectActionService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class ActionHelperServiceImplTest {

    // I prefer explicit initialization instead of declarative one (e.g. annotations)
    private final SaleObjectActionService saleObjectActionService = mock(SaleObjectActionService.class);

    private final ActionHelperService actionHelperService = new ActionHelperServiceImpl(saleObjectActionService);

    @Test
    void performAction_ok() {
        final String[] arguments = new String[] { "./somEfileName.json" };

        actionHelperService.performAction(ActionType.SEND_TO_PARTNERS.getCommandText(), arguments);

        verify(saleObjectActionService).sendToPartners(aryEq(arguments));
    }

    @Test
    void performAction_unknownCommand() {
        assertThrows(ChallengeException.class, () -> actionHelperService.performAction("unknownCommand", new String[] {}));
    }
}