package com.pillgood.service;

import com.pillgood.dto.MemberDto;
import com.pillgood.dto.SocialMemberDto;

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

    // 추가된 메서드
    boolean checkSocialId(String socialId, String provider);
    boolean regSocialMember(SocialMemberDto socialMemberDto);
}
