package mephi.controller;

import mephi.core.Controller;
import mephi.core.Router;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpControllerTest {
    @Mock
    private Router router;

    @InjectMocks
    private HelpController helpController;

    @Test
    public void handleCommandPrintsHelpForAllRoutesExceptItself() {
        Controller authController = mock(Controller.class);
        Controller walletController = mock(Controller.class);
        Map<String, Controller> routes = new HashMap<>();
        routes.put("auth", authController);
        routes.put("wallet", walletController);
        routes.put("help", helpController);
        when(router.getRoutes()).thenReturn(routes);

        helpController.handleCommand(List.of());

        verify(authController, times(1)).help();
        verify(walletController, times(1)).help();
    }

    @Test
    public void handleCommandIgnoresArgs() {
        Controller authController = mock(Controller.class);
        Map<String, Controller> routes = new HashMap<>();
        routes.put("auth", authController);
        when(router.getRoutes()).thenReturn(routes);

        helpController.handleCommand(List.of("any", "args"));

        verify(authController, times(1)).help();
    }
}
