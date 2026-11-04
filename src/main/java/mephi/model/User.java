package mephi.model;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String login;
    private final String hasPassword;

    public User(String login, String hasPassword) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.hasPassword = hasPassword;
    }

    public User(UUID id, String login, String hasPassword) {
        this.id = id;
        this.login = login;
        this.hasPassword = hasPassword;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getHasPassword() {
        return hasPassword;
    }
}
