package com.example.mainservice.controllers;

import com.example.mainservice.exceptions.FailedToGetException;
import com.example.mainservice.exceptions.teacher.TeacherAlreadyExistsException;
import com.example.mainservice.exceptions.teacher.TeacherIllegalArgumentException;
import com.example.mainservice.exceptions.teacher.TeacherNotFoundException;
import com.example.mainservice.models.Teacher;
import com.example.mainservice.services.interfaces.TeacherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/REST/teachers")
public class TeacherControllerREST {

    private static final String LESSON_SERVICE_URL = "https://rude-icons-flash-94-244-36-128.loca.lt/REST/";

    private TeacherService teacherService;

    RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return false;
        }
        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException { }
    }).build();

    @Autowired
    public TeacherControllerREST(TeacherService teacherService)
    {
        this.teacherService = teacherService;
    }

    @GetMapping("/all")
    public List<Teacher> getAllTeachers() {
        return (List<Teacher>) teacherService.getAll();
    }

    @GetMapping("/{id}")
    public Teacher getTeacherById(@PathVariable(value = "id") Long id) throws Exception {
        return teacherService.getTeacherById(id);
    }

    @PostMapping("/add")
    public Teacher addTeacher(@Valid @RequestBody Teacher teacher){
        return teacherService.addTeacher(teacher);
    }

    @PutMapping("/{id}")
    public Teacher updateTeacher(@PathVariable(value = "id") Long id, @Valid @RequestBody Teacher teacher) throws TeacherNotFoundException {
        teacher.setId(id);
        return teacherService.updateTeacher(teacher);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteTeacher(@PathVariable(value = "id") Long id) throws TeacherNotFoundException {
        String url = LESSON_SERVICE_URL + "lessons/deleteByTeacherId/" + id.toString();
        ResponseEntity<String> response = restTemplate.postForEntity(url, "", String.class);
        if(response.getStatusCode() != HttpStatus.OK)
            throw new FailedToGetException("Failed to delete lessons of teacher with id " + id.toString() + "!");
        teacherService.deleteTeacher(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted",true);
        return result;
    }

    @GetMapping("/withLessons/{id}")
    public Map<String, Object> getTeacherAndAllHisLessonsById(@PathVariable(value = "id") Long id) throws Exception {
        Teacher teacher = teacherService.getTeacherById(id);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};
        Map<String, Object> teacherMap = mapper.convertValue(teacher,typeRef);
        List<HashMap<String,Object>> lessonsMaps = getAllLessonsByTeacherId(teacher.getId());
        HashMap<String,Object> map = new HashMap<>();
        map.put("teacher",teacherMap);
        map.put("lessons",lessonsMaps);
        return map;
    }

    private List<HashMap<String,Object>> getAllLessonsByTeacherId(Long id) throws JsonProcessingException {
        String url = LESSON_SERVICE_URL + "lessons/teacherId/" + id.toString();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if(response.getStatusCode() != HttpStatus.OK)
            throw new FailedToGetException("Failed to fetch lessons of teacher with id " + id.toString() + "!");
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<HashMap<String,Object>>> typeRef = new TypeReference<>() {};
        return mapper.readValue(response.getBody(),typeRef);
    }

    @PostMapping("/deleteLessonFromTeacher/{teacherId}/{lessonId}")
    public Teacher deleteTeacherLesson(@PathVariable(value = "teacherId") Long teacherId, @PathVariable(value = "lessonId") Long lessonId) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        List<Long> lessons = teacher.getLessons();
        lessons.remove(lessonId);
        teacher.setLessons(lessons);
        return teacherService.updateTeacher(teacher);
    }

    @PostMapping("/addLessonForTeacher/{teacherId}/{lessonId}")
    public Teacher addTeacherLesson(@PathVariable(value = "teacherId") Long teacherId, @PathVariable(value = "lessonId") Long lessonId) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        List<Long> lessons = teacher.getLessons();
        lessons.add(lessonId);
        teacher.setLessons(lessons);
        return teacherService.updateTeacher(teacher);
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleException(TeacherNotFoundException ex){
        Map<String, String> result = new HashMap<>();
        result.put("success", "false");
        result.put("error", ex.getMessage());
        return result;
    }

    @ExceptionHandler(value = {TeacherIllegalArgumentException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,String>> handleOtherExceptions(TeacherIllegalArgumentException e){
        Map<String,String> map = new HashMap<>();
        map.put("success","false");
        map.put("error",e.getMessage());
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {TeacherAlreadyExistsException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,String>> handleOtherExceptions(TeacherAlreadyExistsException e){
        Map<String,String> map = new HashMap<>();
        map.put("success","false");
        map.put("error",e.getMessage());
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(MethodArgumentNotValidException ex){
        Map<String, String> result = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            result.put(fieldName, errorMessage);
        });
        return result;
    }
}
