package com.example.mainservice.exceptions.specialty;


public class InvalidSpecialtyNameException extends RuntimeException {

    public InvalidSpecialtyNameException(String explanation) {
        super(explanation);
    }
}
