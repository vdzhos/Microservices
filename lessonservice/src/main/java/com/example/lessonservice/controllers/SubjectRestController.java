package com.example.lessonservice.controllers;

import com.example.lessonservice.exceptions.subject.NoSubjectWithSuchIdToDelete;
import com.example.lessonservice.exceptions.subject.SubjectIllegalArgumentException;
import com.example.lessonservice.models.Subject;
import com.example.lessonservice.services.implementations.SubjectServiceImpl;
import com.example.lessonservice.utils.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/REST/subjects")
public class SubjectRestController {

    @Autowired
    private SubjectServiceImpl subjectService;
    private static Logger logger = LogManager.getLogger(SubjectRestController.class);

    @GetMapping("/all")
    public Iterable<Subject> getSubjects(){
        return subjectService.getAll();
    }

    @GetMapping("/getByName/{name}")
    public Subject getSubject(@PathVariable("name") String name){
        return subjectService.getSubjectByName(name);
    }

    @GetMapping("/get/{id}")
    public Subject getSubject(@PathVariable("id") Long id) {
        return subjectService.getSubjectById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSubject(@PathVariable("id") Long id) {
        subjectService.deleteSubject(id);
        logger.info(Markers.DELETE_SUBJECT_MARKER,"Subject has been successfully deleted!");
    }

    @PostMapping("/add")
    public Subject addSubject(@Valid @RequestBody Subject subject){
        Subject s = subjectService.addSubject(subject);
        logger.info(Markers.ALTERING_SUBJECT_TABLE_MARKER,"Subject {} with {} groups has been successfully added!", subject.getName(), subject.getQuantOfGroups());
        return s;
    }

    @PutMapping("/update/{id}")
    public Subject updateSubject(@PathVariable("id") Long id, @Valid @RequestBody Subject subject) {
        subject.setId(id);
        Subject s = subjectService.updateSubject(subject);
        logger.info(Markers.UPDATE_SUBJECT_MARKER,"Subject has been successfully updated!");
        return s;
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
