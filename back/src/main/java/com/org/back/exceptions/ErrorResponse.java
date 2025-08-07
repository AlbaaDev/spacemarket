package com.org.back.exceptions;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private String timeStamp;

    public ErrorResponse(int statusCode, String message, String timeStamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
