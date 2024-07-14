package com.pillgood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pillgood.entity.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByMemberUniqueId(String memberUniqueId);
    Optional<Order> findByOrderNo(String orderNo); // 추가된 메소드
}
