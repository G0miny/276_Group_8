package com.example.demo.models;

import jakarta.persistence.*;;

@Entity
@Table(name = "student_course_selection",
uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "section_is"}))

public class StudentCourseSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSections section;


    public StudentCourseSelection() {

    }

    public StudentCourseSelection(User user, CourseSections section) {
        this.user = user;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CourseSections getSection(){
        return section;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSection(CourseSections section) {
        this.section = section;
    }



}
