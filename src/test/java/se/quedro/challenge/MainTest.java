package se.quedro.challenge;

import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.quedro.challenge.service.action.ActionHelperService;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


class MainTest {

    private final static String INJECTOR_FIELD_NAME = "INJECTOR";

    private static Object REAL_INJECTOR_VALUE;

    private final Injector injector = mock(Injector.class);

    private final ActionHelperService actionHelperService = mock(ActionHelperService.class);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        final Field injectorField = getInjectorField();

        REAL_INJECTOR_VALUE = injectorField.get(null);
        injectorField.set(null, injector);

        doReturn(actionHelperService).when(injector).getInstance(eq(ActionHelperService.class));
    }

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {
        getInjectorField().set(null, REAL_INJECTOR_VALUE);
    }

    @Test
    void main_ok() {
        final String command = "sendToPartners";
        final String argument = "./someFile.xml";

        assertDoesNotThrow(() -> Main.main(new String[] {command, argument}));
        verify(actionHelperService, times(1))
                .performAction(eq(command), aryEq(new String[] {argument}));

        assertDoesNotThrow(() -> Main.main(new String[] {"someOtherCommand"}));
    }

    @Test
    void main_noArguments() {
        Main.main(new String[] {});

        verify(actionHelperService, times(0)).performAction(any(), any());
    }

    private static Field getInjectorField() throws NoSuchFieldException, IllegalAccessException {
        final Field field = Main.class.getDeclaredField(INJECTOR_FIELD_NAME);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");

        field.setAccessible(true);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return field;
    }
}