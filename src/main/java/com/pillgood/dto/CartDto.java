package com.pillgood.dto;

import com.pillgood.entity.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
	private int cartNo;
	private String productName;
    private String memberUniqueId;
    private int productId;
    private int productQuantity;
    private int price; // 추가
    
}
