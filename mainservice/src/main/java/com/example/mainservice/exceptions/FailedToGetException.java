package com.example.mainservice.exceptions;

public class FailedToGetException extends RuntimeException{

    public FailedToGetException(String message) {
        super(message);
    }
}
