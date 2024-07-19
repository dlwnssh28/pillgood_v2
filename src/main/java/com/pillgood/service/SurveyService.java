package com.pillgood.service;

import com.pillgood.dto.SurveyDto;
import com.pillgood.entity.Survey;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SurveyService {
    List<SurveyDto> getAllSurveys();
    Optional<SurveyDto> getSurveyById(int id);
    List<SurveyDto> getSurveysByMemberId(String memberUniqueId);
    SurveyDto createSurvey(SurveyDto surveyDto);
    Optional<SurveyDto> updateSurvey(int id, SurveyDto surveyDto);
    boolean deleteSurvey(int id);
    SurveyDto convertToDto(Survey surveyEntity);
    Survey convertToEntity(SurveyDto surveyDto);
    SurveyDto createOrUpdateSurvey(SurveyDto surveyDto); // 추가
    List<Object[]> getAgeGroupDeficiencyData(); // 추가된 부분 설문조사 결과 나이대별 부족한 영양소 통계 차트
    List<Map<String, Object>> getTopProducts(); //선호차트
}
