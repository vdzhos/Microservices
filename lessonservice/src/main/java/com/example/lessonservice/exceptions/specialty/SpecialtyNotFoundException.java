package com.example.lessonservice.exceptions.specialty;

public class SpecialtyNotFoundException extends RuntimeException {


    public SpecialtyNotFoundException(Long id) {
        super("Specialty with id = "+id+" not found!");
    }
}
