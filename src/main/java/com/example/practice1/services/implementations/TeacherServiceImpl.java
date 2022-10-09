package com.example.practice1.services.implementations;

import com.example.practice1.exceptions.lesson.NoLessonWithSuchIdFound;
import com.example.practice1.exceptions.teacher.TeacherAlreadyExistsException;
import com.example.practice1.exceptions.teacher.TeacherNotFoundException;
import com.example.practice1.models.Lesson;
import com.example.practice1.models.Subject;
import com.example.practice1.models.Teacher;
import com.example.practice1.repositories.TeacherRepository;
import com.example.practice1.services.interfaces.LessonService;
import com.example.practice1.services.interfaces.TeacherService;
import com.example.practice1.utils.Markers;
import com.example.practice1.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    private TeacherRepository teacherRepository;
    private LessonService lessonService;
    private static Logger logger = LogManager.getLogger(TeacherServiceImpl.class);

    @Autowired
    private Utils processor;

    @Autowired
    public void setTeacherRepository(TeacherRepository teacherRepository, LessonService lessonService) {
        this.teacherRepository = teacherRepository;
        this.lessonService = lessonService;
    }

    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects", "allTeachers"}, allEntries = true)
    @Override
    public Teacher addTeacher(String name, Set<Subject> subjects) {
        name = processor.processName(name);
        processor.checkTeacherName(name);
        processor.checkTeachersSubjects(subjects);
        if(teacherRepository.existsByName(name)){
            logger.error(Markers.UPDATE_TEACHER_MARKER,"Teacher '{}' already exists. Teacher has not been added!",name);
            throw new TeacherAlreadyExistsException(name);
        }
        return teacherRepository.save(new Teacher(name, subjects));
    }

    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects", "allTeachers"}, allEntries = true)
    @Override
    public Teacher addTeacher(Teacher teacher){
        return addTeacher(teacher.getName(), teacher.getSubjects());
    }

    @Override
    public boolean teacherExistsById(Long id) {
        return teacherRepository.existsById(id);
    }

    @Override
    public boolean teacherExistsByName(String name) {
        return teacherRepository.existsByName(name);
    }

    @Caching(evict = { @CacheEvict(cacheNames = "allTeachers", allEntries = true),
            @CacheEvict(cacheNames = "teachers", key = "#id")})
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","lessons","allLessons"}, allEntries = true)
    @Transactional
    @Override
    public boolean deleteTeacher(Long id) throws TeacherNotFoundException {
        if(!teacherExistsById(id)) throw new TeacherNotFoundException("Teacher with id \""+id+"\" not found!");
        teacherRepository.deleteById(id);
        return true;
    }


    @CachePut(cacheNames = "teachers", key = "#id")
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Override
    public Teacher updateTeacher(Long id, String name, Set<Subject> subjects) {
        name = processor.processName(name);
        processor.checkTeacherName(name);
        processor.checkTeachersSubjects(subjects);
        if(teacherRepository.existsByNameAndNotId(id,name)){
            logger.error(Markers.UPDATE_TEACHER_MARKER,"Teacher '{}' already exists. Teacher has not been updated!",name);
            throw new TeacherAlreadyExistsException(name);
        }
        String finalName = name;
        return teacherRepository.findById(id).map((teacher) -> {
            teacher.setName(finalName);
            teacher.setSubjects(subjects);
            Set<Long> ids = subjects.stream().map(Subject::getId).collect(Collectors.toSet());
            deleteLessonsWithIncorrectSubjects(ids, teacher.getId());
            return teacherRepository.save(teacher);
        }).orElseGet(() -> teacherRepository.save(new Teacher(finalName, subjects)));
    }


    private void deleteLessonsWithIncorrectSubjects(Set<Long> ids, Long teacherId) {
        Iterable<Lesson> lessons = lessonService.getAll();
        for (Lesson lesson: lessons) {
            if (lesson.getTeacher().getId().equals(teacherId) && !ids.contains(lesson.getSubject().getId())) {
                try {
                    lessonService.deleteLesson(lesson.getId());
                } catch (NoLessonWithSuchIdFound noLessonWithSuchIdFound) {
                    noLessonWithSuchIdFound.printStackTrace();
                }
            }
        }
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        return updateTeacher(teacher.getId(), teacher.getName(), teacher.getSubjects());
    }

    @Override
    public Teacher updateTeacherNoCheck(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Cacheable(cacheNames = "teachers", key = "#id")
    @Override
    public Teacher getTeacherById(Long id) throws TeacherNotFoundException{
        return teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher with id \""+id+"\" not found!"));
    }

   @Cacheable(cacheNames = "teachers", key = "#name")
   @Override
   public Teacher getTeacherByName(String name) throws Exception {
       Iterable<Teacher> res = teacherRepository.findByName(name);
       if (!res.iterator().hasNext()) throw new TeacherNotFoundException("Teacher with name '"+ name +"' has not been found!");
       return res.iterator().next();
   }

    @Override
    public Iterable<Teacher> getTeacherByPartName(String name) throws Exception {
        return teacherRepository.findByPartName(name);
    }

    @Cacheable(cacheNames = "allTeachers")
    @Override
    public Iterable<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @Scheduled(fixedDelay = 120000)
    @CacheEvict(cacheNames = "allTeachers", allEntries = true)
    public void clearAllTeachersCache() {
        logger.info(Markers.TEACHER_CACHING_MARKER, "SCHEDULED REMOVAL: All teachers list removed from cache");
    }

    @Scheduled(cron = "0 */2 * ? * *")
    @CacheEvict(cacheNames = "teachers", allEntries = true)
    public void clearTeachersCache() {
        logger.info(Markers.TEACHER_CACHING_MARKER, "SCHEDULED REMOVAL: All specific teachers removed from cache");
    }
}
