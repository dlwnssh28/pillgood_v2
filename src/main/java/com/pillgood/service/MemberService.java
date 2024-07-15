package com.pillgood.service;

import com.pillgood.dto.MemberDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    MemberDto createMember(MemberDto memberDto);
    Optional<MemberDto> getMemberById(String id);
    List<MemberDto> getAllMembers();
    Optional<MemberDto> updateMember(String id, MemberDto memberDto);
    boolean deleteMember(String id);
    Optional<MemberDto> findByEmail(String email);
    boolean checkPassword(String rawPassword, String encodedPassword);
    Optional<MemberDto> findById(String memberId);
    boolean sendResetLink(String email);
    boolean resetPassword(String token, String newPassword);

    Optional<MemberDto> updateCouponIssued(String memberId, boolean couponIssued);
}