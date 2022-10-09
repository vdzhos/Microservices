package com.example.lessonservice.services.interfaces;

import com.example.lessonservice.exceptions.NoLessonWithSuchIdFound;
import com.example.lessonservice.models.Lesson;

public interface LessonService {

    Lesson getLessonById(Long id) throws NoLessonWithSuchIdFound;
    Iterable<Lesson> getAll();

    Lesson addLesson(Lesson lesson);

    Lesson updateLesson(Lesson lesson) throws NoLessonWithSuchIdFound;

    void deleteLesson(Long id) throws NoLessonWithSuchIdFound;

    boolean lessonExistsById(Long id);

}
