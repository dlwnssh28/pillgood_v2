package com.pillgood.controller;

import com.pillgood.dto.SurveyQuestionDto;
import com.pillgood.service.SurveyQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SurveyQuestionController {

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @PostMapping("/admin/questions/create")
    public ResponseEntity<SurveyQuestionDto> createQuestion(@RequestBody SurveyQuestionDto questionDto) {
        return ResponseEntity.ok(surveyQuestionService.createQuestion(questionDto));
    }

    @GetMapping("/api/questions/list")
    public ResponseEntity<List<SurveyQuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(surveyQuestionService.getAllQuestions());
    }

    @GetMapping("/api/questions/{id}")
    public ResponseEntity<SurveyQuestionDto> getQuestionById(@PathVariable int id) {
        return ResponseEntity.ok(surveyQuestionService.getQuestionById(id));
    }

    @PutMapping("/admin/questions/update/{id}")
    public ResponseEntity<SurveyQuestionDto> updateQuestion(@PathVariable int id, @RequestBody SurveyQuestionDto questionDto) {
        return ResponseEntity.ok(surveyQuestionService.updateQuestion(id, questionDto));
    }

    @DeleteMapping("/admin/questions/delete/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable int id) {
        if (surveyQuestionService.deleteQuestion(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
