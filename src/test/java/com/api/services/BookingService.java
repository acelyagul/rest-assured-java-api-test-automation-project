package com.api.services;

import com.api.utils.RestUtils;
import com.github.javafaker.Faker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class BookingService {
    private static final Faker faker = new Faker();

    public static String generateBookingPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("firstname", faker.name().firstName());
        payload.addProperty("lastname", faker.name().lastName());
        payload.addProperty("totalprice", faker.number().numberBetween(100, 1000));
        payload.addProperty("depositpaid", faker.bool().bool());

        JsonObject bookingDates = new JsonObject();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookingDates.addProperty("checkin", today.format(formatter));
        bookingDates.addProperty("checkout", today.plusDays(60).format(formatter));
        payload.add("bookingdates", bookingDates);

        payload.addProperty("additionalneeds", faker.food().dish());
        return payload.toString();
    }

    public static String generateUpdateBookingPayload(Response createResponse) {
        JsonObject createBooking = JsonParser.parseString(createResponse.getBody().asString())
                .getAsJsonObject()
                .getAsJsonObject("booking");

        createBooking.addProperty("firstname", faker.name().firstName());
        createBooking.addProperty("lastname", faker.name().lastName());

        int originalTotalPrice = createBooking.get("totalprice").getAsInt();
        createBooking.addProperty("totalprice", originalTotalPrice + 100);

        String additionalNeedsOriginal = createBooking.get("additionalneeds").getAsString();
        createBooking.addProperty("additionalneeds", "updated " + additionalNeedsOriginal);

        boolean depositPaidOriginal = createBooking.get("depositpaid").getAsBoolean();
        createBooking.addProperty("depositpaid", !depositPaidOriginal);

        JsonObject bookingDates = createBooking.getAsJsonObject("bookingdates");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate originalCheckin = LocalDate.parse(bookingDates.get("checkin").getAsString(), formatter);
        LocalDate originalCheckout = LocalDate.parse(bookingDates.get("checkout").getAsString(), formatter);

        bookingDates.addProperty("checkin", originalCheckin.plusDays(10).format(formatter));
        bookingDates.addProperty("checkout", originalCheckout.plusDays(10).format(formatter));
        createBooking.add("bookingdates", bookingDates);


        return createBooking.toString();
    }

    public static Response getBooking(int bookingId) {
        RestUtils.setBasePath("/booking/" + bookingId);

        return RestUtils.sendGetRequest();
    }

    public static Response getBookings(Map<String, String> queryParams) {
        return RestUtils.sendGetRequestWithParams(queryParams);
    }

    public static Response updateBooking(int bookingId, String token, String payload) {
        RestUtils.setBasePath("/booking/" + bookingId);
        return RestUtils.sendPutRequest(payload, token);

    }

    public static Response deleteBooking(int bookingId, String token) {
        RestUtils.setBasePath("/booking/" + bookingId);

        return RestUtils.sendDeleteRequest(token);
    }

    public static Response partialUpdateBooking(int bookingId, String token, Map<String, Object> updates) {
        RestUtils.setBasePath("/booking/" + bookingId);
        return RestUtils.sendPatchRequest(updates, token);
    }


}