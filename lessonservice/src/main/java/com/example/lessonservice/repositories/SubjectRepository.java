package com.example.lessonservice.repositories;

import com.example.lessonservice.models.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends CrudRepository<Subject,Long> {

    @Query("select s from Subject s where lower(s.name) like lower( :name)")
    Iterable<Subject> findByName(@Param("name") String name);

    @Query("select s from Subject s where lower(s.name) like lower( :name) and not s.id = :id")
    Iterable<Subject> findByNameAndNotId(@Param("id") long id, @Param("name") String name);

    @Query("select case when count(s)> 0 then true else false end from Subject s where lower(s.name) like lower(concat('%', :name,'%'))")
    boolean existsByName(@Param("name") String name);

}
