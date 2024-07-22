package com.pillgood.service;

import com.pillgood.dto.SurveyAnswerDto;
import com.pillgood.entity.Deficiency;
import com.pillgood.entity.SurveyAnswer;
import com.pillgood.entity.SurveyQuestion;
import com.pillgood.repository.DeficiencyRepository;
import com.pillgood.repository.SurveyAnswerRepository;
import com.pillgood.repository.SurveyQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;

    @Autowired
    private SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    private DeficiencyRepository deficiencyRepository;

    @Override
    public SurveyAnswerDto createAnswer(SurveyAnswerDto answerDto) {
        SurveyAnswer entity = convertToEntity(answerDto);
        return convertToDto(surveyAnswerRepository.save(entity));
    }

    @Override
    public List<SurveyAnswerDto> getAllAnswers() {
        return surveyAnswerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurveyAnswerDto getAnswerById(int id) {
        return surveyAnswerRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Override
    public SurveyAnswerDto updateAnswer(int id, SurveyAnswerDto answerDto) {
        return surveyAnswerRepository.findById(id)
                .map(existingAnswer -> {
                    if (answerDto.getQuestionId() == null) {
                        throw new IllegalArgumentException("Question ID must not be null");
                    }
                    if (answerDto.getDeficiencyId() == null) {
                        throw new IllegalArgumentException("Deficiency ID must not be null");
                    }

                    SurveyQuestion questionEntity = surveyQuestionRepository.findById(answerDto.getQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + answerDto.getQuestionId()));
                    existingAnswer.setQuestion(questionEntity);
                    existingAnswer.setAnswerContent(answerDto.getAnswerContent());

                    Deficiency deficiency = deficiencyRepository.findById(answerDto.getDeficiencyId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid deficiency ID: " + answerDto.getDeficiencyId()));
                    existingAnswer.setDeficiency(deficiency);

                    return convertToDto(surveyAnswerRepository.save(existingAnswer));
                }).orElseThrow(() -> new IllegalArgumentException("Invalid answer ID: " + id));
    }

    @Override
    public boolean deleteAnswer(int id) {
        if (surveyAnswerRepository.existsById(id)) {
            surveyAnswerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<SurveyAnswerDto> getAnswersByQuestionId(int questionId) {
        List<SurveyAnswer> answers = surveyAnswerRepository.findByQuestionId(questionId);
        return answers.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private SurveyAnswerDto convertToDto(SurveyAnswer entity) {
        SurveyAnswerDto dto = new SurveyAnswerDto();
        dto.setId(entity.getId());
        dto.setQuestionId(entity.getQuestion().getId());
        dto.setAnswerContent(entity.getAnswerContent());
        dto.setDeficiencyId(entity.getDeficiency().getDeficiencyId());
        dto.setDeficiencyName(entity.getDeficiency().getDeficiencyName()); // 추가
        return dto;
    }

    private SurveyAnswer convertToEntity(SurveyAnswerDto dto) {
        SurveyAnswer entity = new SurveyAnswer();
        SurveyQuestion questionEntity = surveyQuestionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + dto.getQuestionId()));
        entity.setQuestion(questionEntity);
        entity.setAnswerContent(dto.getAnswerContent());
        
        Deficiency deficiency = deficiencyRepository.findById(dto.getDeficiencyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid deficiency ID: " + dto.getDeficiencyId()));
        entity.setDeficiency(deficiency);
        
        return entity;
    }
}
