package com.pillgood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillgood.dto.AnswerDto;
import com.pillgood.service.AnswerService;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @GetMapping("/api/answers/list")
    public List<AnswerDto> getAllAnswers() {
        return answerService.getAllAnswers();
    }

    @GetMapping("/api/answers/{id}")
    public ResponseEntity<AnswerDto> getAnswerById(@PathVariable int id) {
        AnswerDto answerDto = answerService.getAnswerById(id);
        if (answerDto != null) {
        	return ResponseEntity.ok(answerDto);
        } else {
            return ResponseEntity.ok(new AnswerDto());  // 빈 객체 반환
        }
    }

    @PostMapping("/admin/answers/create")
    public ResponseEntity<AnswerDto> createAnswer(@RequestBody AnswerDto answerDto) {
        System.out.println("Received AnswerDto: " + answerDto); // 전송된 데이터
        if (answerDto.getInquiry() == null) {
            System.out.println("InquiryDto is null");
        } else {
            System.out.println("InquiryDto: " + answerDto.getInquiry());
        }
        
        AnswerDto createdAnswer = answerService.createAnswer(answerDto);
        System.out.println("created answer:" + createdAnswer);
        return ResponseEntity.ok(createdAnswer);
    }

    @PutMapping("/admin/answers/update/{id}")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable int id, @RequestBody AnswerDto answerDto) {
        AnswerDto updatedAnswer = answerService.updateAnswer(id, answerDto);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/admin/answers/delete/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable int id) {
        try {
            answerService.deleteAnswer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
