package com.example.practice1.services.interfaces;

import com.example.practice1.models.Lesson;
import com.example.practice1.models.Specialty;
import com.example.practice1.models.Subject;
import com.example.practice1.models.Teacher;

import java.util.List;
import java.util.Set;

public interface ScheduleService {

	List<Lesson> getTeacherLessons(Long id) throws Throwable;
	
	List<Lesson> getSpecialtyLessons(Long id);
	
	Specialty getSpecialty(Long id);
	
	Teacher getTeacher(Long id) throws Throwable;
	
	Iterable<Subject> getTeacherSubjects(Long id) throws Throwable;
	
	Iterable<Subject> getSpecialtySubjects(Long id);
	
	Iterable<Integer> getSubjectLessonsWeeks(Iterable<Subject> subjects);
	
	Set<String> getLessonsRooms(List<Lesson> list);
	
//filters
	
	List<Lesson> getLessonsFromSubjects(Iterable<Subject> subjects);
	
	List<Lesson> getSubjectLessons(Long id);
	
	List<Lesson> filterLessonsByWeek(List<Lesson> list, int week);
	
	List<Lesson> filterLessonsByRoom(List<Lesson> list, String room);
	
	Set<Teacher> getTeachersFromSubjects(Iterable<Subject> subjects);
	
}