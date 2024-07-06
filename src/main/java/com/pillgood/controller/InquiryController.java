package com.pillgood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pillgood.dto.InquiryDto;
import com.pillgood.service.InquiryService;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/list")
    public List<InquiryDto> getAllInquiries() {
        return inquiryService.getAllInquiries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryDto> getInquiryById(@PathVariable int id) {
        InquiryDto inquiryDto = inquiryService.getInquiryById(id);
        if (inquiryDto != null) {
            return ResponseEntity.ok(inquiryDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<InquiryDto>> getInquiriesByMemberId(@PathVariable String memberId) {
        List<InquiryDto> inquiries = inquiryService.getInquiriesByMemberId(memberId);
        return ResponseEntity.ok(inquiries);
    }
    
    @PostMapping("/create")
    public ResponseEntity<InquiryDto> createInquiry(@RequestBody InquiryDto inquiryDto) {
        // 요청된 데이터 확인
        System.out.println("received inquiry: " + inquiryDto);
        InquiryDto createdInquiry = inquiryService.createInquiry(inquiryDto);
        
        // 생성된 데이터 확인
        System.out.println("created inquiry: " + createdInquiry);
        return ResponseEntity.ok(createdInquiry);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InquiryDto> updateInquiry(@PathVariable int id, @RequestBody InquiryDto inquiryDto) {
        InquiryDto updatedInquiry = inquiryService.updateInquiry(id, inquiryDto);
        if (updatedInquiry != null) {
            return ResponseEntity.ok(updatedInquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable int id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.noContent().build();
    }
}
