package com.example.mainservice.services.implementations;

import com.example.mainservice.exceptions.lesson.NoLessonWithSuchIdFound;
import com.example.mainservice.exceptions.subject.NoSubjectWithSuchIdToDelete;
import com.example.mainservice.exceptions.subject.SubjectNotFoundException;
import com.example.mainservice.models.Lesson;
import com.example.mainservice.models.Specialty;
import com.example.mainservice.models.Subject;
import com.example.mainservice.models.SubjectType;
import com.example.mainservice.repositories.SubjectRepository;
import com.example.mainservice.services.interfaces.LessonService;
import com.example.mainservice.services.interfaces.SubjectService;
import com.example.mainservice.utils.Markers;
import com.example.mainservice.utils.Utils;
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

import java.util.*;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private LessonService lessonService;

    private Utils processor;

    private static Logger logger = LogManager.getLogger(SubjectServiceImpl.class);

    @Autowired
    public void setProcessor(Utils processor) {
        this.processor = processor;
    }


    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "allSubjects"}, allEntries = true)
    @Override
    public Subject addSubject(String name, int quantOfGroups, Set<Specialty> specialties) {
        name = processor.processName(name);
        processor.checkSubjectName(name);
        processor.checkQuantOfGroups(quantOfGroups);
        processor.checkQuantOfSpecialties(specialties == null? 0 : specialties.size());
        Iterable<Subject> subjectsWithSuchName = subjectRepository.findByName(name);
        processor.checkSpecialties(subjectsWithSuchName, specialties);
        return subjectRepository.save(new Subject(name, quantOfGroups, specialties));
    }

    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "allSubjects"}, allEntries = true)
    @Override
    public Subject addSubject(Subject subject) {
        subject.setId(-1L);
        return addSubject(subject.getName(), subject.getQuantOfGroups(), subject.getSpecialties());
    }

    @Caching(evict = { @CacheEvict(cacheNames = "allSubjects", allEntries = true),
            @CacheEvict(cacheNames = "subjects", key = "#id")})
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "teachers", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Transactional
    @Override
    public void deleteSubject(Long id) {
        if(!subjectExistsById(id)) throw new NoSubjectWithSuchIdToDelete(id);
        subjectRepository.deleteById(id);
    }

    @CachePut(cacheNames = "subjects", key = "#id")
    @CacheEvict(cacheNames = {"specialties", "allSpecialties", "allSubjects", "teachers", "allTeachers","lessons","allLessons"}, allEntries = true)
    @Override
    public Subject updateSubject(Long id, String name, int quantOfGroups, Set<Specialty> specialties) {
        name = processor.processName(name);
        processor.checkSubjectName(name);
        processor.checkQuantOfGroups(quantOfGroups);
        processor.checkQuantOfSpecialties(specialties == null? 0 : specialties.size());
        Iterable<Subject> subjectsWithSuchName = subjectRepository.findByNameAndNotId(id, name);
        processor.checkSpecialties(subjectsWithSuchName, specialties);
        String finalName = name;
        return subjectRepository.findById(id).map((subject) -> {
            if (nothingChanged(subject, finalName, quantOfGroups, specialties))
                return subject;

            if (subject.getQuantOfGroups() > quantOfGroups) {
                deleteLessonsWithIncorrectGroups(quantOfGroups, subject.getId());
            }

            subject.setName(finalName);
            subject.setQuantOfGroups(quantOfGroups);
            subject.setSpecialties(specialties);
            return subjectRepository.save(subject);
        }).orElseGet(() -> {
            return subjectRepository.save(new Subject(id, finalName, quantOfGroups, specialties));
        });
    }

    private void deleteLessonsWithIncorrectGroups(int newGroupNum, Long subjectId) {
        Iterable<Lesson> lessons = lessonService.getAll();
        for (Lesson lesson: lessons) {
            if (lesson.getGroup().getType() == SubjectType.SubjectTypeEnum.LECTURE) continue;
            if (lesson.getSubject().getId().equals(subjectId) &&
                    Integer.parseInt(lesson.getGroup().getGroup()) > newGroupNum) {
                try {
                    lessonService.deleteLesson(lesson.getId());
                } catch (NoLessonWithSuchIdFound noLessonWithSuchIdFound) {
                    noLessonWithSuchIdFound.printStackTrace();
                }
            }
        }
    }

    @CacheEvict(cacheNames = {"specialties", "allSpecialties","lessons","allLessons"}, allEntries = true)
    @Override
    public Subject updateSubject(Subject subject) {
        return updateSubject(subject.getId(), subject.getName(), subject.getQuantOfGroups(), subject.getSpecialties());
    }

    @Override
    public Subject updateSubjectNoCheck(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Cacheable(cacheNames = "allSubjects")
    @Override
    public Iterable<Subject> getAll() {
        List<Subject> subjects = (List<Subject>)subjectRepository.findAll();
        Collections.sort(subjects);
        return subjects;
    }

    @Cacheable(cacheNames = "subjects", key = "#name")
    @Override
    public Subject getSubjectByName(String name) {
        Iterable<Subject> res = subjectRepository.findByName(name);
        if (!res.iterator().hasNext()) throw new SubjectNotFoundException("Subject with name '"+ name +"' has not been found!");
        return res.iterator().next();
    }

    @Cacheable(cacheNames = "subjects", key = "#id")
    @Override
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new SubjectNotFoundException("Subject with id '"+ id +"' has not been found!"));
    }

    @Override
    public boolean subjectExistsById(Long id) {
        return subjectRepository.existsById(id);
    }

    @Override
    public boolean subjectExistsByName(String name) {
        return subjectRepository.existsByName(name);
    }

    private boolean nothingChanged(Subject subject, String name, int quantOfGroups, Set<Specialty> specialties) {
        return subject.getName().equals(name) && subject.getQuantOfGroups() == quantOfGroups
                && subject.getSpecialties().equals(specialties);
    }

	@Override
	public Set<Integer> getLessonWeeks(Long id) {
		Subject sbj = this.getSubjectById(id);
		SortedSet<Integer> set = new TreeSet<Integer>();
		for(Lesson less : sbj.getLessons())
			set.addAll(less.getIntWeeks());
		return set;
	}

	@Override
	public Set<Integer> getLessonWeeks(Set<Long> ids) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		for(Long id : ids)
			set.addAll(this.getLessonWeeks(id));
		return set;
	}

    @Scheduled(fixedDelay = 120000)
    @CacheEvict(cacheNames = "allSubjects", allEntries = true)
    public void clearAllSubjectsCache() {
        logger.info(Markers.SUBJECT_CACHING_MARKER, "SCHEDULED REMOVAL: All subjects list removed from cache");
    }

    @Scheduled(cron = "0 */2 * ? * *")
    @CacheEvict(cacheNames = "subjects", allEntries = true)
    public void clearSubjectsCache() {
        logger.info(Markers.SUBJECT_CACHING_MARKER, "SCHEDULED REMOVAL: All specific subjects removed from cache");
    }


    @Override
    public List<Lesson> getSubjectLessons(Long subjectId) {
        return getSubjectById(subjectId).getLessons();
    }

}
