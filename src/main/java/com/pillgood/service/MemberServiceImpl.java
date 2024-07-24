package com.pillgood.service;

import com.pillgood.config.Role;
import com.pillgood.dto.MemberDto;
import com.pillgood.dto.SocialMemberDto;
import com.pillgood.entity.Member;
import com.pillgood.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(MemberServiceImpl.class.getName());

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public Optional<MemberDto> findById(String memberId) {
        return memberRepository.findById(memberId)
                .map(this::convertToDto);
    }

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        Member member = convertToEntity(memberDto);
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        member.setMemberLevel(Role.USER);
        member.setSubscriptionStatus(false);
        member.setRegistrationDate(LocalDateTime.now());
        member = memberRepository.save(member);

        return convertToDto(member);
    }

    @Override
    public Optional<MemberDto> getMemberById(String id) {
        return memberRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MemberDto> updateMember(String id, MemberDto memberDto) {
        return memberRepository.findById(id)
                .map(existingMember -> {
                    Member updatedMember = convertToEntity(memberDto);
                    updatedMember.setMemberUniqueId(existingMember.getMemberUniqueId());
                    updatedMember.setPassword(existingMember.getPassword()); // 기존 비밀번호 유지
                    updatedMember.setModifiedDate(LocalDateTime.now());
                    updatedMember = memberRepository.save(updatedMember);
                    return convertToDto(updatedMember);
                });
    }

    @Override
    public boolean deleteMember(String id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<MemberDto> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(this::convertToDto);
    }

    @Override
    public boolean sendResetLink(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // 이메일로 재설정 링크를 전송하는 로직 추가
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        // 비밀번호 재설정 로직 추가
        return true;
    }

    @Override
    public Optional<MemberDto> updateCouponIssued(String memberId, boolean couponIssued) {
        return memberRepository.findById(memberId)
                .map(member -> {
                    member.setCouponIssued(couponIssued);
                    member.setModifiedDate(LocalDateTime.now());
                    Member updatedMember = memberRepository.save(member);
                    return convertToDto(updatedMember);
                });
    }

    @Override
    public boolean checkSocialId(String socialId, String provider) {
        logger.info("checkSocialId 호출됨: socialId=" + socialId + ", provider=" + provider);
        return memberRepository.existsBySocialIdAndProvider(socialId, provider);
    }

    @Override
    public boolean regSocialMember(SocialMemberDto socialMemberDto) {
        logger.info("regSocialMember 호출됨: socialMemberDto=" + socialMemberDto);

        Member member = new Member();
        member.setMemberUniqueId(UUID.randomUUID().toString().replace("-", ""));
        member.setEmail(UUID.randomUUID().toString() + "@default.com"); // 기본 이메일 설정
        member.setName(socialMemberDto.getNickname());
        member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        member.setMemberLevel(Role.USER);
        member.setRegistrationDate(LocalDateTime.now());
        member.setProvider(socialMemberDto.getProvider());
        member.setSocialId(socialMemberDto.getSocialId());
        member.setAge(0); // 기본값 설정
        member.setGender("unknown"); // 기본값 설정
        member.setPhoneNumber("000-0000-0000"); // 기본 전화번호 설정
        memberRepository.save(member);
        return true;
    }

    // 추가된 메서드
    @Override
    public Optional<MemberDto> findBySocialId(String socialId, String provider) {
        return memberRepository.findBySocialIdAndProvider(socialId, provider)
                .map(this::convertToDto);
    }
    
    @Override
    public void updateSubscriptionStatus(String memberId, int status) {
        memberRepository.findById(memberId)
                        .ifPresent(member -> {
                            member.setSubscriptionStatus(status == 1);
                            memberRepository.save(member);
                        });
    }

    private MemberDto convertToDto(Member member) {
        return new MemberDto(
                member.getMemberUniqueId(),
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getAge(),
                member.getGender(),
                member.getPhoneNumber(),
                member.getRegistrationDate(),
                member.getSubscriptionStatus(),
                member.getModifiedDate(),
                member.getMemberLevel(),
                member.isCouponIssued()
        );
    }

    private Member convertToEntity(MemberDto memberDto) {
        Member member = new Member();
        member.setMemberUniqueId(memberDto.getMemberUniqueId() != null ? memberDto.getMemberUniqueId() : UUID.randomUUID().toString().replace("-", ""));
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
        member.setName(memberDto.getName());
        member.setAge(memberDto.getAge());
        member.setGender(memberDto.getGender());
        member.setPhoneNumber(memberDto.getPhoneNumber() != null ? memberDto.getPhoneNumber() : "000-0000-0000"); // 기본 전화번호 설정
        member.setRegistrationDate(memberDto.getRegistrationDate());
        member.setSubscriptionStatus(memberDto.getSubscriptionStatus());
        member.setModifiedDate(memberDto.getModifiedDate());
        member.setMemberLevel(memberDto.getMemberLevel());
        member.setCouponIssued(memberDto.isCouponIssued());
        return member;
    }
}
