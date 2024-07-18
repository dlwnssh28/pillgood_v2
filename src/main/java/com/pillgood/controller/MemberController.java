package com.pillgood.controller;

import com.pillgood.dto.MemberDto;
import com.pillgood.dto.SocialMemberDto;
import com.pillgood.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/members/register")
    public MemberDto createMember(@RequestBody MemberDto memberDto) {
        System.out.println("createMember called with: " + memberDto);  // 디버깅용 로그
        return memberService.createMember(memberDto);
    }

    @GetMapping("/api/members/findById")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId != null) {
            Optional<MemberDto> memberOpt = memberService.findById(memberId);
            if (memberOpt.isPresent()) {
                MemberDto member = memberOpt.get();
                return ResponseEntity.ok(Collections.singletonMap("user", member));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
            }
        } else {
            System.out.println("세션 확인: 세션이 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
    }

    @PostMapping("/api/members/login")
    public ResponseEntity<?> loginMember(@RequestBody MemberDto memberDto, HttpSession session) {
        Optional<MemberDto> optionalMember = memberService.findByEmail(memberDto.getEmail());
        if (optionalMember.isPresent()) {
            MemberDto foundMember = optionalMember.get();
            if (memberService.checkPassword(memberDto.getPassword(), foundMember.getPassword())) {
                System.out.println("비밀번호 확인: 로그인 성공");
                session.setAttribute("memberId", foundMember.getMemberUniqueId());
                return ResponseEntity.ok("Login successful");
            } else {
                System.out.println("비밀번호 확인: 로그인 실패 - 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            System.out.println("멤버가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/api/members/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId != null) {
            Optional<MemberDto> memberOpt = memberService.findById(memberId);
            if (memberOpt.isPresent()) {
                MemberDto member = memberOpt.get();
                MemberDto responseDto = new MemberDto();
                responseDto.setMemberUniqueId(member.getMemberUniqueId());
                return ResponseEntity.ok(Collections.singletonMap("user", responseDto));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
    }

    @GetMapping("/admin/members/list")
    public List<MemberDto> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PutMapping("/api/members/update/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable String id, @RequestBody MemberDto memberDto) {
        Optional<MemberDto> updatedMember = memberService.updateMember(id, memberDto);
        return updatedMember
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/members/delete/{id}")
    public boolean deleteMember(@PathVariable String id) {
        return memberService.deleteMember(id);
    }

    @GetMapping("/api/members/findByEmail/{email}")
    public Optional<MemberDto> findByEmail(@PathVariable String email) {
        return memberService.findByEmail(email);
    }
  
    // 로그아웃 엔드포인트 추가
    @PostMapping("/api/members/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate(); // 세션 무효화

        // 캐시 관련 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        System.out.println("로그아웃: 세션 무효화");
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/api/members/mypage")
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId != null) {
            Optional<MemberDto> memberOpt = memberService.findById(memberId);
            if (memberOpt.isPresent()) {
                MemberDto member = memberOpt.get();
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
    }

    @PostMapping("/api/members/verifyPassword")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String memberId = request.get("memberId");
        String password = request.get("password");
        Optional<MemberDto> optionalMember = memberService.findById(memberId);
        if (optionalMember.isPresent()) {
            MemberDto foundMember = optionalMember.get();
            if (memberService.checkPassword(password, foundMember.getPassword())) {
                return ResponseEntity.ok("Password verified");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid member");
        }
    }

    // 사용자가 비밀번호 재설정을 위해 이메일 입력 -> 비밀번호 재설정 링크를 전송
    @PostMapping("/api/members/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isSent = memberService.sendResetLink(email);
        if (isSent) {
            return ResponseEntity.ok("Reset link sent");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send reset link");
        }
    }

    // 사용자가 토큰을 사용하여 비밀번호를 재설정
    @PostMapping("/api/members/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        boolean isReset = memberService.resetPassword(token, newPassword);
        if (isReset) {
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }

    @GetMapping("/api/members/status")
    public ResponseEntity<Map<String, Object>> getStatus(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        Map<String, Object> response = new HashMap<>();

        if (loggedIn != null && loggedIn) {
            response.put("isLoggedIn", true);
            response.put("memberId", session.getAttribute("memberId"));
            response.put("member", session.getAttribute("member"));
            response.put("isAdmin", session.getAttribute("isAdmin"));
            response.put("userName", session.getAttribute("userName"));
        } else {
            response.put("isLoggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/members/updateCouponIssued/{id}")
    public ResponseEntity<Void> updateCouponIssued(@PathVariable String id, @RequestBody Map<String, Boolean> request) {
        Optional<MemberDto> updatedMember = memberService.updateCouponIssued(id, request.get("couponIssued"));
        if (updatedMember.isPresent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
