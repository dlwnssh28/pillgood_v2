package com.pillgood.service;

import com.pillgood.config.JwtConfig;
import com.pillgood.config.Role;
import com.pillgood.dto.MemberDto;
import com.pillgood.dto.SocialMemberDto;
import com.pillgood.entity.Member;
import com.pillgood.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // 이메일 전송을 위한 JavaMailSender
    private final JwtConfig jwtConfig; // JWT 설정 클래스

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
        member.setPhoneNumber(memberDto.getPhoneNumber());
        member.setRegistrationDate(memberDto.getRegistrationDate());
        member.setSubscriptionStatus(memberDto.getSubscriptionStatus());
        member.setModifiedDate(memberDto.getModifiedDate());
        member.setMemberLevel(memberDto.getMemberLevel());
        member.setCouponIssued(memberDto.isCouponIssued());
        return member;
    }
  
    @Override
    public boolean sendResetLink(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            String token = generateResetToken(member);

            // 이메일로 재설정 링크를 전송합니다.
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Link");
            message.setText("Click the link to reset your password: http://localhost:8080/changepassword?token=" + token);
            mailSender.send(message);

            return true;
        } else {
            return false;
        }
    }


    // JWT 토큰을 생성
    private String generateResetToken(Member member) {
        long expirationTime = 1000 * 60 * 60; // 1시간
        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim("memberId", member.getMemberUniqueId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
    }

    // 블랙리스트를 위한 ConcurrentHashMap
    private final ConcurrentHashMap<String, Boolean> tokenBlacklist = new ConcurrentHashMap<>();

    // 사용자가 토큰을 사용하여 비밀번호를 재설정합니다.
    @Override
    public boolean resetPassword(String token, String newPassword) {
        try {
            if (tokenBlacklist.containsKey(token)) {
                return false; // 토큰이 블랙리스트에 있으면 비밀번호 재설정을 허용하지 않음
            }

            Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
            String memberId = claims.get("memberId", String.class);

            Optional<Member> optionalMember = memberRepository.findById(memberId);
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();
                member.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화
                memberRepository.save(member);

                // 토큰을 블랙리스트에 추가하여 다시 사용되지 않도록 함
                tokenBlacklist.put(token, true);

                return true;
            } else {
                return false;
            }
        } catch (ExpiredJwtException e) {
            return false; // 토큰이 만료된 경우
        } catch (Exception e) {
            return false;
        }
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
}
