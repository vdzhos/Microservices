package com.example.lessonservice.utils;

public class Values {

    public static final int MAX_YEAR = 6;
    public static final int MIN_YEAR = 1;

    public static final String INVALID_SPECIALTY_NAME = "Incorrect specialty name!";
    public static final String SPECIALTY_ALREADY_EXISTS = "Specialty with such parameters already exists!";

    public static final String TEACHER_ALREADY_EXISTS = "Teacher with such name already exists!";
    public static final String FULL_NAME_PATTERN = "(?=^.{0,40}$)^([a-zA-ZА-Яа-яІіЇїЄє]+([-']{1}[a-zA-ZА-Яа-яІіЇїЄє]+)?)\\s([a-zA-ZА-Яа-яІіЇїЄє]+([-']{1}[a-zA-ZА-Яа-яІіЇїЄє]+)?)(\\s([a-zA-ZА-Яа-яІіЇїЄє]+([-']{1}[a-zA-ZА-Яа-яІіЇїЄє]+)?))?$";

    public static final int MIN_QUANT_OF_GROUPS = 1;
    public static final int MAX_QUANT_OF_GROUPS = 50;
    public static final String INVALID_SUBJECT_NAME = "Incorrect subject name!";
    public static final int MIN_QUANT_OF_SPECIALTIES_ON_SUBJECT = 1;
    public static final int MIN_QUANT_OF_SUBJECTS_FOR_TEACHER = 1;

}
