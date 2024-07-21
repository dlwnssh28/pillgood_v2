package com.pillgood.repository;

import com.pillgood.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Integer> {
    List<SurveyAnswer> findByQuestionId(int questionId);
}
