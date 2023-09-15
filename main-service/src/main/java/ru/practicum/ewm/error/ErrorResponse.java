package ru.practicum.ewm.error;

class ErrorResponse {

    String error;
    String reason;

    public ErrorResponse(String error, String reason) {
        this.error = error;
        this.reason = reason;
    }

    public String getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }
}
