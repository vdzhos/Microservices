package com.example.mainservice.controllers;

import com.example.mainservice.exceptions.FailedToGetException;
import com.example.mainservice.exceptions.subject.NoSubjectWithSuchIdToDelete;
import com.example.mainservice.exceptions.subject.SubjectIllegalArgumentException;
import com.example.mainservice.models.Subject;
import com.example.mainservice.models.Teacher;
import com.example.mainservice.services.implementations.SubjectServiceImpl;
import com.example.mainservice.utils.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/REST/subjects")
public class SubjectRestController {

    private static final String LESSON_SERVICE_URL = "https://rude-icons-flash-94-244-36-128.loca.lt/REST/";

    @Autowired
    private SubjectServiceImpl subjectService;
    private static Logger logger = LogManager.getLogger(SubjectRestController.class);

    RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return false;
        }
        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException { }
    }).build();

    @GetMapping("/all")
    public Iterable<Subject> getSubjects(){
        return subjectService.getAll();
    }

    @GetMapping("/getByName/{name}")
    public Subject getSubject(@PathVariable("name") String name){
        return subjectService.getSubjectByName(name);
    }

    @GetMapping("/{id}")
    public Subject getSubject(@PathVariable("id") Long id) {
        return subjectService.getSubjectById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable("id") Long id) {
        String url = LESSON_SERVICE_URL + "lessons/deleteBySubjectId/" + id.toString();
        ResponseEntity<String> response = restTemplate.postForEntity(url, "", String.class);
        if(response.getStatusCode() != HttpStatus.OK)
            throw new FailedToGetException("Failed to delete lessons of teacher with id " + id.toString() + "!");
        subjectService.deleteSubject(id);
        logger.info(Markers.DELETE_SUBJECT_MARKER,"Subject has been successfully deleted!");
    }

    @PostMapping("/add")
    public Subject addSubject(@Valid @RequestBody Subject subject){
        Subject s = subjectService.addSubject(subject);
        logger.info(Markers.ALTERING_SUBJECT_TABLE_MARKER,"Subject {} with {} groups has been successfully added!", subject.getName(), subject.getQuantOfGroups());
        return s;
    }

    @PutMapping("/{id}")
    public Subject updateSubject(@PathVariable("id") Long id, @Valid @RequestBody Subject subject) {
        subject.setId(id);
        Subject s = subjectService.updateSubject(subject);
        logger.info(Markers.UPDATE_SUBJECT_MARKER,"Subject has been successfully updated!");
        return s;
    }

    @PostMapping("/deleteLessonFromSubject/{subjectId}/{lessonId}")
    public Subject deleteSubjectLesson(@PathVariable(value = "subjectId") Long subjectId, @PathVariable(value = "lessonId") Long lessonId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Long> lessons = subject.getLessons();
        lessons.remove(lessonId);
        subject.setLessons(lessons);
        return subjectService.updateSubject(subject);
    }

    @PostMapping("/addLessonForSubject/{subjectId}/{lessonId}")
    public Subject addSubjectLesson(@PathVariable(value = "subjectId") Long subjectId, @PathVariable(value = "lessonId") Long lessonId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Long> lessons = subject.getLessons();
        lessons.add(lessonId);
        subject.setLessons(lessons);
        return subjectService.updateSubject(subject);
    }

    @ExceptionHandler(NoSubjectWithSuchIdToDelete.class)
    public ResponseEntity<Map<String,String>> handleException(NoSubjectWithSuchIdToDelete ex){
        Map<String, String> result = new HashMap<>();
        result.put("deleted", "false");
        result.put("error", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {SubjectIllegalArgumentException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,String>> handleOtherExceptions(SubjectIllegalArgumentException e){
        Map<String,String> map = new HashMap<>();
        map.put("success","false");
        map.put("error",e.getMessage());
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

}
