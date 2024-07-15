package com.pillgood.controller;

import com.pillgood.dto.MemberDto;
import com.pillgood.dto.SocialMemberDto;
import com.pillgood.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PostMapping("/api/members/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
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

    // 카카오 로그인 처리하는 서버 엔드포인트
    @PostMapping("/api/members/kakaoLogin")
    @ResponseBody
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, Object> requestBody, HttpSession session) {
        System.out.println("카카오 로그인 엔드포인트 도달"); // 추가된 로그
        String code = (String) requestBody.get("code");
        String clientId = "03f074279f45f35b6bed2cfbcc42ec4d"; // 카카오 개발자 콘솔에서 발급받은 REST API 키
        String redirectUri = "http://localhost:8080/kakaocallback"; // 리디렉트 URI 확인

        // 액세스 토큰 발급을 위한 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 토큰 요청 로그
        System.out.println("카카오 토큰 요청 URL: " + tokenUrl);
        System.out.println("카카오 토큰 요청 파라미터: " + params);

        ResponseEntity<Map> tokenResponse;
        try {
            tokenResponse = restTemplate.postForEntity(tokenUrl, request, Map.class);
            // 토큰 응답 로그
            System.out.println("카카오 토큰 응답 상태 코드: " + tokenResponse.getStatusCode());
            System.out.println("카카오 토큰 응답 바디: " + tokenResponse.getBody());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "액세스 토큰 발급 실패"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "카카오 API 호출 중 오류가 발생했습니다."));
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 액세스 토큰을 사용하여 카카오 API 호출
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
            );
            // 카카오 사용자 정보 응답 로그
            System.out.println("카카오 사용자 정보 응답 상태 코드: " + response.getStatusCode());
            System.out.println("카카오 사용자 정보 응답 바디: " + response.getBody());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "유효하지 않은 액세스 토큰입니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "카카오 API 호출 중 오류가 발생했습니다."));
        }

        Map<String, Object> userInfo = response.getBody();
        if (userInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "사용자 정보를 가져오지 못했습니다."));
        }

        String kakaoId = String.valueOf(userInfo.get("id"));
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        String nickname = properties != null ? String.valueOf(properties.get("nickname")) : "N/A";

        try {
            if (!memberService.checkSocialId(kakaoId, "kakao")) {
                SocialMemberDto socialMemberDto = new SocialMemberDto();
                socialMemberDto.setSocialId(kakaoId);
                socialMemberDto.setProvider("kakao");
                socialMemberDto.setNickname(nickname);

                boolean isRegistered = memberService.regSocialMember(socialMemberDto);
                if (isRegistered) {
                    System.out.println("새 소셜 멤버 등록 성공: " + socialMemberDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "사용자 정보를 저장하는데 실패했습니다."));
        }

        session.setAttribute("memberId", kakaoId);
        session.setAttribute("nickname", nickname);

        System.out.println("카카오 로그인 성공: 사용자 ID - " + kakaoId + ", 닉네임 - " + nickname);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
