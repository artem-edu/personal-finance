package mephi.util;

import mephi.exception.AuthenticationException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private final Map<UUID, UUID> sessions = new ConcurrentHashMap<>();

    public UUID createSession(UUID userId) {
        UUID token = UUID.randomUUID();
        sessions.put(token, userId);

        return token;
    }

    public UUID getUserId(UUID token) {
        if (token == null) {
            throw new AuthenticationException("Требуется авторизация");
        }

        UUID userId = sessions.get(token);
        if (userId == null) {
            throw new AuthenticationException("Сессия истекла");
        }

        return userId;
    }

    public void logout(UUID token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}
