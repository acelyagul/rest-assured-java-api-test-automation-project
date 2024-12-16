package com.api.services;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;

public class PingService {

    public static Response ping() {
        return RestAssured
                .given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .basePath("/ping")
                .get();
    }
}