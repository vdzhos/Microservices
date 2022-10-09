package com.example.mainservice.exceptions.specialty;

public class IncorrectRequestBodyException extends RuntimeException {

    public IncorrectRequestBodyException(String message) {
        super(message);
    }
}
