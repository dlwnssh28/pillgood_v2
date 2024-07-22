package com.pillgood.dto;

import lombok.Data;

@Data
public class SurveyAnswerDto {
    private Integer id;
    private Integer questionId;
    private String answerContent;
    private Integer deficiencyId;
    private String deficiencyName; // 추가
}
