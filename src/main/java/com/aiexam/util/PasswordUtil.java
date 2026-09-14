package com.aiexam.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps jBCrypt so the rest of the application never touches raw hashing
 * logic directly. Salted automatically per-call; cost factor 12 is a good
 * balance of security and login latency for this use case.
 */
public class PasswordUtil {

    private static final int COST_FACTOR = 12;

    private PasswordUtil() {
    }

    public static String hash(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(COST_FACTOR));
    }

    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // storedHash was not a valid BCrypt hash
            return false;
        }
    }
}
