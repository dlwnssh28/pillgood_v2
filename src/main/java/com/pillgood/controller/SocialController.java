package com.pillgood.controller;

import com.pillgood.dto.SocialMemberDto;
import com.pillgood.config.Role;
import com.pillgood.dto.MemberDto;
import com.pillgood.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;
    private final RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(SocialController.class.getName());
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @PostMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestBody Map<String, String> body, HttpSession session, HttpServletResponse response) {

        String accessToken = body.get("accessToken");
        if (accessToken == null) {
            logger.severe("Access token is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access token is missing");
        }

        // 액세스 토큰 로그 기록
        logger.info("Received Kakao access token: " + accessToken);
        
        // 세션에 액세스 토큰 저장
        session.setAttribute("kakaoAccessToken", accessToken);
        
        // 액세스 토큰을 HTTP Only 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("kakaoAccessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 사용
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(360000); // 1시간 유효기간
        response.addCookie(accessTokenCookie);

        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
        if (userInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get user info from Kakao");
        }

        logger.info("카카오 사용자 정보: " + userInfo);

        String socialId = userInfo.get("id").toString();
        String nickname = ((Map<String, String>) userInfo.get("properties")).get("nickname");

        SocialMemberDto socialMemberDto = new SocialMemberDto();
        socialMemberDto.setSocialId(socialId);
        socialMemberDto.setProvider("KAKAO");
        socialMemberDto.setNickname(nickname);

        boolean exists = memberService.checkSocialId(socialId, "KAKAO");
        if (exists) {
            logger.info("기존 소셜 회원 로그인: " + socialId);

            Optional<MemberDto> optionalMember = memberService.findBySocialId(socialId, "KAKAO");
            if (optionalMember.isPresent()) {
                MemberDto member = optionalMember.get();
                // 직접 세션 속성 설정
                session.setAttribute("memberId", member.getMemberUniqueId());
                session.setAttribute("loggedIn", true);
                session.setAttribute("member", member);
                session.setAttribute("isAdmin", member.getMemberLevel() == Role.ADMIN);

                // 세션 정보 로깅
                logSessionAttributes(session);

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Social login successful",
                    "memberId", member.getMemberUniqueId(),
                    "isAdmin", member.getMemberLevel() == Role.ADMIN,
                    "member", member
                ));
            } else {
                logger.severe("소셜 회원 정보를 찾을 수 없습니다: " + socialId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to retrieve social member information"
                ));
            }
        } else {
            boolean registered = memberService.regSocialMember(socialMemberDto);
            if (registered) {
                logger.info("신규 소셜 회원 등록: " + socialId);

                Optional<MemberDto> optionalMember = memberService.findBySocialId(socialId, "KAKAO");
                if (optionalMember.isPresent()) {
                    MemberDto member = optionalMember.get();
                    // 직접 세션 속성 설정
                    session.setAttribute("memberId", member.getMemberUniqueId());
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("member", member);
                    session.setAttribute("isAdmin", member.getMemberLevel() == Role.ADMIN);

                    // 세션 정보 로깅
                    logSessionAttributes(session);

                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Social registration successful",
                        "memberId", member.getMemberUniqueId(),
                        "isAdmin", member.getMemberLevel() == Role.ADMIN,
                        "member", member
                    ));
                } else {
                    logger.severe("소셜 회원 정보를 찾을 수 없습니다: " + socialId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Failed to retrieve social member information"
                    ));
                }
            } else {
                logger.severe("소셜 회원 등록 실패: " + socialId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Social registration failed"
                ));
            }
        }
    }

    private void logSessionAttributes(HttpSession session) {
        logger.info("Session ID: " + session.getId());
        logger.info("Session memberId: " + session.getAttribute("memberId"));
        logger.info("Session loggedIn: " + session.getAttribute("loggedIn"));
        logger.info("Session member: " + session.getAttribute("member"));
        logger.info("Session isAdmin: " + session.getAttribute("isAdmin"));
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            logger.severe("Failed to get user info from Kakao: " + response.getStatusCode());
            return null;
        }
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(HttpSession session) {
        Map<String, Object> sessionAttributes = Map.of(
            "sessionId", session.getId(),
            "memberId", session.getAttribute("memberId"),
            "loggedIn", session.getAttribute("loggedIn"),
            "member", session.getAttribute("member"),
            "isAdmin", session.getAttribute("isAdmin")
        );
        return ResponseEntity.ok(sessionAttributes);
    }
}
