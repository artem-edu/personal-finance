package mephi.core;

import java.util.List;

public interface Controller {
    void handleCommand(List<String> args);
    void help();
}
