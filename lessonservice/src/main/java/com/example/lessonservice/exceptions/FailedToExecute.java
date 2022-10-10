package com.example.lessonservice.exceptions;

public class FailedToExecute extends RuntimeException{

    public FailedToExecute(String message) {
        super(message);
    }
}
