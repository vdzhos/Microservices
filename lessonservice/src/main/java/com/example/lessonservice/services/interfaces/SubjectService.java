package com.example.lessonservice.services.interfaces;

import com.example.lessonservice.models.Lesson;
import com.example.lessonservice.models.Specialty;
import com.example.lessonservice.models.Subject;

import java.util.List;
import java.util.Set;

public interface SubjectService {

    Subject addSubject(String name, int quantOfGroups, Set<Specialty> specialties);
    Subject addSubject(Subject subject);
    void deleteSubject(Long id);
    Subject updateSubject(Long id, String name, int quantOfGroups, Set<Specialty> specialties);
    Subject updateSubject(Subject subject);
    Subject updateSubjectNoCheck(Subject subject);
    Iterable<Subject> getAll();
    Subject getSubjectByName(String name);
    Subject getSubjectById(Long id);
    boolean subjectExistsById(Long id);
    boolean subjectExistsByName(String name);

    List<Lesson> getSubjectLessons(Long subjectId);

    Set<Integer> getLessonWeeks(Long id);

    Set<Integer> getLessonWeeks(Set<Long> ids);

}
