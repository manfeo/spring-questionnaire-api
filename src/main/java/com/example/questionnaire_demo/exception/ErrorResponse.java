package com.example.questionnaire_demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String error;
    private String userFriendlyMessage;
    private long timestamp = System.currentTimeMillis();

    public ErrorResponse(int status, String message, String error, String userFriendlyMessage) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.userFriendlyMessage = userFriendlyMessage;
    }
}
