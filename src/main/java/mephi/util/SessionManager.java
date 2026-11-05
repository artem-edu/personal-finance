package mephi.util;

import mephi.exception.AuthenticationException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private final Map<UUID, UUID> sessions = new ConcurrentHashMap<>();
    private UUID currentSession;

    public UUID createSession(UUID userId) {
        UUID token = UUID.randomUUID();
        sessions.put(token, userId);
        currentSession = token;
        return token;
    }

    public UUID getUserId() {
        if (currentSession == null) {
            throw new AuthenticationException("Требуется авторизация");
        }

        UUID userId = sessions.get(currentSession);
        if (userId == null) {
            throw new AuthenticationException("Сессия истекла");
        }

        return userId;
    }

    public boolean isLogged() {
        return currentSession != null && sessions.get(currentSession) != null;
    }

    public void logout() {
        if (currentSession != null) {
            sessions.remove(currentSession);
        }
    }
}
