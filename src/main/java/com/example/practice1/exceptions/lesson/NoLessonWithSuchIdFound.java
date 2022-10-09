package com.example.practice1.exceptions.lesson;

public class NoLessonWithSuchIdFound extends Exception {

    private String action;

    public NoLessonWithSuchIdFound(Long id, String action) {
        super("Lesson with id '"+ id +"' not found!");
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}