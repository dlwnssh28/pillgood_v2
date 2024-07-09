package com.pillgood.dto;

import java.time.LocalDateTime;

import com.pillgood.entity.Answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private int inquiryNo;
    private String answerContent;
    private LocalDateTime answerDate;
    private InquiryDto inquiry;
    

    public AnswerDto(Answer answer) {
        this.inquiryNo = answer.getInquiry().getInquiryNo();
        this.answerContent = answer.getAnswerContent();
        this.answerDate = answer.getAnswerDate();
        this.inquiry = new InquiryDto(answer.getInquiry());
    }
}
