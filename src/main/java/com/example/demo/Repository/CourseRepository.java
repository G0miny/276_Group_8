package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Course;
import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course, Long>{
    Optional<Course> findByYearAndTermAndDepartmentAndCourseNumber(
        int year, String term, String department, String courseNumber);
}
