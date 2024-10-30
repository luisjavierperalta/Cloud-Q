package com.cloudq.cloudq.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class SessionManagementService {

    private static final Logger logger = LoggerFactory.getLogger(SessionManagementService.class);

    private final SessionRegistry sessionRegistry;

    @Autowired
    public SessionManagementService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Create a new session for the authenticated user.
     *
     * @param session The HTTP session.
     */
    public void createSession(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Register the session in the session registry
            sessionRegistry.registerNewSession(session.getId(), authentication.getPrincipal());
            logger.info("Session created for user: {}", authentication.getName());
        }
    }

    /**
     * Terminate the session by invalidating it.
     *
     * @param session The HTTP session.
     */
    public void terminateSession(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Unregister the session
            sessionRegistry.removeSessionInformation(session.getId());
            session.invalidate();
            logger.info("Session terminated for user: {}", authentication.getName());
        }
    }

    /**
     * Get all active sessions for the currently authenticated user.
     *
     * @return List of active session IDs.
     */
    public List<String> getActiveSessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Retrieve active sessions for the user
            List<Object> principals = sessionRegistry.getAllPrincipals();
            return sessionRegistry.getAllSessions(authentication.getPrincipal(), false)
                    .stream()
                    .map(sessionInformation -> sessionInformation.getSessionId())
                    .toList();
        }
        return List.of(); // Return an empty list if not authenticated
    }
}
