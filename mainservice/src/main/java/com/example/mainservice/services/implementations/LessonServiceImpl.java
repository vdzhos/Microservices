package com.example.mainservice.services.implementations;

import com.example.mainservice.models.*;
import com.example.mainservice.exceptions.lesson.InvalidLessonArgumentsException;
import com.example.mainservice.exceptions.lesson.NoLessonWithSuchIdFound;
import com.example.mainservice.exceptions.subject.SubjectNotFoundException;
import com.example.mainservice.exceptions.teacher.TeacherNotFoundException;
import com.example.mainservice.repositories.LessonRepository;
import com.example.mainservice.repositories.SubjectRepository;
import com.example.mainservice.repositories.TeacherRepository;
import com.example.mainservice.services.interfaces.LessonService;
import com.example.mainservice.utils.Markers;
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

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private static final Logger logger = LogManager.getLogger(LessonServiceImpl.class);

    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","allLessons", "teachers", "allTeachers"}, allEntries = true)
    @Override
    public Lesson addLesson(Lesson.Time time, Long subjId, Long teachId, SubjectType subjectType, String weeks, String room, DayOfWeek dayOfWeek) throws Exception {
        Object[] res = verifyAndProcessData(subjId,teachId,weeks,room,time,subjectType,dayOfWeek);
        logger.info(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson successfully added!");
        return lessonRepository.save(new Lesson(time,(Subject) res[1],(Teacher) res[2],subjectType,weeks,(Room) res[0],dayOfWeek));
    }

    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","allLessons", "teachers", "allTeachers"}, allEntries = true)
    @Override
    public Lesson addLesson(Lesson lesson) {
        lesson.setId(-1L);
        return lessonRepository.save(lesson);
    }

    @Override
    public boolean lessonExistsById(Long id) {
        return lessonRepository.existsById(id);
    }

    @Caching(evict = { @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","allLessons", "teachers", "allTeachers"}, allEntries = true),
            @CacheEvict(cacheNames = "lessons", key = "#id")})
    @Transactional
    @Override
    public void deleteLesson(Long id) throws NoLessonWithSuchIdFound {
        if(!lessonExistsById(id)){
            logger.info(Markers.DELETE_LESSON_MARKER,"Lesson not deleted!");
            throw new NoLessonWithSuchIdFound(id,"deleted");
        }
        lessonRepository.deleteById(id);
        logger.info(Markers.DELETE_LESSON_MARKER,"Lesson successfully deleted!");
    }

    @CachePut(cacheNames = "lessons", key = "#id")
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","allLessons", "teachers", "allTeachers"}, allEntries = true)
    @Override
    public Lesson updateLesson(Long id, Lesson.Time time, Long subjId, Long teachId, SubjectType subjectType, String weeks, String room, DayOfWeek dayOfWeek) throws Exception {
        Object[] res = verifyAndProcessData(subjId,teachId,weeks,room,time,subjectType,dayOfWeek);
        logger.info(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson successfully added!");
        return lessonRepository.save(new Lesson(id,time,(Subject) res[1],(Teacher) res[2],subjectType,weeks,(Room) res[0],dayOfWeek));
    }

    @CachePut(cacheNames = "lessons", key = "#lesson.id")
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "subjects", "allSubjects","allLessons", "teachers", "allTeachers"}, allEntries = true)
    @Override
    public Lesson updateLesson(Lesson lesson) throws NoLessonWithSuchIdFound {
        if(!lessonExistsById(lesson.getId())) throw new NoLessonWithSuchIdFound(lesson.getId(),"updated");
        return lessonRepository.save(lesson);
    }

    @Cacheable(cacheNames = "lessons", key = "#id")
    @Override
    public Lesson getLessonById(Long id) throws NoLessonWithSuchIdFound{
        return lessonRepository.findById(id).orElseThrow(() -> new NoLessonWithSuchIdFound(id,"get"));
    }

    @Cacheable(cacheNames = "allLessons")
    @Override
    public Iterable<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    @Scheduled(fixedDelay = 120000)
    @CacheEvict(cacheNames = "allLessons", allEntries = true)
    public void clearAllLessonsCache() {
        logger.info(Markers.LESSON_CACHING_MARKER, "SCHEDULED REMOVAL: All lessons list removed from cache");
    }

    @Scheduled(cron = "0 */2 * ? * *")
    @CacheEvict(cacheNames = "lessons", allEntries = true)
    public void clearLessonsCache() {
        logger.info(Markers.LESSON_CACHING_MARKER, "SCHEDULED REMOVAL: All specific lessons removed from cache");
    }

    private Object[] verifyAndProcessData(Long subjId, Long teachId, String weeks, String room,Lesson.Time time, SubjectType subjectType,  DayOfWeek dayOfWeek) throws Exception {
        Room r;
        if(room.equals("remotely")) r = new Room();
        else r = new Room(room);

        List<Integer> weeksList = Stream.of(weeks.split("[,-]")).map(Integer::parseInt).collect(Collectors.toList());

        if(weeks.isEmpty() || !weeks.matches("^([1-9][0-9]*(-[1-9][0-9]*)?)(,([1-9][0-9]*(-[1-9][0-9]*)?))*$") || !checkWeeksAscending(weeksList)){
            logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
            throw new InvalidLessonArgumentsException("weeks",weeks);
        }

        Optional<Subject> s = subjectRepository.findById(subjId);
        Optional<Teacher> t = teacherRepository.findById(teachId);

        if(s.isEmpty()){
            logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
            throw new SubjectNotFoundException("Subject with id \""+subjId+"\" not found!");
        }
        if(t.isEmpty()){
            logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
            throw new TeacherNotFoundException("Teacher with id \""+teachId+"\" not found!");
        }

        for (Lesson l : t.get().lessons()) {
            Set<Integer> w = l.getIntWeeks().stream().distinct().filter(weeksList::contains).collect(Collectors.toSet());
            boolean sameTime = l.getDayOfWeek()==dayOfWeek && l.getTime() == time && !w.isEmpty();

            if(sameTime){
                if(!l.getSubject().getId().equals(s.get().getId())){
                    logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
                    throw new Exception("Teacher can't teach different lessons at the same time!");
                }else if(!l.getRoom().getRoom().equals(room)){
                    logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
                    throw new Exception("Teacher can't have lessons in different rooms at the same time!");
                }else if(l.getGroup().getGroup().equals(subjectType.getGroup())){
                    logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
                    throw new Exception("Duplicate lesson for same group!");
                }else if(!(l.getGroup().getType() == SubjectType.SubjectTypeEnum.PRACTICE && subjectType.getType() == SubjectType.SubjectTypeEnum.PRACTICE)){
                    logger.error(Markers.ALTERING_LESSON_TABLE_MARKER,"Lesson not added!");
                    throw new Exception("Teacher can't have lecture and practice at the same time!");
                }
            }
        }

        return new Object[]{r,s.get(),t.get()};
    }

    private boolean checkWeeksAscending(List<Integer> w){
        boolean ok = true;
        for (int i = 1; i < w.size(); i++) {
            if(w.get(i)<=w.get(i-1)) {
                ok = false;
                break;
            }
        }
        return ok;
    }

}
