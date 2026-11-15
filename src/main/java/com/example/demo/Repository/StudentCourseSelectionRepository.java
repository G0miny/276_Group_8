package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.StudentCourseSelection;

public interface StudentCourseSelectionRepository extends JpaRepository<StudentCourseSelection, Long> {

    // Retrives sections belonign to a student 
    List<StudentCourseSelection> findByUser_Uid(Long uid);
    
    // Check if a student has alredy selcted a section 
    boolean existsByUser_UidAndSection_Id(Long uid, Long sectionId);
}
