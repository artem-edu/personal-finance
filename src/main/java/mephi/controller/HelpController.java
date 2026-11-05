package mephi.controller;

import mephi.core.Controller;

import java.util.List;

public class HelpController implements Controller {
    @Override
    public void handleCommand(List<String> args) {
        help();
    }

    public void help() {
        System.out.println("\n=== Общая справка ===");
        System.out.println("Вывести справки по всем контроллерам");
    }
}
