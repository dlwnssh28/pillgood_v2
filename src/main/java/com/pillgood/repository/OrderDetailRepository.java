package com.pillgood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.pillgood.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderNo(String orderNo);

    @Query("SELECT od.product.id, COUNT(od) as salesCount FROM OrderDetail od GROUP BY od.product.id ORDER BY salesCount DESC")
    List<Object[]> findTopSellingProducts();
    
    void deleteByOrderOrderNo(String orderNo);
}