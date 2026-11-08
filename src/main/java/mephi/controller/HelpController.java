package mephi.controller;

import mephi.core.Controller;
import mephi.core.Router;

import java.util.List;
import java.util.Map;

public class HelpController implements Controller {
    private final Router router;
    public HelpController(Router router) {
        this.router = router;
    }

    @Override
    public void handleCommand(List<String> args) {
        help();
    }

    public void help() {
        System.out.println("[ Справка по командам системы]".toUpperCase());

        for (Controller controller: router.getRoutes().values()) {
            if (controller == this) {
                continue;
            }

            controller.help();
        }

    }
}
