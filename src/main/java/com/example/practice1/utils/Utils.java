package com.example.practice1.utils;

import com.example.practice1.models.Specialty;
import com.example.practice1.models.Subject;

import java.util.Set;

public interface Utils {

    String processName(String name);

    boolean isInvalidName(String name);

    void checkName(String name);

    void checkSubjectName(String name);

    void checkTeacherName(String name);

    Long getUniqueId();

    void checkYear(int year);

    void checkQuantOfGroups(int quantOfGroups);

    void checkQuantOfSpecialties(int quantOfSpecialties);

    void checkSpecialties(Iterable<Subject> subjects, Set<Specialty> specialties);

    void checkTeachersSubjects (Set<Subject> subjects);
}
