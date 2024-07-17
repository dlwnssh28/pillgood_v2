package com.pillgood.dto;

import lombok.Data;

@Data
public class SocialMemberDto {
    private String socialId;
    private String provider;
    private String nickname;
    private String email; // 이메일 필드 추가
}
