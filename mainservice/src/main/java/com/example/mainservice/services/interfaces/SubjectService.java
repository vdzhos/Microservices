package com.example.mainservice.services.interfaces;

import com.example.mainservice.models.Specialty;
import com.example.mainservice.models.Subject;

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

    List<Long> getSubjectLessons(Long subjectId);


}
