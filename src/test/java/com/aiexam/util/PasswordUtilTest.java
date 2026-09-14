package com.aiexam.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerifyPassword() {
        String plainPassword = "TestPassword@123";
        String hashed = PasswordUtil.hash(plainPassword);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$") || hashed.startsWith("$2y$"));
        assertTrue(PasswordUtil.verify(plainPassword, hashed));
        assertFalse(PasswordUtil.verify("WrongPassword", hashed));
    }

    @Test
    public void testNullOrInvalidInput() {
        assertFalse(PasswordUtil.verify(null, "$2a$12$eIXipzN3vmUXWpadfxLB4O1zJ5/U.zJ4K5V2/3uW5N4O1zJ5/U.zJ"));
        assertFalse(PasswordUtil.verify("password", null));
        assertFalse(PasswordUtil.verify("password", "invalid_hash_format"));
    }
}
