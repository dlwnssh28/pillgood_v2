package com.pillgood.service;

import com.pillgood.dto.SurveyDto;
import com.pillgood.entity.Survey;
import com.pillgood.repository.MemberRepository;
import com.pillgood.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<SurveyDto> getAllSurveys() {
        return surveyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SurveyDto> getSurveyById(int id) {
        return surveyRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<SurveyDto> getSurveysByMemberId(String memberUniqueId) {
        return surveyRepository.findByMemberUniqueId(memberUniqueId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurveyDto createSurvey(SurveyDto surveyDto) {
        if (surveyDto.getMemberUniqueId() == null || !memberRepository.existsById(surveyDto.getMemberUniqueId())) {
            throw new IllegalArgumentException("Invalid member ID: " + surveyDto.getMemberUniqueId());
        }

        Survey surveyEntity = convertToEntity(surveyDto);
        Survey savedSurvey = surveyRepository.save(surveyEntity);
        return convertToDto(savedSurvey);
    }

    @Override
    public Optional<SurveyDto> updateSurvey(int id, SurveyDto surveyDto) {
        return surveyRepository.findById(id)
                .map(existingSurvey -> {
                    existingSurvey.setMemberUniqueId(surveyDto.getMemberUniqueId());
                    existingSurvey.setName(surveyDto.getName());
                    existingSurvey.setAge(surveyDto.getAge());
                    existingSurvey.setGender(surveyDto.getGender());
                    existingSurvey.setHeight(surveyDto.getHeight());
                    existingSurvey.setWeight(surveyDto.getWeight());
                    existingSurvey.setDeficiencyId1(surveyDto.getDeficiencyId1());
                    existingSurvey.setDeficiencyId2(surveyDto.getDeficiencyId2());
                    existingSurvey.setDeficiencyId3(surveyDto.getDeficiencyId3());
                    existingSurvey.setSurveyDate(surveyDto.getSurveyDate());
                    existingSurvey.setRecommendedProducts(surveyDto.getRecommendedProducts());
                    existingSurvey.setKeywords(surveyDto.getKeywords());
                    Survey updatedSurvey = surveyRepository.save(existingSurvey);
                    return convertToDto(updatedSurvey);
                });
    }

    @Override
    public boolean deleteSurvey(int id) {
        if (surveyRepository.existsById(id)) {
            surveyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public SurveyDto convertToDto(Survey surveyEntity) {
        return new SurveyDto(
            surveyEntity.getSurveyNo(),
            surveyEntity.getMemberUniqueId(),
            surveyEntity.getName(),
            surveyEntity.getAge(),
            surveyEntity.getGender(),
            surveyEntity.getHeight(),
            surveyEntity.getWeight(),
            surveyEntity.getDeficiencyId1(),
            surveyEntity.getDeficiencyId2(),
            surveyEntity.getDeficiencyId3(),
            surveyEntity.getSurveyDate(),
            surveyEntity.getRecommendedProducts(),
            surveyEntity.getKeywords()
        );
    }

    @Override
    public Survey convertToEntity(SurveyDto surveyDto) {
        return new Survey(
            surveyDto.getSurveyNo(),
            surveyDto.getMemberUniqueId(),
            surveyDto.getName(),
            surveyDto.getAge(),
            surveyDto.getGender(),
            surveyDto.getHeight(),
            surveyDto.getWeight(),
            surveyDto.getDeficiencyId1(),
            surveyDto.getDeficiencyId2(),
            surveyDto.getDeficiencyId3(),
            surveyDto.getSurveyDate(),
            surveyDto.getRecommendedProducts(),
            surveyDto.getKeywords()
        );
    }

    @Override
    public SurveyDto createOrUpdateSurvey(SurveyDto surveyDto) {
        System.out.println("Creating or updating survey with data: " + surveyDto); // 디버그 로그 추가
        List<Survey> existingSurveys = surveyRepository.findByMemberUniqueId(surveyDto.getMemberUniqueId());

        if (existingSurveys.isEmpty()) {
            return createSurvey(surveyDto);
        } else {
            Survey existingSurvey = existingSurveys.get(0);
            existingSurvey.setMemberUniqueId(surveyDto.getMemberUniqueId());
            existingSurvey.setName(surveyDto.getName());
            existingSurvey.setAge(surveyDto.getAge());
            existingSurvey.setGender(surveyDto.getGender());
            existingSurvey.setHeight(surveyDto.getHeight());
            existingSurvey.setWeight(surveyDto.getWeight());
            existingSurvey.setDeficiencyId1(surveyDto.getDeficiencyId1());
            existingSurvey.setDeficiencyId2(surveyDto.getDeficiencyId2());
            existingSurvey.setDeficiencyId3(surveyDto.getDeficiencyId3());
            existingSurvey.setSurveyDate(surveyDto.getSurveyDate());
            existingSurvey.setRecommendedProducts(surveyDto.getRecommendedProducts());
            existingSurvey.setKeywords(surveyDto.getKeywords());
            Survey updatedSurvey = surveyRepository.save(existingSurvey);
            System.out.println("Updated survey data: " + updatedSurvey); // 디버그 로그 추가
            return convertToDto(updatedSurvey);
        }
    }

    @Override
    public List<Object[]> getAgeGroupDeficiencyData() {
        String sql = "SELECT " +
                "CASE " +
                "    WHEN age BETWEEN 10 AND 19 THEN '10-19' " +
                "    WHEN age BETWEEN 20 AND 29 THEN '20-29' " +
                "    WHEN age BETWEEN 30 AND 39 THEN '30-39' " +
                "    WHEN age BETWEEN 40 AND 49 THEN '40-49' " +
                "    WHEN age BETWEEN 50 AND 59 THEN '50-59' " +
                "    WHEN age >= 60 THEN '60+' " +
                "END AS age_group, " +
                "deficiency_id1, " +
                "COUNT(*) AS count " +
                "FROM surveys " +
                "GROUP BY age_group, deficiency_id1 " +
                "ORDER BY age_group, count DESC";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
    @Override
    public List<Map<String, Object>> getTopProducts() {
        String sql = "SELECT p.product_id, p.product_name, SUM(od.quantity) AS total_quantity_sold " +
                     "FROM orders o " +
                     "JOIN order_details od ON o.order_no = od.order_no " +
                     "JOIN products p ON od.product_id = p.product_id " +
                     "JOIN payments pay ON o.order_no = pay.order_no " +
                     "WHERE pay.status = 'DONE' " +
                     "GROUP BY p.product_id, p.product_name " +
                     "ORDER BY total_quantity_sold DESC " +
                     "LIMIT 3";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        List<Map<String, Object>> topProducts = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("product_id", result[0]);
            productInfo.put("product_name", result[1]);
            productInfo.put("total_quantity_sold", result[2]);
            topProducts.add(productInfo);
        }

        return topProducts;
    }
}
