package com.pillgood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Integer orderDetailNo;
    private String orderNo;
    private Integer productId;
    private Integer quantity;
    private Integer amount;
}
