package com.pillgood.repository;

import com.pillgood.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Integer> {

    @Query("SELECT q FROM SurveyQuestion q WHERE q.parentQuestion IS NULL")
    List<SurveyQuestion> findParentQuestions();

    @Query("SELECT q FROM SurveyQuestion q WHERE q.parentQuestion.id = :parentQuestionId")
    List<SurveyQuestion> findByParentQuestionId(@Param("parentQuestionId") Integer parentQuestionId);
}
