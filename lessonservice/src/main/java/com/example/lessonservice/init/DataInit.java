package com.example.lessonservice.init;

import com.example.lessonservice.models.*;
import com.example.lessonservice.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
@Profile("dev")
public class DataInit implements ApplicationRunner {

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        addLessons();
    }

    private void addLessons(){
        Lesson l1 = new Lesson(Lesson.Time.TIME1, 4L, 7L, new SubjectType(0), "1-15", new Room("215"), DayOfWeek.MONDAY);
        Lesson l2 = new Lesson(Lesson.Time.TIME2, 4L, 8L, new SubjectType(1), "1-15", new Room("216"), DayOfWeek.MONDAY);
        Lesson l3 = new Lesson(Lesson.Time.TIME3, 5L, 8L, new SubjectType(2), "1-15", new Room("216"), DayOfWeek.MONDAY);

        lessonRepository.save(l1);
        lessonRepository.save(l2);
        lessonRepository.save(l3);
    }


}
