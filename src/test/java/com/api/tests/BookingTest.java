package com.api.tests;

import com.api.services.AuthService;
import com.api.services.BookingService;
import com.api.utils.RestUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingTest {

    @BeforeEach
    public void setup() {
        RestUtils.setBaseUrl("http://restful-booker.herokuapp.com");
        RestUtils.setBasePath("/booking");
        RestUtils.setContentType("application/json");
    }

    @Test
    public void createBookingTest() {
        String payload = BookingService.generateBookingPayload();
        System.out.println("Generated Payload for Create Booking: " + payload);
        Response response = RestUtils.sendPostRequest(payload);
        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        System.out.println("Response Body: " + responseBody);

        JsonObject payloadJson = JsonParser.parseString(payload).getAsJsonObject();
        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

        JsonObject bookingResponse = responseJson.getAsJsonObject("booking");

        assertEquals(payloadJson.get("firstname").getAsString(), bookingResponse.get("firstname").getAsString());
        assertEquals(payloadJson.get("lastname").getAsString(), bookingResponse.get("lastname").getAsString());
        assertEquals(payloadJson.get("totalprice").getAsInt(), bookingResponse.get("totalprice").getAsInt());
        assertEquals(payloadJson.get("depositpaid").getAsBoolean(), bookingResponse.get("depositpaid").getAsBoolean());

        JsonObject payloadBookingDates = payloadJson.getAsJsonObject("bookingdates");
        JsonObject responseBookingDates = bookingResponse.getAsJsonObject("bookingdates");

        assertEquals(payloadBookingDates.get("checkin").getAsString(), responseBookingDates.get("checkin").getAsString());
        assertEquals(payloadBookingDates.get("checkout").getAsString(), responseBookingDates.get("checkout").getAsString());


        String schemaString = RestUtils.readSchemaFile("createBookingSchema.json");

        RestUtils.validateJsonSchema(response, schemaString);
    }

    @Test
    public void getBookingTest() {
        String createPayload = BookingService.generateBookingPayload();
        Response createResponse = RestUtils.sendPostRequest(createPayload);

        assertEquals(200, createResponse.getStatusCode());
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.println("Booking ID Created: " + bookingId);

        Response getResponse = BookingService.getBooking(bookingId);

        assertEquals(200, getResponse.getStatusCode());

        String schemaString = RestUtils.readSchemaFile("getBookingSchema.json");
        RestUtils.validateJsonSchema(getResponse, schemaString);


        JsonPath createdJson = new JsonPath(createResponse.getBody().asString());
        JsonPath fetchedJson = new JsonPath(getResponse.getBody().asString());

        assertEquals(createdJson.getString("booking.firstname"), fetchedJson.getString("firstname"));
        assertEquals(createdJson.getString("booking.lastname"), fetchedJson.getString("lastname"));
        assertEquals(createdJson.getInt("booking.totalprice"), fetchedJson.getInt("totalprice"));
        assertEquals(createdJson.getBoolean("booking.depositpaid"), fetchedJson.getBoolean("depositpaid"));
        assertEquals(createdJson.getString("booking.bookingdates.checkin"), fetchedJson.getString("bookingdates.checkin"));
        assertEquals(createdJson.getString("booking.bookingdates.checkout"), fetchedJson.getString("bookingdates.checkout"));
        assertEquals(createdJson.getString("booking.additionalneeds"), fetchedJson.getString("additionalneeds"));

        System.out.println("Get Booking Response: " + getResponse.getBody().asPrettyString());
    }

    @Test
    public void getBookingsWithFilterTest() {
        String createPayload = BookingService.generateBookingPayload();
        Response createResponse = RestUtils.sendPostRequest(createPayload);

        assertEquals(200, createResponse.getStatusCode());
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        String firstname = createResponse.jsonPath().getString("booking.firstname");
        String lastname = createResponse.jsonPath().getString("booking.lastname");
        System.out.println("Booking ID Created: " + bookingId);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("firstname", firstname);
        queryParams.put("lastname", lastname);

        Response getResponse = BookingService.getBookings(queryParams);

        assertEquals(200, getResponse.getStatusCode());

        List<Integer> bookingIds = getResponse.jsonPath().getList("bookingid");
        assertTrue(bookingIds.contains(bookingId));
        System.out.println("Filtered Bookings Response: " + getResponse.getBody().asPrettyString());
    }

    @Test
    public void updateBookingTest() {

        String createPayload = BookingService.generateBookingPayload();
        System.out.println("Generated create Payload: " + createPayload);

        Response createResponse = RestUtils.sendPostRequest(createPayload);

        assertEquals(200, createResponse.getStatusCode());
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.println("Booking ID Created: " + bookingId);

        String token = AuthService.getToken();

        String updatePayload = BookingService.generateUpdateBookingPayload(createResponse);
        System.out.println("Generated Update Payload: " + updatePayload);

        Response updateResponse = BookingService.updateBooking(bookingId, token, updatePayload);

        assertEquals(200, updateResponse.getStatusCode());
        System.out.println("Update Booking Response: " + updateResponse.getBody().asPrettyString());


        String responseBody = updateResponse.getBody().asString();
        System.out.println("Response Body: " + responseBody);

        String schemaString = RestUtils.readSchemaFile("updateBookingSchema.json");
        RestUtils.validateJsonSchema(updateResponse, schemaString);

        JsonObject payloadJson = JsonParser.parseString(updatePayload).getAsJsonObject();
        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

        assertEquals(payloadJson.get("firstname").getAsString(), responseJson.get("firstname").getAsString());
        assertEquals(payloadJson.get("lastname").getAsString(), responseJson.get("lastname").getAsString());
        assertEquals(payloadJson.get("totalprice").getAsInt(), responseJson.get("totalprice").getAsInt());
        assertEquals(payloadJson.get("depositpaid").getAsBoolean(), responseJson.get("depositpaid").getAsBoolean());

        JsonObject payloadBookingDates = payloadJson.getAsJsonObject("bookingdates");
        JsonObject responseBookingDates = responseJson.getAsJsonObject("bookingdates");

        assertEquals(payloadBookingDates.get("checkin").getAsString(), responseBookingDates.get("checkin").getAsString());
        assertEquals(payloadBookingDates.get("checkout").getAsString(), responseBookingDates.get("checkout").getAsString());
    }

    @Test
    public void deleteBookingTest() {
        String createPayload = BookingService.generateBookingPayload();
        Response createResponse = RestUtils.sendPostRequest(createPayload);

        assertEquals(200, createResponse.getStatusCode());
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.println("Booking ID Created: " + bookingId);

        String token = AuthService.generateAuthToken();

        Response deleteResponse = BookingService.deleteBooking(bookingId, token);

        assertEquals(201, deleteResponse.getStatusCode());
        System.out.println("Delete Booking Response: " + deleteResponse.getBody().asPrettyString());

        RestUtils.setBasePath("/booking/" + bookingId);
        Response fetchResponse = RestUtils.sendGetRequest();
        assertEquals(404, fetchResponse.getStatusCode());
    }


    @Test
    public void partialUpdateBookingTest() {
        String createPayload = BookingService.generateBookingPayload();
        Response createResponse = RestUtils.sendPostRequest(createPayload);

        assertEquals(200, createResponse.getStatusCode());
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.println("Booking ID Created: " + bookingId);


        String token = AuthService.generateAuthToken();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstname", "UpdatedFirstName");
        updates.put("lastname", "UpdatedLastName");

        Response partialUpdateResponse = BookingService.partialUpdateBooking(bookingId, token, updates);

        String schemaString = RestUtils.readSchemaFile("patchBookingSchema.json");
        RestUtils.validateJsonSchema(partialUpdateResponse, schemaString);


        assertEquals(200, partialUpdateResponse.getStatusCode());

        updates.forEach((key, expectedValue) -> {
            Object actualValue = partialUpdateResponse.jsonPath().get(key);
            assertEquals(expectedValue, actualValue);
        });
        System.out.println("Partial Update Booking Response: " + partialUpdateResponse.getBody().asPrettyString());
    }

}