package com.example.practice1.exceptions.subject;

public class InvalidSubjectNameException extends RuntimeException {

    public InvalidSubjectNameException(String explanation) {
        super(explanation);
    }
}
