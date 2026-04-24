package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiError {
    private int statusCode;
    private String errorMessage;

    public ApiError() {
    }

    public ApiError(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    @JsonProperty("statusCode")
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }
}