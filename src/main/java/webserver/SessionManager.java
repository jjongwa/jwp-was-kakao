package webserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void add(final String sessionId) {
        SESSIONS.put(sessionId, new Session(sessionId));
    }

    public Session findSession(final String id) {
        if (id == null || !SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}

