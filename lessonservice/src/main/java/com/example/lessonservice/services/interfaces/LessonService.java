package com.example.lessonservice.services.interfaces;

import com.example.lessonservice.exceptions.lesson.NoLessonWithSuchIdFound;
import com.example.lessonservice.models.Lesson;
import com.example.lessonservice.models.SubjectType;

import java.time.DayOfWeek;

public interface LessonService {

    Lesson getLessonById(Long id) throws NoLessonWithSuchIdFound;
    Iterable<Lesson> getAll();

    Lesson addLesson(Lesson.Time value, Long subjId, Long teachId, SubjectType subjectType, String weeks, String room, DayOfWeek of) throws Exception;
    Lesson addLesson(Lesson lesson);

    Lesson updateLesson(Long id, Lesson.Time value, Long subjId, Long teachId, SubjectType subjectType, String weeks, String room, DayOfWeek of) throws Exception;
    Lesson updateLesson(Lesson lesson) throws NoLessonWithSuchIdFound;

    void deleteLesson(Long id) throws NoLessonWithSuchIdFound;

    boolean lessonExistsById(Long id);

}
