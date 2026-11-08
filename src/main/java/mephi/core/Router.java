package mephi.core;

import mephi.exception.PersonalFinanceException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final Map<String, Controller> routes = new HashMap<>();

    public void register(String route, Controller controller) {
        routes.put(route.toLowerCase(), controller);
    }

    public void route(List<String> tokens) {
        if (tokens.isEmpty()) return;

        String routeName = tokens.get(0).toLowerCase();
        Controller controller = routes.get(routeName);

        if (controller == null) {
            throw new PersonalFinanceException("Команда не найдена, вывод справки >> help");
        }

        List<String> args = tokens.subList(1, tokens.size());
        controller.handleCommand(args);
    }

    public Map<String, Controller> getRoutes() {
        return Collections.unmodifiableMap(routes);
    }
}
