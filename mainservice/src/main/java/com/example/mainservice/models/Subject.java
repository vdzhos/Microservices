package com.example.mainservice.models;

import com.example.mainservice.utils.EntityIdResolver;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class Subject implements Comparable<Subject> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Field shouldn't be empty!")
    @NotNull(message = "Mandatory field!")
    private String name;

    @Column(nullable = false)
    @Min(1)
    @Max(30)
    @NotNull(message = "Mandatory field!")
    private int quantOfGroups;


    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Teacher.class,
            resolver = EntityIdResolver.class)
    @JsonIdentityReference(alwaysAsId = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "subjects", fetch = FetchType.EAGER)
    private Set<Teacher> teachers;


    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Specialty.class,
            resolver = EntityIdResolver.class)
    @JsonIdentityReference(alwaysAsId = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "subjects_specialties",
            joinColumns = @JoinColumn(name = "subject_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "specialty_id", nullable = false)
    )
    @NotNull(message = "Mandatory field!")
    private Set<Specialty> specialties;


    @ElementCollection
    private List<Long> lessons;

    public Subject() {
    }

    public Subject(String name, int quantOfGroups) {
        this.name = name;
        this.quantOfGroups = quantOfGroups;
        this.teachers = new HashSet<>();
        this.specialties = new HashSet<>();
    }

    public Subject(String name, int quantOfGroups, Set<Specialty> specialties) {
        this.name = name;
        this.quantOfGroups = quantOfGroups;
        this.teachers = new HashSet<>();
        this.specialties = specialties;
    }

    public Subject(long id, String name, int quantOfGroups, Set<Specialty> specialties) {
        this.name = name;
        this.quantOfGroups = quantOfGroups;
        this.teachers = new HashSet<>();
        this.specialties = specialties;
    }

    @Override
    public String toString() {
        return name + '(' + getSpecialtiesToString() + ')';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantOfGroups() {
        return quantOfGroups;
    }

    public void setQuantOfGroups(int quantOfGroups) {
        this.quantOfGroups = quantOfGroups;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public List<Long> getLessons()
    {
    	return this.lessons;
    }

    public String getSpecialtiesToString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (specialties.isEmpty()) return "";
        for (Specialty s : specialties) {
            stringBuilder.append(s).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public String getTeachersToString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (teachers.isEmpty()) return "";
        for (Teacher t : teachers) {
            stringBuilder.append(t).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    public void addTeacher(Teacher t) {
        teachers.add(t);
    }

    public void addSpecialty(Specialty sp) {
        specialties.add(sp);
    }

    public boolean hasSpecialty(Specialty sp) {
        return specialties.contains(sp);
    }

    public boolean hasSpecialty(Long spId) {
        for (Specialty s: specialties) {
            if (s.getId().equals(spId))
                return true;
        }
        return false;
    }

    public boolean hasOnlyOneSpecialty() {
        return specialties.size() == 1;
    }

    @Override
    public int compareTo(Subject o) {
        int cmp = getName().compareTo(o.getName());
        if (cmp == 0) {
            return getQuantOfGroups() - o.getQuantOfGroups();
        }
        return cmp;
    }
}
