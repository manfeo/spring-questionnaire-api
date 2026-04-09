package com.example.questionnaire_demo.exception;

import jakarta.ws.rs.InternalServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String userFriendlyMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::createUserFriendlyMessage)
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                String.join(", ", errors),
                "BAD_REQUEST",
                userFriendlyMessages
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        ErrorResponse error = new ErrorResponse(
                e.getStatusCode().value(),
                e.getReason() != null ? e.getReason() : "No message available",
                e.getStatusCode().toString(),
                e.getReason() != null ? e.getReason() : "Unknown error"
        );
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error: " + e.getMessage(),
                "INTERNAL_SERVER_ERROR",
                "Произошла ошибка со стороны сервера"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String createUserFriendlyMessage(FieldError error) {
        String field = error.getField();
        String defaultMessage = error.getDefaultMessage();

        // Определяем user-friendly сообщение на основе поля и типа ошибки
        switch (field) {
            case "workCategories":
                if (defaultMessage.contains("required")) {
                    return "Категории работ обязательны для заполнения";
                }
                break;
            case "hasTeam":
                if (defaultMessage.contains("required")) {
                    return "Имеется ли команда обязательно для заполнения";
                }
                break;
            case "workExp":
                if (defaultMessage.contains("required")) {
                    return "Опыт работы обязателен для заполнения";
                }
                break;
            case "selfInfo":
                if (defaultMessage.contains("required")) {
                    return "Информация о себе обязательна для заполнения";
                }
                break;
            case "prices":
                if (defaultMessage.contains("required")) {
                    return "Расценки обязательны для заполнения";
                }
                break;
            case "entityId":
            case "questionnaireId":
            case "type":
            case "userId":
                if (defaultMessage.contains("required")) {
                    return "Произошла ошибка со стороны сервера";
                }
                break;
        }

        return "Произошла ошибка со стороны сервера";
    }
}
