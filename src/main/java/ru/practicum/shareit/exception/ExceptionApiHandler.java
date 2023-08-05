package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFound(NotFoundException e) {
        return Map.of(e.getMessage(), 404);
    }

    @ExceptionHandler(value = PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Integer> handlePermission(PermissionException e) {
        return Map.of(e.getMessage(), 403);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleValidationArgument(MethodArgumentNotValidException e) {
        return Map.of(e.getMessage(), 400);
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleBadRequest(BadRequestException e) {
        return Map.of(e.getMessage(), 400);
    }

    @ExceptionHandler(value = EmailBusyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleEmailBusy(EmailBusyException e) {
        return Map.of(e.getMessage(), 409);
    }

    @ExceptionHandler(value = NotImplementedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotImplemented(NotImplementedException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Integer> handleServerError(Exception e) {
        return Map.of(e.getMessage(), 500);
    }
}
