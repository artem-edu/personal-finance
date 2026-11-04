package mephi;

import mephi.config.AppConfig;
import mephi.controller.MainController;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.create();
        new MainController(config).run();
    }
}