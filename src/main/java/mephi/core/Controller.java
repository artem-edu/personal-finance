package mephi.core;

import java.util.List;

public interface Controller {
    void help();

    void handleCommand(List<String> args);
}
