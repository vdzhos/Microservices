package com.example.practice1.exceptions.specialty;

public class SpecialtyInstanceAlreadyExistsException extends RuntimeException {
    public SpecialtyInstanceAlreadyExistsException(String message) {
        super(message);
    }
}
