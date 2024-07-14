package com.pillgood.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillgood.dto.AnswerDto;
import com.pillgood.dto.InquiryDto;
import com.pillgood.entity.Answer;
import com.pillgood.entity.Inquiry;
import com.pillgood.repository.AnswerRepository;
import com.pillgood.repository.InquiryRepository;

import jakarta.transaction.Transactional;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;
    
    @Autowired
    private InquiryRepository inquiryRepository;

    @Override
    public List<AnswerDto> getAllAnswers() {
        return answerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnswerDto getAnswerById(int inquiryNo) {
        Optional<Answer> answerOpt = answerRepository.findById(inquiryNo);
        return answerOpt.map(this::convertToDto).orElse(null);
    }

    @Override
    @Transactional
    public AnswerDto createAnswer(AnswerDto answerDto) {
        Answer answer = new Answer();
        answer.setAnswerContent(answerDto.getAnswerContent());
        answer.setAnswerDate(answerDto.getAnswerDate());

        Inquiry inquiry = inquiryRepository.findById(answerDto.getInquiry().getInquiryNo())
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        answer.setInquiry(inquiry);

        // 문의 상태를 '답변 완료'로 변경
        inquiry.setInquiryStatus("답변 완료");
        inquiryRepository.save(inquiry);

        Answer savedAnswer = answerRepository.save(answer);

        return new AnswerDto(savedAnswer);
    }

    @Override
    public AnswerDto updateAnswer(int id, AnswerDto answerDto) {
        Answer answer = answerRepository.findById(id).orElseThrow(() -> new RuntimeException("Answer not found"));
        answer.setAnswerContent(answerDto.getAnswerContent());
        answer.setAnswerDate(answerDto.getAnswerDate());

        Answer updatedAnswer = answerRepository.save(answer);
        return new AnswerDto(updatedAnswer.getInquiry().getInquiryNo(), updatedAnswer.getAnswerContent(), updatedAnswer.getAnswerDate(), new InquiryDto(updatedAnswer.getInquiry()));
    }

    @Override
    public void deleteAnswer(int id) {
        Optional<Answer> answerOpt = answerRepository.findById(id);
        if (answerOpt.isPresent()) {
            Answer answer = answerOpt.get();
            Inquiry inquiry = answer.getInquiry();
            if (inquiry != null) {
                inquiry.setInquiryStatus("미답변"); // 상태 변경
                inquiryRepository.save(inquiry); // 문의 상태 저장
            }
            answerRepository.delete(answer);
        } else {
            throw new RuntimeException("답변을 찾을 수 없습니다.");
        }
    }
    
    private AnswerDto convertToDto(Answer answerEntity) {
        return new AnswerDto(
                answerEntity.getInquiryNo(),
                answerEntity.getAnswerContent(),
                answerEntity.getAnswerDate(),
                new InquiryDto(
                        answerEntity.getInquiry().getInquiryNo(),
                        answerEntity.getInquiry().getMemberUniqueId(),
                        answerEntity.getInquiry().getInquiryDate(),
                        answerEntity.getInquiry().getInquiryStatus(),
                        answerEntity.getInquiry().getInquiryType(),
                        answerEntity.getInquiry().getInquiryTitle(),
                        answerEntity.getInquiry().getInquiryContent()
                )
        );
    }

    private Answer convertToEntity(AnswerDto answerDto) {
        if (answerDto == null || answerDto.getInquiry() == null) {
            throw new NullPointerException("AnswerDto or InquiryDto is null");
        }

        Answer answer = new Answer();
        answer.setAnswerContent(answerDto.getAnswerContent());
        answer.setAnswerDate(answerDto.getAnswerDate());

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryNo(answerDto.getInquiry().getInquiryNo());
        inquiry.setMemberUniqueId(answerDto.getInquiry().getMemberUniqueId());
        inquiry.setInquiryDate(answerDto.getInquiry().getInquiryDate());
        inquiry.setInquiryStatus(answerDto.getInquiry().getInquiryStatus());
        inquiry.setInquiryType(answerDto.getInquiry().getInquiryType());
        inquiry.setInquiryTitle(answerDto.getInquiry().getInquiryTitle());
        inquiry.setInquiryContent(answerDto.getInquiry().getInquiryContent());

        answer.setInquiry(inquiry);
        return answer;
    }

    private void updateEntityFromDto(Answer answerEntity, AnswerDto answerDto) {
        answerEntity.setAnswerContent(answerDto.getAnswerContent());
        answerEntity.setAnswerDate(answerDto.getAnswerDate());
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryNo(answerDto.getInquiry().getInquiryNo());
        inquiry.setMemberUniqueId(answerDto.getInquiry().getMemberUniqueId());
        inquiry.setInquiryDate(answerDto.getInquiry().getInquiryDate());
        inquiry.setInquiryStatus(answerDto.getInquiry().getInquiryStatus());
        inquiry.setInquiryType(answerDto.getInquiry().getInquiryType());
        inquiry.setInquiryTitle(answerDto.getInquiry().getInquiryTitle());
        inquiry.setInquiryContent(answerDto.getInquiry().getInquiryContent());
        answerEntity.setInquiry(inquiry);
    }
}
