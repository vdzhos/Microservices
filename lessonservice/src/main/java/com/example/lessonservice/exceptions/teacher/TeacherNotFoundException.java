package com.example.lessonservice.exceptions.teacher;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String str) {
        super(str);
    }
}
