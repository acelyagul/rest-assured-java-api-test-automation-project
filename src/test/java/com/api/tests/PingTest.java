package com.api.tests;

import com.api.services.PingService;
import com.api.utils.RestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingTest {
    @BeforeEach
    public void setup() {
        RestUtils.setBaseUrl("http://restful-booker.herokuapp.com");
        RestUtils.setBasePath("/ping");
        RestUtils.setContentType("application/json");
    }

    @Test
    public void pingServiceTest() {
        Response pingResponse = PingService.ping();
        assertEquals(201, pingResponse.getStatusCode());
        String responseBody = pingResponse.getBody().asString();
        assertEquals("Created", responseBody);
    }
}
