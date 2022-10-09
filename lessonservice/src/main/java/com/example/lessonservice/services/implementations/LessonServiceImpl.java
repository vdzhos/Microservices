package com.example.lessonservice.services.implementations;

import com.example.lessonservice.exceptions.NoLessonWithSuchIdFound;
import com.example.lessonservice.models.Lesson;
import com.example.lessonservice.repositories.LessonRepository;
import com.example.lessonservice.services.interfaces.LessonService;
import com.example.lessonservice.utils.Markers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    private static final Logger logger = LogManager.getLogger(LessonServiceImpl.class);

    @Override
    public Lesson addLesson(Lesson lesson) {
        lesson.setId(-1L);
        return lessonRepository.save(lesson);
    }

    @Override
    public boolean lessonExistsById(Long id) {
        return lessonRepository.existsById(id);
    }

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

    @Override
    public Lesson updateLesson(Lesson lesson) throws NoLessonWithSuchIdFound {
        if(!lessonExistsById(lesson.getId())) throw new NoLessonWithSuchIdFound(lesson.getId(),"updated");
        return lessonRepository.save(lesson);
    }

    @Override
    public Lesson getLessonById(Long id) throws NoLessonWithSuchIdFound{
        return lessonRepository.findById(id).orElseThrow(() -> new NoLessonWithSuchIdFound(id,"get"));
    }

    @Override
    public Iterable<Lesson> getAll() {
        return lessonRepository.findAll();
    }

}
