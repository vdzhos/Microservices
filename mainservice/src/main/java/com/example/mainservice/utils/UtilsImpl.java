package com.example.mainservice.utils;

import com.example.mainservice.exceptions.specialty.InvalidSpecialtyNameException;
import com.example.mainservice.exceptions.specialty.SpecialtyIllegalArgumentException;
import com.example.mainservice.exceptions.subject.InvalidSubjectNameException;
import com.example.mainservice.exceptions.subject.SubjectIllegalArgumentException;
import com.example.mainservice.exceptions.teacher.TeacherIllegalArgumentException;
import com.example.mainservice.models.Specialty;
import com.example.mainservice.models.Subject;

import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsImpl  implements Utils {

    @Override
    public String processName(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

    @Override
    public boolean isInvalidName(String name) {
        return name.isEmpty();
    }

    @Override
    public void checkName(String name) {
        if (isInvalidName(name)) {
            throw new InvalidSpecialtyNameException(Values.INVALID_SPECIALTY_NAME);
        }
    }

    @Override
    public void checkSubjectName(String name) {
        if (isInvalidName(name)) {
            throw new InvalidSubjectNameException(Values.INVALID_SUBJECT_NAME);
        }
    }

    @Override
    public void checkTeacherName(String name) {
        Pattern pattern = Pattern.compile(Values.FULL_NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        if(!matcher.matches())
            throw new TeacherIllegalArgumentException("Incorrect name: " + name + " for a teacher!");
    }

    @Override
    public Long getUniqueId() {
        return new Random().nextLong() + System.currentTimeMillis();
    }

    @Override
    public void checkYear(int year) {
        if (year > Values.MAX_YEAR || year < Values.MIN_YEAR) {
            throw new SpecialtyIllegalArgumentException("Specialty year = " + year + " is out of bounds " + Values.MIN_YEAR + " - " + Values.MAX_YEAR);
        }
    }

    @Override
    public void checkQuantOfGroups(int quantOfGroups) {
        if (quantOfGroups > Values.MAX_QUANT_OF_GROUPS || quantOfGroups < Values.MIN_QUANT_OF_GROUPS) {
            throw new SubjectIllegalArgumentException("Subject quantity of groups = " + quantOfGroups + " is out of bounds " + Values.MIN_QUANT_OF_GROUPS + " - " + Values.MAX_QUANT_OF_GROUPS);
        }
    }

    @Override
    public void checkQuantOfSpecialties(int quantOfSpecialties) {
        if (quantOfSpecialties < Values.MIN_QUANT_OF_SPECIALTIES_ON_SUBJECT) {
            throw new SubjectIllegalArgumentException("Subject quantity of specialties = " + quantOfSpecialties + " is incorrect - less than " + Values.MIN_QUANT_OF_GROUPS);
        }
    }

    @Override
    public void checkSpecialties(Iterable<Subject> subjects, Set<Specialty> specialties) {
        for (Subject subject : subjects) {
            for (Specialty specialty : subject.getSpecialties()) {
                if (specialties.contains(specialty))
                    throw new SubjectIllegalArgumentException("Subject with such name already exists on specialty " + specialty);
            }
        }
    }

    @Override
    public void checkTeachersSubjects(Set<Subject> subjects)
    {
        int quant = subjects == null ? 0 : subjects.size();
        if(quant < Values.MIN_QUANT_OF_SUBJECTS_FOR_TEACHER)
            throw new TeacherIllegalArgumentException("Subject quantity for Teacher = " + quant + " is incorrect - less than " + Values.MIN_QUANT_OF_SUBJECTS_FOR_TEACHER);
    }

}
