package com.example.lessonservice.repositories;

import com.example.lessonservice.models.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TeacherRepository extends CrudRepository<Teacher,Long> {

    @Query("select t from Teacher t where lower(t.name) like lower( :name)")
    Iterable<Teacher> findByName(@Param("name") String name);

    @Query("select case when count(t)> 0 then true else false end from Teacher t where lower(t.name) like lower(concat('%', :name,'%')) and not t.id = :id")
    boolean existsByNameAndNotId(@Param("id") Long id, @Param("name") String name);

    @Query("select case when count(t)> 0 then true else false end from Teacher t where lower(t.name) like lower(concat('%', :name,'%'))")
    boolean existsByName(@Param("name") String name);

    @Query("select t from Teacher t where lower(t.name) like lower(concat('%', :name,'%'))")
    Iterable<Teacher> findByPartName(@Param("name") String name);



}

