package com.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class RestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class);
    public static RequestSpecification requestSpecification;

    public static void setBaseUrl(String baseUrl) {
        RestAssured.baseURI = baseUrl;
        requestSpecification = given();
        LOGGER.info("Base URL set to: {}", baseUrl);
    }

    public static void setBasePath(String basePath) {
        RestAssured.basePath = basePath;
        LOGGER.info("Base Path set to: {}", basePath);
    }

    public static void setContentType(String contentType) {
        requestSpecification.contentType(contentType);
        LOGGER.info("Content-Type set to: {}", contentType);
    }


    public static Response sendPostRequest(String payload) {
        requestSpecification.body(payload);
        Response response = requestSpecification.post(RestAssured.basePath);
        LOGGER.info("POST Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }

    public static Response sendGetRequest() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        Response response = requestSpecification.get(RestAssured.basePath);
        LOGGER.info("GET Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }

    public static Response sendGetRequestWithParams(Map<String, String> queryParams) {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(requestSpecification::queryParam);
        }

        Response response = requestSpecification.get(RestAssured.basePath);
        LOGGER.info("GET Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }


    public static Response sendPutRequest(String payload, String cookie) {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        if (cookie != null && !cookie.isEmpty()) {
            requestSpecification.cookie("token", cookie);
            LOGGER.info("Cookie added to the request: {}", cookie);
        }

        requestSpecification.body(payload);

        Response response = requestSpecification.put(RestAssured.basePath);
        LOGGER.info("PUT Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }


    public static Response sendDeleteRequest(String cookie) {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        if (cookie != null && !cookie.isEmpty()) {
            requestSpecification.cookie("token", cookie);
            LOGGER.info("Cookie added to the request: {}", cookie);
        }

        Response response = requestSpecification.delete(RestAssured.basePath);
        LOGGER.info("DELETE Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }


    public static Response sendPatchRequest(Map<String, Object> updates, String cookie) {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        JsonObject payload = new JsonObject();
        updates.forEach((key, value) -> {
            if (value instanceof Number) {
                payload.addProperty(key, (Number) value);
            } else if (value instanceof Boolean) {
                payload.addProperty(key, (Boolean) value);
            } else {
                payload.addProperty(key, value.toString());
            }
        });

        if (cookie != null && !cookie.isEmpty()) {
            requestSpecification.cookie("token", cookie);
            LOGGER.info("Cookie added to the request: {}", cookie);
        }

        requestSpecification.body(payload.toString());

        Response response = requestSpecification.patch(RestAssured.basePath);
        LOGGER.info("PATCH Request Sent to: {}", RestAssured.baseURI + RestAssured.basePath);
        LOGGER.info("Response Status Code: {}", response.getStatusCode());
        return response;
    }

    public static String readSchemaFile(String schemaFileName) {
        try {
            String schemaPath = "src/test/resources/schemas/" + schemaFileName;
            return new String(Files.readAllBytes(Paths.get(schemaPath)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON schema file: " + schemaFileName, e);
        }
    }


    public static void validateJsonSchema(Response response, String schemaString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            JsonSchema schema = factory.getSchema(schemaString);
            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());

            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (!errors.isEmpty()) {
                throw new RuntimeException("JSON Schema validation failed: " + errors);
            }

            LOGGER.info("JSON Schema validation passed.");
        } catch (Exception e) {
            throw new RuntimeException("Error during JSON Schema validation: " + e.getMessage(), e);
        }
    }
}