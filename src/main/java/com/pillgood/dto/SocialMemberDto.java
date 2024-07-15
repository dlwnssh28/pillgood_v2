package com.pillgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialMemberDto {
    private String socialId;
    private String provider;
    private String nickname;
}
