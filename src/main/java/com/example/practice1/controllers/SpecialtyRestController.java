package com.example.practice1.controllers;

import com.example.practice1.exceptions.specialty.IncorrectRequestBodyException;
import com.example.practice1.models.Specialty;
import com.example.practice1.services.interfaces.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/REST/specialties")
public class SpecialtyRestController {

    private SpecialtyService specialtyService;

    @Autowired
    public SpecialtyRestController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping("/all")
    public Iterable<Specialty> getAllSpecialties(){
        return specialtyService.getAll();
    }

    @GetMapping("/{id}")
    public Specialty getSpecialty(@Min(1) @PathVariable(name = "id") Long id){
        return specialtyService.getSpecialty(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Specialty addSpecialty(@RequestBody String json){
        try {
            JSONObject specialty = new JSONObject(json);
            String name = specialty.getString("name");
            int year = specialty.getInt("year");
            JSONArray array = specialty.getJSONArray("subjects");
            return specialtyService.addSpecialty(name,year,array);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new IncorrectRequestBodyException(""+e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Specialty updateSpecialty(@Valid @RequestBody Specialty newSpecialty, @Min(1) @PathVariable(name = "id") Long id) {
        return specialtyService.updateSpecialty(id,newSpecialty.getName(),newSpecialty.getYear());
    }

    @DeleteMapping("/{id}")
    public void deleteSpecialty(@PathVariable(name = "id") @Min(1) Long id){
        specialtyService.deleteSpecialty(id);
    }

    @DeleteMapping
    public void deleteAll(){
        specialtyService.deleteAll();
    }

}