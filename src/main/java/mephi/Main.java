package mephi;

import mephi.core.AppBootstrap;
import mephi.util.ConsoleReader;
import mephi.core.Router;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Router router = AppBootstrap.bootstrap();
        while (true) {
            List<String> tokens = ConsoleReader.readCommand("> ");
            if (tokens.get(0).equals("exit")) {
                return;
            }
            try {
                router.route(tokens);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}