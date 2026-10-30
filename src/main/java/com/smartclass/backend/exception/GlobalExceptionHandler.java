package com.smartclass.backend.exception;

import com.smartclass.backend.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handlerException(UserAlreadyExistsException e) {

        final int status = HttpStatus.CONFLICT.value();
        final String message = e.getMessage();

        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, message));
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<?> handlerScheduleConflict(
            ScheduleConflictException e
    ) {
        final int status = HttpStatus.CONFLICT.value();
        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, e.getMessage()));
    }

    @ExceptionHandler(VisionInputException.class)
    public ResponseEntity<?> handlerVisionInput(VisionInputException e) {
        final int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handlerMissingMultipartPart(
            MissingServletRequestPartException e
    ) {
        final int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, "Field image wajib diisi"));
    }

    @ExceptionHandler({
            VisionPayloadTooLargeException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<?> handlerVisionPayloadTooLarge(Exception e) {
        final int status = HttpStatus.PAYLOAD_TOO_LARGE.value();
        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, e.getMessage()));
    }

    @ExceptionHandler(VisionInferenceException.class)
    public ResponseEntity<?> handlerVisionInference(VisionInferenceException e) {
        final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, e.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handlerDisabled(DisabledException e) {

        final int status = HttpStatus.UNAUTHORIZED.value();

        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, "Akun tidak aktif"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handlerAuthentication(AuthenticationException e) {

        final int status = HttpStatus.UNAUTHORIZED.value();

        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, "Email atau password salah"));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<?> handlerBadRequest(Exception e) {

        final int status = HttpStatus.BAD_REQUEST.value();
        final String message = e.getMessage();

        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerException(Exception e) {

        final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        final String message = e.getMessage();

        return ResponseEntity
                .status(status)
                .body(generateAPIResponse(status, message));
    }

    private static APIResponse generateAPIResponse(
            int status,
            String message
    ) {
        return new APIResponse(status, message);
    }
}