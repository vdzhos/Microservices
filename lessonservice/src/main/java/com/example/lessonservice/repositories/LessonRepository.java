package com.example.lessonservice.repositories;

import com.example.lessonservice.models.Lesson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends CrudRepository<Lesson,Long> {

    @Query("from Lesson l where l.teacherId=:teacherId")
    Iterable<Lesson> findByTeacherId(@Param("teacherId") Long teacherId);

}
