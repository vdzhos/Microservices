package com.example.lessonservice.exceptions.teacher;

public class TeacherAlreadyExistsException extends RuntimeException {
    public TeacherAlreadyExistsException(String name) {
        super("Teacher with name \""+name+"\" already exists!");
    }
}
