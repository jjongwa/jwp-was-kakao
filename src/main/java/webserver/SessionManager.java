package webserver;

import db.DataBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final String USER_KEY = "user";
    private static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void makeUserSession(final String sessionId, final String userId) {
        if (findSession(sessionId) == null) {
            add(sessionId);
            final Session session = findSession(sessionId);
            session.setAttribute(USER_KEY, DataBase.findUserById(userId));
        }
    }

    private void add(final String sessionId) {
        SESSIONS.put(sessionId, new Session(sessionId));
    }

    public Session findSession(final String id) {
        if (id == null || !SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public boolean isLogined(final HttpCookie cookie) {
        return SESSIONS.containsKey(cookie.getValue(JSESSIONID));
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}

