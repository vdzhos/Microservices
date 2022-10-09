package com.example.practice1.models;

import java.util.List;

public class TimeSlot {


    private List<Lesson> lessons;
    private Lesson.Time time;

    public TimeSlot(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public Lesson.Time getTime() {
        return time;
    }

    public void setTime(Lesson.Time t) {
        time = t;
    }
}
