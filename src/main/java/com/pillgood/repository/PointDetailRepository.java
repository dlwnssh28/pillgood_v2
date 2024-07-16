package com.pillgood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pillgood.entity.PointDetail;

@Repository
public interface PointDetailRepository extends JpaRepository<PointDetail, Integer> {
    List<PointDetail> findByMemberUniqueId(String memberUniqueId);
    List<PointDetail> findByMemberUniqueIdAndPointsGreaterThanOrderByTransactionDateAsc(String memberUniqueId, Integer points);
    List<PointDetail> findByMemberUniqueIdAndPointStatusCodeAndPointId(String memberUniqueId, String pointStatusCode, Integer pointId);
    @Query("SELECT COALESCE(MAX(pd.pointDetailId), 0) FROM PointDetail pd")
    Integer findMaxPointDetailId();
}