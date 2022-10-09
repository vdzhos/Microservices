package com.example.practice1.exceptions.teacher;

public class TeacherAlreadyExistsException extends RuntimeException {
    public TeacherAlreadyExistsException(String name) {
        super("Teacher with name \""+name+"\" already exists!");
    }
}
