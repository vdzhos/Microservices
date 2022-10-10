package com.example.mainservice.init;

import com.example.mainservice.models.*;
import com.example.mainservice.repositories.SpecialtyRepository;
import com.example.mainservice.repositories.SubjectRepository;
import com.example.mainservice.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DataInit implements ApplicationRunner {

    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        addSpecialties();
        addSubjects();
        addTeachers();
    }

    private void addTeachers() {
        Teacher t = new Teacher("Tfst Tfst Teacher");
        Teacher t1 = new Teacher("Tsnd Tsnd Teacher");
        Teacher t2 = new Teacher("Ttrd Ttrd Teacher");

        Subject subject = subjectRepository.findByName("Subject 1").iterator().next();
        Subject subject1 = subjectRepository.findByName("Subject 2").iterator().next();

        t.addSubject(subject);
        t.addSubject(subject1);
        List<Long> tLessons = new ArrayList<>();
        tLessons.add(1L);
        t.setLessons(tLessons);

        t1.addSubject(subject);
        List<Long> t1Lessons = new ArrayList<>();
        t1Lessons.add(2L);
        t1Lessons.add(3L);
        t1.setLessons(t1Lessons);

        t2.addSubject(subject1);

        teacherRepository.save(t);
        teacherRepository.save(t1);
        teacherRepository.save(t2);
    }

    private void addSubjects() {
        Subject s = new Subject("Subject 1", 3);
        Subject s1 = new Subject("Subject 2", 4);
        Subject s2 = new Subject("Subject 3", 7);

        Specialty sp1 = specialtyRepository.findByNameAndYear("IPZ", 3).iterator().next();
        Specialty sp2 = specialtyRepository.findByNameAndYear("IPZ", 4).iterator().next();
        Specialty sp3 = specialtyRepository.findByNameAndYear("KN", 3).iterator().next();

        List<Long> sLessons = new ArrayList<>();
        sLessons.add(1L);
        sLessons.add(2L);
        s.setLessons(sLessons);

        List<Long> s1Lessons = new ArrayList<>();
        s1Lessons.add(3L);
        s1.setLessons(s1Lessons);

        s.addSpecialty(sp2);
        s.addSpecialty(sp1);
        s1.addSpecialty(sp2);
        s1.addSpecialty(sp3);
        s2.addSpecialty(sp3);

        subjectRepository.save(s);
        subjectRepository.save(s1);
        subjectRepository.save(s2);
    }

    private void addSpecialties(){
        Specialty sp1 =  new Specialty("IPZ",3);
        Specialty sp2 =  new Specialty("IPZ",4);
        Specialty sp3 =  new Specialty("KN",3);

        specialtyRepository.save(sp1);
        specialtyRepository.save(sp2);
        specialtyRepository.save(sp3);
    }


}
