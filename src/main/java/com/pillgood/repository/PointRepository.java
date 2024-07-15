package com.pillgood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pillgood.entity.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Integer> {
    List<Point> findByMemberUniqueId(String memberUniqueId);

    @Query("SELECT SUM(p.points) FROM Point p WHERE p.memberUniqueId = :memberUniqueId")
    Integer findTotalPointsByMemberUniqueId(String memberUniqueId);
}
