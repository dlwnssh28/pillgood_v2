package com.pillgood.controller;

import com.pillgood.dto.SurveyDto;
import com.pillgood.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    
    @GetMapping("/api/surveys/result")
    public ResponseEntity<?> getSurveyResult(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        
        if (memberId == null) {
            return new ResponseEntity<>("세션에 memberId가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        System.out.println(memberId + ": 설문조사 결과 조회");
        List<SurveyDto> surveys = surveyService.getSurveysByMemberId(memberId);
        
        if (surveys.isEmpty()) {
            return new ResponseEntity<>("설문조사 결과가 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(surveys, HttpStatus.OK);
    }

    @PostMapping("/api/surveys/create")
    public ResponseEntity<SurveyDto> createSurvey(@RequestBody SurveyDto surveyDto) {
        System.out.println("Received survey data: " + surveyDto); // 디버그 로그 추가
        SurveyDto createdSurvey = surveyService.createOrUpdateSurvey(surveyDto);
        return ResponseEntity.ok(createdSurvey);
    }

    @PutMapping("/api/surveys/update/{id}")
    public ResponseEntity<SurveyDto> updateSurvey(@PathVariable Integer id, @RequestBody SurveyDto surveyDto) {
        System.out.println("Updating survey data: " + surveyDto); // 디버그 로그 추가
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

    @GetMapping("/api/surveys/age-group-deficiency")
    public ResponseEntity<List<Object[]>> getAgeGroupDeficiencyData() {
        List<Object[]> data = surveyService.getAgeGroupDeficiencyData();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/api/surveys/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopProducts() {
        List<Map<String, Object>> data = surveyService.getTopProducts();
        return ResponseEntity.ok(data);
    }
}
