package com.example.lessonservice.controllers;

import com.example.lessonservice.exceptions.teacher.TeacherAlreadyExistsException;
import com.example.lessonservice.exceptions.teacher.TeacherIllegalArgumentException;
import com.example.lessonservice.exceptions.teacher.TeacherNotFoundException;
import com.example.lessonservice.models.Teacher;
import com.example.lessonservice.services.interfaces.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/REST/teachers")
public class TeacherControllerREST {

    private TeacherService teacherService;

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
        teacherService.deleteTeacher(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted",true);
        return result;
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
