package com.api.services;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthService {
    public static String token;

    public static String generateAuthToken() {
        RestAssured.baseURI = "http://restful-booker.herokuapp.com";
        RestAssured.basePath = "/auth";

        JsonObject payload = new JsonObject();
        payload.addProperty("username", "admin");
        payload.addProperty("password", "password123");

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(payload.toString())
                .post();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to generate token! Status Code: " + response.getStatusCode());
        }

        token = response.jsonPath().getString("token");
        System.out.println("Generated Token: " + token);
        return token;
    }

    public static String getToken() {
        if (token == null) {
            return generateAuthToken();
        }
        return token;
    }
}
