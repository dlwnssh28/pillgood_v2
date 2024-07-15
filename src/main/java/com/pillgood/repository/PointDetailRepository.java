package com.pillgood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pillgood.entity.PointDetail;

@Repository
public interface PointDetailRepository extends JpaRepository<PointDetail, Integer> {
    List<PointDetail> findByMemberUniqueId(String memberUniqueId);
    List<PointDetail> findByMemberUniqueIdAndPointsGreaterThanOrderByTransactionDateAsc(String memberUniqueId, Integer points);
}
