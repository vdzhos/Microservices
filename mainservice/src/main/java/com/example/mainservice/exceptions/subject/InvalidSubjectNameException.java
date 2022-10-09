package com.example.mainservice.exceptions.subject;

public class InvalidSubjectNameException extends RuntimeException {

    public InvalidSubjectNameException(String explanation) {
        super(explanation);
    }
}
