package com.example.lessonservice.utils;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Markers {

    public static final Marker IMPORTANT_MARKER = MarkerManager.getMarker("IMPORTANT");
    public static final Marker USER_ACTIVITY = MarkerManager.getMarker("USER_ACTIVITY");

    public static final Marker ALTERING_LESSON_TABLE_MARKER = MarkerManager.getMarker("ALTERING_LESSON_TABLE")
            .setParents(IMPORTANT_MARKER);
    public static final Marker DELETE_LESSON_MARKER = MarkerManager.getMarker("DELETE_LESSON")
            .setParents(ALTERING_LESSON_TABLE_MARKER);

    public static final Marker USER_MARKER = MarkerManager.getMarker("USER_ACT")
    		.setParents(IMPORTANT_MARKER);

    public static final Marker ALTERING_SPECIALTY_TABLE_MARKER = MarkerManager.getMarker("ALTERING_SPECIALTY_TABLE")
            .setParents(IMPORTANT_MARKER);
    public static final Marker DELETE_SPECIALTY_MARKER = MarkerManager.getMarker("DELETE_SPECIALTY")
            .setParents(ALTERING_SPECIALTY_TABLE_MARKER);
    public static final Marker UPDATE_SPECIALTY_MARKER = MarkerManager.getMarker("UPDATE_SPECIALTY")
            .setParents(ALTERING_SPECIALTY_TABLE_MARKER);
    public static final Marker SAVE_SPECIALTY_MARKER = MarkerManager.getMarker("SAVE_SPECIALTY")
            .setParents(ALTERING_SPECIALTY_TABLE_MARKER);

    public static final Marker ALTERING_SUBJECT_TABLE_MARKER = MarkerManager.getMarker("ALTERING_SUBJECT_TABLE")
            .setParents(IMPORTANT_MARKER);
    public static final Marker UPDATE_SUBJECT_MARKER = MarkerManager.getMarker("UPDATE_SUBJECT")
            .setParents(ALTERING_SUBJECT_TABLE_MARKER);
    public static final Marker DELETE_SUBJECT_MARKER = MarkerManager.getMarker("DELETE_SUBJECT")
            .setParents(ALTERING_SUBJECT_TABLE_MARKER);

    public static final Marker ALTERING_TEACHER_TABLE_MARKER = MarkerManager.getMarker("ALTERING_TEACHER_TABLE")
            .setParents(IMPORTANT_MARKER);
    public static final Marker ADD_TEACHER_MARKER = MarkerManager.getMarker("ADD_TEACHER")
            .setParents(ALTERING_TEACHER_TABLE_MARKER);
    public static final Marker DELETE_TEACHER_MARKER = MarkerManager.getMarker("DELETE_TEACHER")
            .setParents(ALTERING_TEACHER_TABLE_MARKER);
    public static final Marker UPDATE_TEACHER_MARKER = MarkerManager.getMarker("UPDATE_TEACHER")
            .setParents(ALTERING_TEACHER_TABLE_MARKER);
    public static final Marker SAVE_TEACHER_MARKER = MarkerManager.getMarker("SAVE_TEACHER")
            .setParents(ALTERING_TEACHER_TABLE_MARKER);


    public static final Marker ADD_METHOD_INVOKED_MARKER = MarkerManager.getMarker("ADD_METHOD_INVOKED")
            .setParents(IMPORTANT_MARKER);
    public static final Marker GET_METHOD_INVOKED_MARKER = MarkerManager.getMarker("GET_METHOD_INVOKED")
            .setParents(IMPORTANT_MARKER);
    public static final Marker DELETE_METHOD_INVOKED_MARKER = MarkerManager.getMarker("DELETE_METHOD_INVOKED")
            .setParents(IMPORTANT_MARKER);
    public static final Marker METHOD_INVOKE_TIME_MARKER = MarkerManager.getMarker("METHOD_INVOKE_TIME_MARKER")
            .setParents(IMPORTANT_MARKER);
    public static final Marker UPDATE_METHOD_INVOKED_MARKER = MarkerManager.getMarker("UPDATE_METHOD_INVOKED")
            .setParents(IMPORTANT_MARKER);


    public static final Marker SPECIALTY_CACHING_MARKER = MarkerManager.getMarker("SPECIALTY_CACHING")
            .setParents(IMPORTANT_MARKER);
    public static final Marker SUBJECT_CACHING_MARKER = MarkerManager.getMarker("SUBJECT_CACHING")
            .setParents(IMPORTANT_MARKER);
    public static final Marker TEACHER_CACHING_MARKER = MarkerManager.getMarker("TEACHER_CACHING")
            .setParents(IMPORTANT_MARKER);
    public static final Marker LESSON_CACHING_MARKER = MarkerManager.getMarker("LESSON_CACHING")
            .setParents(IMPORTANT_MARKER);
    public static final Marker USER_CACHING_MARKER = MarkerManager.getMarker("USER_CACHING")
            .setParents(IMPORTANT_MARKER);


    public static final Marker CUSTOM_CACHING_MARKER = MarkerManager.getMarker("CUSTOM_CACHING")
            .setParents(IMPORTANT_MARKER);

}
