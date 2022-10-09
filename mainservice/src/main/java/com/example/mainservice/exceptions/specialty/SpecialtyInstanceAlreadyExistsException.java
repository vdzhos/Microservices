package com.example.mainservice.exceptions.specialty;

public class SpecialtyInstanceAlreadyExistsException extends RuntimeException {
    public SpecialtyInstanceAlreadyExistsException(String message) {
        super(message);
    }
}
