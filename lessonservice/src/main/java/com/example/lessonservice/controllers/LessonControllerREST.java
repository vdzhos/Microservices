package com.example.lessonservice.controllers;

import com.example.lessonservice.exceptions.FailedToExecute;
import com.example.lessonservice.exceptions.NoLessonWithSuchIdFound;
import com.example.lessonservice.exceptions.NotFoundException;
import com.example.lessonservice.models.Lesson;
import com.example.lessonservice.services.interfaces.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/REST/lessons")
public class LessonControllerREST {

    private static final String MAIN_SERVICE_URL = "https://a963-94-244-36-128.eu.ngrok.io/REST/";

    private LessonService lessonService;

    RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return false;
        }
        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException { }
    }).build();

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
    public Lesson addLesson(@Valid @RequestBody Lesson lesson) throws NoLessonWithSuchIdFound {
        verifySubjectAndTeacherExists(lesson);
        Lesson added = lessonService.addLesson(lesson);
        String url1 = MAIN_SERVICE_URL + "teachers/addLessonForTeacher/" + added.getTeacher().toString() + "/" + added.getId().toString();
        ResponseEntity<String> response1 = restTemplate.postForEntity(url1, "", String.class);
        if(response1.getStatusCode() != HttpStatus.OK){
            lessonService.deleteLesson(added.getId());
            throw new FailedToExecute("Failed to add lesson for teacher with id  " + lesson.getTeacher().toString() + "!");
        }
        String url2 = MAIN_SERVICE_URL + "subjects/addLessonForSubject/" + added.getSubject().toString() + "/" + added.getId().toString();
        ResponseEntity<String> response2 = restTemplate.postForEntity(url2, "", String.class);
        if(response2.getStatusCode() != HttpStatus.OK){
            lessonService.deleteLesson(added.getId());
            throw new FailedToExecute("Failed to add lesson for subject with id  " + lesson.getSubject().toString() + "!");
        }
        return added;
    }

    @PutMapping("/{id}")
    public Lesson updateLesson(@PathVariable(value = "id") Long id, @Valid @RequestBody Lesson lesson) throws NoLessonWithSuchIdFound {
        verifySubjectAndTeacherExists(lesson);
        lesson.setId(id);
        return lessonService.updateLesson(lesson);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteLesson(@PathVariable(value = "id") Long id) throws NoLessonWithSuchIdFound {
        Lesson lesson = lessonService.getLessonById(id);

        String url1 = MAIN_SERVICE_URL + "teachers/deleteLessonFromTeacher/" + lesson.getTeacher().toString() + "/" + id.toString();
        ResponseEntity<String> response1 = restTemplate.postForEntity(url1,"", String.class);
        if(response1.getStatusCode() != HttpStatus.OK)
            throw new FailedToExecute("Failed to delete lesson from teacher with id  " + lesson.getTeacher().toString() + "!");

        String url2 = MAIN_SERVICE_URL + "subjects/deleteLessonFromSubject/" + lesson.getSubject().toString() + "/" + id.toString();
        ResponseEntity<String> response2 = restTemplate.postForEntity(url2,"", String.class);
        if(response2.getStatusCode() != HttpStatus.OK)
            throw new FailedToExecute("Failed to delete lesson from subject with id  " + lesson.getSubject().toString() + "!");

        lessonService.deleteLesson(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted",true);
        return result;
    }

    @GetMapping("/teacherId/{id}")
    public List<Lesson> getAllLessonsByTeacherId(@PathVariable(value = "id") Long teacherId) {
        return (List<Lesson>) lessonService.getByTeacherId(teacherId);
    }

    @PostMapping("/deleteByTeacherId/{id}")
    public Map<String, Boolean> deleteLessonsByTeacherId(@PathVariable(value = "id") Long id) {
        lessonService.deleteLessonsByTeacherId(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted",true);
        return result;
    }

    @PostMapping("/deleteBySubjectId/{id}")
    public Map<String, Boolean> deleteLessonsBySubjectId(@PathVariable(value = "id") Long id) {
        lessonService.deleteLessonsBySubjectId(id);
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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleException(NotFoundException ex){
        Map<String, String> result = new HashMap<>();
        result.put("error", ex.getMessage());
        return result;
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleException(RestClientException ex){
        Map<String, String> result = new HashMap<>();
        result.put("error", "Failed to connect to main service!");
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

    private void verifySubjectAndTeacherExists(Lesson lesson){
        String url1 = MAIN_SERVICE_URL + "teachers/" + lesson.getTeacher().toString();
        ResponseEntity<String> response1 = restTemplate.getForEntity(url1, String.class);
        if(response1.getStatusCode() != HttpStatus.OK)
            throw new NotFoundException("No teacher with id " + lesson.getTeacher().toString() + "!");

        String url2 = MAIN_SERVICE_URL + "subjects/" + lesson.getSubject().toString();
        ResponseEntity<String> response2 = restTemplate.getForEntity(url2, String.class);
        if(response2.getStatusCode() != HttpStatus.OK)
            throw new NotFoundException("No subject with id " + lesson.getSubject().toString() + "!");
    }

}
