package com.Myproject.insurance.repository;

import com.Myproject.insurance.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions,Long> {
    List<Questions> findByWriter(String name);


}
