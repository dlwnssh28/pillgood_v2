package com.pillgood.dto;

import com.pillgood.entity.Inquiry;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    private int inquiryNo;
    private String memberUniqueId;
    private LocalDateTime inquiryDate;
    private String inquiryStatus;
    private String inquiryType;
    private String inquiryTitle;
    private String inquiryContent;

    // Inquiry 엔티티를 매개변수로 받는 생성자 추가
    public InquiryDto(Inquiry inquiry) {
        this.inquiryNo = inquiry.getInquiryNo();
        this.memberUniqueId = inquiry.getMemberUniqueId();
        this.inquiryDate = inquiry.getInquiryDate();
        this.inquiryStatus = inquiry.getInquiryStatus();
        this.inquiryType = inquiry.getInquiryType();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContent();
    }
}
