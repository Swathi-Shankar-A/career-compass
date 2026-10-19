package com.career.util;

import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class for security operations including CSRF protection,
 * XSS escaping, and input sanitization.
 */
public class SecurityUtil {

    private static final String CSRF_SESSION_KEY = "csrfToken";
    private static final String CSRF_PARAM_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Retrieves or generates a cryptographically secure CSRF token for the session.
     */
    public static String getOrCreateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null || token.isEmpty()) {
            token = generateCsrfToken();
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        return token;
    }

    /**
     * Generates a new random URL-safe base64 token.
     */
    public static String generateCsrfToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Validates that the request has a valid matching CSRF token.
     */
    public static boolean validateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            // Also check header (e.g. for AJAX or fetch)
            requestToken = request.getHeader("X-CSRF-TOKEN");
        }

        return sessionToken.equals(requestToken);
    }

    /**
     * Escapes standard HTML special characters to prevent Cross-Site Scripting (XSS).
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(Math.max(16, input.length()));
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '"':
                    out.append("&quot;");
                    break;
                case '\'':
                    out.append("&#x27;");
                    break;
                case '/':
                    out.append("&#x2F;");
                    break;
                default:
                    out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Simple sanitization of user strings (null-safe, trimmed).
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
}
