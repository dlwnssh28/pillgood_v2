package com.pillgood.repository;

import com.pillgood.dto.ShippingAddressDto;
import com.pillgood.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    boolean existsBySocialIdAndProvider(String socialId, String provider); // 이미 정의됨
    Optional<Member> findBySocialIdAndProvider(String socialId, String provider); // 추가된 메서드
    Optional<Member> findByMemberUniqueId(String memberUniqueId); // 수정된 메서드
}
