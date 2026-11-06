package mephi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleReader {
    private static final Scanner scanner = new Scanner(System.in);

    public static List<String> readCommand(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();

        return parser(line);
    }

    public static int readInt(String prompt) {
        System.out.print(prompt + ": ");
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print(prompt + ": ");
            }
        }
    }
    public static int readInt(String prompt, int min, int max) {
        System.out.print(prompt + ": ");
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Ошибка: число должно быть от %d до %d\n", min, max);
            } catch (NumberFormatException e) {
                System.out.print(prompt + ": ");
            }
        }
    }


    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static Boolean readBoolean(String prompt) {
        System.out.print(prompt + "(д/н): ");
        String input = scanner.nextLine().trim().toLowerCase();

        return input.startsWith("д") || input.startsWith("y");
    }

    private static List<String> parser(String line) {
        List<String> tokens = new ArrayList<>();
        boolean insideQuotes = false;
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }

            if (Character.isWhitespace(c) && !insideQuotes) {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                continue;
            }

            currentToken.append(c);
        }

        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        if (insideQuotes) {
            System.out.println("Ошибка: Незакрытая кавычка");
            return new ArrayList<>();
        }

        return tokens;
    }

}
