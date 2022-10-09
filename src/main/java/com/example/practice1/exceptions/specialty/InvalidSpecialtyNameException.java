package com.example.practice1.exceptions.specialty;


public class InvalidSpecialtyNameException extends RuntimeException {

    public InvalidSpecialtyNameException(String explanation) {
        super(explanation);
    }
}
