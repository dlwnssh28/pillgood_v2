package com.pillgood.repository;

import com.pillgood.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {
    @Query("SELECT " +
            "CASE " +
            "    WHEN s.age BETWEEN 10 AND 19 THEN '10-19' " +
            "    WHEN s.age BETWEEN 20 AND 29 THEN '20-29' " +
            "    WHEN s.age BETWEEN 30 AND 39 THEN '30-39' " +
            "    WHEN s.age BETWEEN 40 AND 49 THEN '40-49' " +
            "    WHEN s.age BETWEEN 50 AND 59 THEN '50-59' " +
            "    WHEN s.age >= 60 THEN '60+' " +
            "END AS age_group, " +
            "s.recommendedProducts, " +
            "COUNT(s) AS count " +
            "FROM Survey s " +
            "JOIN Order o ON s.memberUniqueId = o.memberUniqueId " +
            "JOIN Payment p ON o.orderNo = p.orderNo " +
            "WHERE p.status = 'DONE' " +
            "GROUP BY age_group, s.recommendedProducts " +
            "ORDER BY age_group, count DESC")
    List<Object[]> findTopProductsByAgeGroup();

    List<Survey> findByMemberUniqueId(String memberUniqueId);
}
