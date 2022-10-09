package com.example.practice1.services.implementations;

import com.example.practice1.exceptions.specialty.SpecialtyInstanceAlreadyExistsException;
import com.example.practice1.exceptions.specialty.SpecialtyNotFoundException;
import com.example.practice1.models.Lesson;
import com.example.practice1.models.Specialty;
import com.example.practice1.models.Subject;
import com.example.practice1.repositories.SpecialtyRepository;
import com.example.practice1.services.interfaces.SpecialtyService;
import com.example.practice1.services.interfaces.SubjectService;
import com.example.practice1.utils.Markers;
import com.example.practice1.utils.Utils;
import com.example.practice1.utils.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private SpecialtyRepository specialtyRepository;

    @Autowired
    private SubjectService subjectService;

    private static Logger logger = LogManager.getLogger(SpecialtyServiceImpl.class);

    private Utils processor;

    @Autowired
    public void setProcessor(Utils processor) {
        this.processor = processor;
    }

    @Autowired
    public void setSpecialtyRepository(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }


    @CacheEvict(cacheNames = "allSpecialties", allEntries = true)
    @Override
    public Specialty addSpecialty(String name, int year) {
        name = processor.processName(name);
        processor.checkName(name);
        processor.checkYear(year);
        if(specialtyRepository.existsByNameAndYear(name, year)) {
            logger.error(Markers.SAVE_SPECIALTY_MARKER,"Specialty '{}'-'{}' already exists. Specialty has not been added!",name,year);
            throw new SpecialtyInstanceAlreadyExistsException(Values.SPECIALTY_ALREADY_EXISTS);
        }
        Specialty s = specialtyRepository.save(new Specialty(name,year));
        logger.info(Markers.SAVE_SPECIALTY_MARKER,"Specialty '{}'-'{}' has been successfully added!",name,year);
        return s;
    }


    private void deleteSubjects(Long spId) {
        Iterable<Subject> subjects = subjectService.getAll();
        for (Subject s: subjects) {
            if (s.hasOnlyOneSpecialty() && s.hasSpecialty(spId)) {
                subjectService.deleteSubject(s.getId());
            }
        }
    }

    @Caching(evict = { @CacheEvict(cacheNames = "allSpecialties", allEntries = true),
            @CacheEvict(cacheNames = "specialties", key = "#id")})
    @CacheEvict(cacheNames = {"subjects", "allSubjects", "teachers", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Transactional
    @Override
    public void deleteSpecialty(Long id) {
        if(specialtyRepository.existsById(id)) {
            specialtyRepository.deleteById(id);
            deleteSubjects(id);
            logger.info(Markers.DELETE_SPECIALTY_MARKER, "Specialty has been successfully deleted!");
        }
        else throw new SpecialtyNotFoundException(id);
    }

    @CachePut(cacheNames = "specialties", key = "#id")
    @CacheEvict(cacheNames = {"allSpecialties", "subjects", "allSubjects", "teachers", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Override
    public Specialty updateSpecialty(long id, String name, int year) {
        logger.info(Markers.SPECIALTY_CACHING_MARKER, "Specialty {}-{} id={} updated in cache",id,name,year);
        name = processor.processName(name);
        processor.checkName(name);
        processor.checkYear(year);
        if(specialtyRepository.existsByNameAndYearAndNotId(id,name,year)){
            logger.error(Markers.UPDATE_SPECIALTY_MARKER,"Specialty '{}'-'{}' already exists. Specialty has not been updated!",name,year);
            throw new SpecialtyInstanceAlreadyExistsException(Values.SPECIALTY_ALREADY_EXISTS);
        }
        String finalName = name;
        return specialtyRepository.findById(id).map((specialty) -> {
            if(nothingChanged(specialty,finalName,year)) {
                logger.info(Markers.UPDATE_SPECIALTY_MARKER,"Specialty has not been changed as the new specialty is the exact old specialty!");
                return specialty;
            }
            specialty.setName(finalName);
            specialty.setYear(year);
            Specialty s = specialtyRepository.save(specialty);
            logger.info(Markers.UPDATE_SPECIALTY_MARKER,"Specialty has been successfully updated to '{}'-'{}'!", finalName,year);
            return s;
        }).orElseGet(() -> {
            return specialtyRepository.save(new Specialty(id,finalName,year));
        });
    }


    private boolean nothingChanged(Specialty specialty, String name, int year) {
        return specialty.getName().equals(name)&&specialty.getYear()==year;
    }

    @Cacheable(cacheNames = "allSpecialties")
    @Override
    public Iterable<Specialty> getAll() {
        logger.info(Markers.SPECIALTY_CACHING_MARKER, "get all specialties method executed");
        List<Specialty> specialties = (List<Specialty>)specialtyRepository.findAll();
        Collections.sort(specialties);
        return specialties;
    }

    @Cacheable(cacheNames = "specialties", key = "#id")
    @Override
    public Specialty getSpecialty(Long id) {
        logger.info(Markers.SPECIALTY_CACHING_MARKER, "get specialty by id executed");
        return specialtyRepository.findById(id).orElseThrow(() -> new SpecialtyNotFoundException(id));
    }

    @Scheduled(fixedDelay = 120000)
    @CacheEvict(cacheNames = "allSpecialties", allEntries = true)
    public void clearAllSpecialtiesCache() {
        logger.info(Markers.SPECIALTY_CACHING_MARKER, "SCHEDULED REMOVAL: All specialties list removed from cache");
    }

    @Scheduled(cron = "0 */2 * ? * *")
    @CacheEvict(cacheNames = "specialties", allEntries = true)
    public void clearSpecialtiesCache() {
        logger.info(Markers.SPECIALTY_CACHING_MARKER, "SCHEDULED REMOVAL: All specific specialties removed from cache");
    }


    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects", "teachers", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Override
    public void deleteAll() {
        for (Specialty s : getAll()) {
            specialtyRepository.deleteById(s.getId());
            deleteSubjects(s.getId());
        }
    }

    @CacheEvict(cacheNames = "allSpecialties", allEntries = true)
    @Override
    public Specialty addSpecialty(String name, int year, JSONArray subjectIds) {
        Set<Subject> subjects = new HashSet<>();
        for(int i = 0; i < subjectIds.length(); i++){
            try {
                subjects.add(subjectService.getSubjectById(subjectIds.getLong(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Specialty specialty = addSpecialty(name,year);
        subjects.forEach(s -> {s.addSpecialty(specialty);
        subjectService.updateSubjectNoCheck(s);});
        specialty.setSubjects(subjects);
        return specialty;
    }

    @Override
    public Iterable<Subject> getSpecialtySubjects(Long specialtyId) {
        return getSpecialty(specialtyId).getSubjects();
    }

    @Override
    public List<Lesson> getSpecialtyLessons(Long id) {
        List<Lesson> lessons = new ArrayList<Lesson>();
        for (Subject s : getSpecialtySubjects(id)) {
            lessons.addAll(s.getLessons());
        }
        return lessons;
    }


}
