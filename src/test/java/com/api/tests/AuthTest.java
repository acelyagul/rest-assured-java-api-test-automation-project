package com.api.tests;

import com.api.services.AuthService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthTest {
    @Test
    public void createTokenTest() {
        String token = AuthService.generateAuthToken();
        assertNotNull(token);
        System.out.println("Token: " + token);
    }
}
