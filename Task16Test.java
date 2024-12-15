

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class RequestPayload {
    private String key;
    private String value;

    public RequestPayload(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class ResponsePayload {
    private String key;
    private String value;

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class ResponseComparator {
    public static boolean compareResponses(ResponsePayload expected, ResponsePayload actual) {
        return expected.getKey().equals(actual.getKey()) && expected.getValue().equals(actual.getValue());
    }
}

public class Task16Test {

    public static void main(String[] args) {
        RestAssured.baseURI = "https://api.example.com";

        RequestPayload requestPayload = new RequestPayload("testKey", "testValue");
        Response response = given()
                .contentType("application/json")
                .body(requestPayload)
                .when()
                .post("/endpoint")
                .then()
                .statusCode(200)
                .extract()
                .response();

        ResponsePayload actualResponse = response.as(ResponsePayload.class);
        ResponsePayload expectedResponse = new ResponsePayload("testKey", "testValue");

        boolean isValid = ResponseComparator.compareResponses(expectedResponse, actualResponse);
        System.out.println("Validation using RestAssured: " + (isValid ? "Success" : "Failed"));

        // Scenario 2: Using another API client (e.g., OkHttp)
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                "{\"key\": \"testKey\", \"value\": \"testValue\"}"
        );

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.example.com/endpoint")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (okhttp3.Response okHttpResponse = client.newCall(request).execute()) {
            if (okHttpResponse.isSuccessful()) {
                String responseBody = okHttpResponse.body().string();
                ResponsePayload actualOkHttpResponse = new com.google.gson.Gson().fromJson(responseBody, ResponsePayload.class);
                boolean isOkHttpValid = ResponseComparator.compareResponses(expectedResponse, actualOkHttpResponse);
                System.out.println("Validation using OkHttp: " + (isOkHttpValid ? "Success" : "Failed"));
            } else {
                System.out.println("OkHttp Request failed with status code: " + okHttpResponse.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
