package com.pillgood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.pillgood.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderNo(String orderNo);

    @Query("SELECT p.id, SUM(od.quantity) AS salesCount " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.orderStatus IN ('결제완료', '주문완료', '구매확정') " +
            "GROUP BY p.id " +
            "ORDER BY salesCount DESC")
    List<Object[]> findTopSellingProducts();
    
    void deleteByOrderOrderNo(String orderNo);
}