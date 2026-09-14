package com.aiexam.util;

import java.util.regex.Pattern;

/**
 * Server-side input validation. All Servlets MUST validate incoming
 * parameters with these helpers before they reach the DAO layer -
 * never trust client-side validation alone.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // At least 8 chars, one uppercase, one lowercase, one digit, one special char
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Strips characters commonly used in basic XSS payloads when echoing
     * user-supplied text back into JSP pages. This is a defense-in-depth
     * helper only - JSTL's <c:out> / EL auto-escaping is the primary
     * defense and must still be used in every JSP.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;");
    }
}
