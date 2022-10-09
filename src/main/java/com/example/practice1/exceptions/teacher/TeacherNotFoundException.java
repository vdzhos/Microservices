package com.example.practice1.exceptions.teacher;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String str) {
        super(str);
    }
}
