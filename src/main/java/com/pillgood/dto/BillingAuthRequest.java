package com.pillgood.dto;

import lombok.Data;

@Data
public class BillingAuthRequest {
    private String customerKey;
    private String authKey;
}
