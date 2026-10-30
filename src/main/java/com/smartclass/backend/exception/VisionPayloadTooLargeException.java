package com.smartclass.backend.exception;

public class VisionPayloadTooLargeException extends RuntimeException {

    public VisionPayloadTooLargeException(String message) {
        super(message);
    }
}
