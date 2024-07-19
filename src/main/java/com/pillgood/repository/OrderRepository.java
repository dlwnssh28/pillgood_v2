package com.pillgood.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pillgood.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Modifying
    @Query("UPDATE Order o SET o.pointsToUse = :pointsToUse WHERE o.orderNo = :orderNo")
    void updatePointsToUseByOrderNo(@Param("orderNo") String orderNo, @Param("pointsToUse") Integer pointsToUse);
    
    Optional<Order> findByOrderNo(String orderNo);
    
    List<Order> findByMemberUniqueId(String memberUniqueId);
    
    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :status WHERE o.orderNo = :orderNo")
    void updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") String status);
}
