package com.ipharmacare.collect.common.core.exception;

public class CollectBadRequestException extends RuntimeException {

    public CollectBadRequestException(String message) {
        super(message);
    }

    public CollectBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
