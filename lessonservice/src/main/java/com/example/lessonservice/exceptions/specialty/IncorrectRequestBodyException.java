package com.example.lessonservice.exceptions.specialty;

public class IncorrectRequestBodyException extends RuntimeException {

    public IncorrectRequestBodyException(String message) {
        super(message);
    }
}
