package com.example.mainservice.services.interfaces;

import com.example.mainservice.exceptions.teacher.TeacherNotFoundException;
import com.example.mainservice.models.Subject;
import com.example.mainservice.models.Teacher;

import java.util.Set;


public interface TeacherService {

    Teacher addTeacher(String name, Set<Subject> subjects);
    Teacher addTeacher(Teacher teacher);

    boolean teacherExistsById(Long id);
    boolean teacherExistsByName(String name);

    boolean deleteTeacher(Long id) throws TeacherNotFoundException;
    Teacher updateTeacher(Long id, String name, Set<Subject> subjects);
    Teacher updateTeacher(Teacher teacher);
    Teacher updateTeacherNoCheck(Teacher teacher);
    Teacher getTeacherById(Long id) throws TeacherNotFoundException;

    Iterable<Teacher> getTeacherByPartName(String name) throws Exception;
    Teacher getTeacherByName(String name) throws Exception;
    Iterable<Teacher> getAll();


}
