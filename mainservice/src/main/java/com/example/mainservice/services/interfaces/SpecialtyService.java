package com.example.mainservice.services.interfaces;

import com.example.mainservice.models.Lesson;
import com.example.mainservice.models.Specialty;
import com.example.mainservice.models.Subject;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.List;

public interface SpecialtyService {

    Specialty addSpecialty(String name, int year);
    void deleteSpecialty(Long id);

    Specialty updateSpecialty(long id, String name, int year);
    Iterable<Specialty> getAll();
    Specialty getSpecialty(Long id);
    void deleteAll();
    Specialty addSpecialty(String name, int year, JSONArray subjects);
    Iterable<Subject> getSpecialtySubjects(Long specialtyId);

    List<Lesson> getSpecialtyLessons(Long id);
}
