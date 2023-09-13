package com.ipharmacare.collect.api.result;

public enum RestResult implements IRestResult {
    OK(200, "OK"),
    INVALID_REQUEST_PARAMETERS(400, "Invalid request parameters"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    ;

    private final Integer code;
    private final String message;

    RestResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}