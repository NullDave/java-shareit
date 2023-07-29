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
    public Map<String, Integer> handleValidation(MethodArgumentNotValidException e) {
        return Map.of(e.getMessage(), 400);
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Integer> handleValidation(Exception e) {
        return Map.of(e.getMessage(), 500);
    }

}
