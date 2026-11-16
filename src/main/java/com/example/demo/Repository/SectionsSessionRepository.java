package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.SectionSession;
import com.example.demo.models.CourseSections;
import java.util.List;

public interface SectionsSessionRepository extends JpaRepository<SectionSession, Long>{
    List<SectionSession> findBySection(CourseSections sections);
}
