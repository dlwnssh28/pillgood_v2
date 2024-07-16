package com.pillgood.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDetailDto {
    private Integer pointDetailId;
    private String memberUniqueId;
    private String pointStatusCode;
    private Integer points;
    private Integer detailHistoryId;
    private Integer pointId;
    private LocalDateTime transactionDate;
    private LocalDateTime expiryDate;
}
