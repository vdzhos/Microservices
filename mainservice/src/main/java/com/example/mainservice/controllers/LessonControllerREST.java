package com.example.mainservice.controllers;

import com.example.mainservice.exceptions.lesson.NoLessonWithSuchIdFound;
import com.example.mainservice.models.Lesson;
import com.example.mainservice.services.interfaces.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/REST/lessons")
public class LessonControllerREST {

    private LessonService lessonService;

    @Autowired
    public LessonControllerREST(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/all")
    public List<Lesson> getAllLessons() {
        return (List<Lesson>) lessonService.getAll();
    }

    @GetMapping("/{id}")
    public Lesson getLessonById(@PathVariable(value = "id") Long id) throws Exception {
        return lessonService.getLessonById(id);
    }

    @PostMapping("/add")
    public Lesson addLesson(@Valid @RequestBody Lesson lesson){
        return lessonService.addLesson(lesson);
    }

    @PutMapping("/{id}")
    public Lesson updateLesson(@PathVariable(value = "id") Long id, @Valid @RequestBody Lesson lesson) throws NoLessonWithSuchIdFound {
        lesson.setId(id);
        return lessonService.updateLesson(lesson);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteLesson(@PathVariable(value = "id") Long id) throws NoLessonWithSuchIdFound {
        lessonService.deleteLesson(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted",true);
        return result;
    }

    @ExceptionHandler(NoLessonWithSuchIdFound.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleException(NoLessonWithSuchIdFound ex){
        Map<String, String> result = new HashMap<>();
        result.put(ex.getAction(), "false");
        result.put("error", ex.getMessage());
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(MethodArgumentNotValidException ex){
        Map<String, String> result = new HashMap<>();
        result.put("success", "false");
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            result.put(fieldName, errorMessage);
        });
        return result;
    }

}
