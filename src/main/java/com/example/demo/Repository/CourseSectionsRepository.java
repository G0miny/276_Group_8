package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.CourseSections;
import com.example.demo.models.Course;
import java.util.Optional;
import java.util.List;

public interface CourseSectionsRepository extends JpaRepository<CourseSections, Long>{
    
    List<CourseSections> findByCourse(Course course);

    Optional<CourseSections> findByCourseAndSection(
        Course course, String section);
    
}
