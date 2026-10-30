package com.smartclass.backend.exception;

public class VisionInputException extends RuntimeException {

    public VisionInputException(String message) {
        super(message);
    }

    public VisionInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
