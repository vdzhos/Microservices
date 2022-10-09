package com.example.mainservice.exceptions.teacher;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String str) {
        super(str);
    }
}
