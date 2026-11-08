package mephi.view;

public class AuthView {

    public void printHelp() {
        System.out.println("\n[ Авторизация и регистрация ]");
        String format = "\t %-25s - %s\n";
        System.out.printf(format, "auth login", "авторизация");
        System.out.printf(format, "auth reg", "регистрация");
        System.out.printf(format, "auth logout", "выход из учетной записи");
    }

    public void printUnknownAction() {
        System.out.println("Команда не найдена, вывод справки >> auth help");
    }

    public void printAlreadyLogged() {
        System.out.println("Вы уже авторизованы.");
        System.out.println("Для входа в другой аккаунт выполните выход >> auth logout");
    }

    public void printLoginHeader() {
        System.out.println("\n[ Авторизация ]");
    }

    public void printRegHeader() {
        System.out.println("\n[ Регистрация ]");
    }

    public void printLoginSuccess(String login) {
        System.out.printf("\nДобро пожаловать, %s\n", login);
    }

    public void printRegSuccess() {
        System.out.println("\nПользователь создан. Теперь войдите. >> auth login");
    }

    public void printLogoutSuccess() {
        System.out.println("\nВы вышли из системы.");
    }

    public void printError(RuntimeException error) {
        System.out.println("\nОшибка: " + error.getMessage());
    }
}
