package com.example.mainservice.exceptions.subject;

public class SubjectNotFoundException extends RuntimeException {

    public SubjectNotFoundException(String message) {
        super(message);
    }
}
