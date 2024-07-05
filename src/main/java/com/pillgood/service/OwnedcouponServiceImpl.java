package com.pillgood.service;

import com.pillgood.dto.OwnedcouponDto;
import com.pillgood.entity.Coupon;
import com.pillgood.entity.Ownedcoupon;
import com.pillgood.repository.CouponRepository;
import com.pillgood.repository.OwnedcouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnedcouponServiceImpl implements OwnedcouponService {

    private final OwnedcouponRepository ownedcouponRepository;
    private final CouponRepository couponRepository;

    @Override
    public List<OwnedcouponDto> getAllOwnedCoupons() {
        return ownedcouponRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnedcouponDto> getOwnedCouponByMemberId(String memberId) {
        System.out.println(memberId + ": 쿠폰 조회");
        return ownedcouponRepository.findByMemberUniqueIdAndCouponUsedFalse(memberId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OwnedcouponDto> getOwnedCouponById(int ownedCouponId) {
        return ownedcouponRepository.findById(ownedCouponId)
                .map(this::convertToDto);
    }

    @Override
    public OwnedcouponDto createOwnedCoupon(OwnedcouponDto ownedcouponDto) {
        Ownedcoupon ownedcouponEntity = convertToEntity(ownedcouponDto);
        Ownedcoupon savedOwnedCoupon = ownedcouponRepository.save(ownedcouponEntity);
        return convertToDto(savedOwnedCoupon);
    }

    @Override
    public Optional<OwnedcouponDto> updateOwnedCoupon(int ownedCouponId, OwnedcouponDto updatedOwnedcouponDto) {
        return ownedcouponRepository.findById(ownedCouponId)
                .map(ownedcoupon -> {
                    ownedcoupon.setCouponId(updatedOwnedcouponDto.getCouponId());
                    ownedcoupon.setMemberUniqueId(updatedOwnedcouponDto.getMemberUniqueId());
                    ownedcoupon.setCouponUsed(updatedOwnedcouponDto.isCouponUsed());
                    ownedcoupon.setIssuedDate(updatedOwnedcouponDto.getIssuedDate());
                    ownedcoupon.setExpiryDate(updatedOwnedcouponDto.getExpiryDate());
                    Ownedcoupon updatedOwnedCoupon = ownedcouponRepository.save(ownedcoupon);
                    return convertToDto(updatedOwnedCoupon);
                });
    }

    @Override
    public boolean deleteOwnedCoupon(int ownedCouponId) {
        if (ownedcouponRepository.existsById(ownedCouponId)) {
            ownedcouponRepository.deleteById(ownedCouponId);
            return true;
        }
        return false;
    }

    @Override
    public OwnedcouponDto convertToDto(Ownedcoupon ownedcouponEntity) {
        OwnedcouponDto ownedcouponDto = new OwnedcouponDto();
        ownedcouponDto.setOwnedCouponId(ownedcouponEntity.getOwnedCouponId());
        ownedcouponDto.setCouponId(ownedcouponEntity.getCouponId());
        ownedcouponDto.setMemberUniqueId(ownedcouponEntity.getMemberUniqueId());
        ownedcouponDto.setCouponUsed(ownedcouponEntity.isCouponUsed());
        ownedcouponDto.setIssuedDate(ownedcouponEntity.getIssuedDate());
        ownedcouponDto.setExpiryDate(ownedcouponEntity.getExpiryDate());
        Coupon coupon = couponRepository.findById(ownedcouponEntity.getCouponId())
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다: " + ownedcouponEntity.getCouponId()));
        ownedcouponDto.setDiscountAmount(coupon.getDiscountAmount());
        ownedcouponDto.setCouponName(coupon.getCouponName());

        return ownedcouponDto;
    }

    @Override
    public Ownedcoupon convertToEntity(OwnedcouponDto ownedcouponDto) {
        return new Ownedcoupon(
                ownedcouponDto.getOwnedCouponId(),
                ownedcouponDto.getCouponId(),
                ownedcouponDto.getMemberUniqueId(),
                ownedcouponDto.isCouponUsed(),
                ownedcouponDto.getIssuedDate(),
                ownedcouponDto.getExpiryDate()
        );
    }

    @Override
    public void markCouponAsUsed(int ownedCouponId) {
        ownedcouponRepository.findById(ownedCouponId).ifPresent(ownedcoupon -> {
            ownedcoupon.setCouponUsed(true);
            ownedcouponRepository.save(ownedcoupon);
        });
    }
}
