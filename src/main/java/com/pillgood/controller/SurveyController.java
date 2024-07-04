package com.pillgood.controller;

import com.pillgood.dto.SurveyDto;
import com.pillgood.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/api/surveys/list")
    public ResponseEntity<List<SurveyDto>> getAllSurveys() {
        List<SurveyDto> surveys = surveyService.getAllSurveys();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/api/surveys/{id}")
    public ResponseEntity<SurveyDto> getSurveyById(@PathVariable Integer id) {
        return surveyService.getSurveyById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/surveys/member/{memberId}")
    public ResponseEntity<List<SurveyDto>> getSurveysByMemberId(@PathVariable String memberId) {
        List<SurveyDto> surveys = surveyService.getSurveysByMemberId(memberId);
        return ResponseEntity.ok(surveys);
    }

    @PostMapping("/api/surveys/create")
    public ResponseEntity<SurveyDto> createSurvey(@RequestBody SurveyDto surveyDto) {
        SurveyDto createdSurvey = surveyService.createSurvey(surveyDto);
        return ResponseEntity.ok(createdSurvey);
    }

    @PutMapping("/api/surveys/update/{id}")
    public ResponseEntity<SurveyDto> updateSurvey(@PathVariable Integer id, @RequestBody SurveyDto surveyDto) {
        return surveyService.updateSurvey(id, surveyDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/surveys/delete/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Integer id) {
        if (surveyService.deleteSurvey(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
