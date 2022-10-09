package com.example.mainservice.exceptions.lesson;


public class InvalidLessonArgumentsException extends RuntimeException {

    public InvalidLessonArgumentsException(String attrib, String value) {
        super("Invalid lesson '"+attrib+"' attribute value: \""+value+"\"! Required pattern (example): 1-7,9,11-14");
    }

}
