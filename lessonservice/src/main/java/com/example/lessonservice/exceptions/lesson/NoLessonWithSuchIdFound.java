package com.example.lessonservice.exceptions.lesson;

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