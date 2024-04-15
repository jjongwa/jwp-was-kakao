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

    public Session add(final String sessionId) {
        Session session = new Session(sessionId);
        SESSIONS.put(sessionId, session);
        return session;
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

