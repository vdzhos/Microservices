package com.example.mainservice.repositories;

import com.example.mainservice.models.Specialty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SpecialtyRepository extends CrudRepository<Specialty, Long> {

    Iterable<Specialty> findByYear(int year);

    @Query("select s from Specialty s where lower(s.name) like lower( :name) and s.year = :year")
    Iterable<Specialty> findByNameAndYear(@Param("name")String name, @Param("year") int year);

    Iterable<Specialty> findByName(String name);

    @Query("select case when count(s)> 0 then true else false end from Specialty s where lower(s.name) like lower(concat('%', :name,'%')) and s.year = :year and not s.id = :id")
    boolean existsByNameAndYearAndNotId(@Param("id") long id, @Param("name") String name,@Param("year") int year);


    @Query("select case when count(s)> 0 then true else false end from Specialty s where lower(s.name) like lower(concat('%', :name,'%')) and s.year = :year")
    boolean existsByNameAndYear(@Param("name") String name,@Param("year") int year);

}
