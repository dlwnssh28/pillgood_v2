package com.pillgood.controller;

import com.pillgood.dto.CartDto;
import com.pillgood.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    
    @PostMapping("/api/carts/create")
    public ResponseEntity<CartDto> createCart(HttpSession session, @RequestBody CartDto cartDto) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        cartDto.setMemberUniqueId(memberId);
        Optional<CartDto> createdOrUpdatedCart = cartService.addOrUpdateCart(cartDto);
        return new ResponseEntity<>(createdOrUpdatedCart.get(), HttpStatus.CREATED);
    }
    @GetMapping("/api/carts/find/{id}")
    public Optional<CartDto> getCartById(@PathVariable int id) {
        return cartService.getCartById(id);
    }
    
    @GetMapping("/api/carts/findbyid")
    public ResponseEntity<?> getCartsFindById(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        
        if (memberId == null) {
            return new ResponseEntity<>("세션에 memberId가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        System.out.println(memberId + ": 상품 조회");
        List<CartDto> carts = cartService.getCartByMemberId(memberId);
        
        if (carts.isEmpty()) {
            return new ResponseEntity<>("장바구니에 항목이 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/api/carts/list")
    public List<CartDto> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/api/carts/member/{memberId}")
    public List<CartDto> getCartByMemberId(@PathVariable String memberId) {
        return cartService.getCartByMemberId(memberId);
    }

    @PutMapping("/api/carts/update/{id}")
    public Optional<CartDto> updateCart(@PathVariable int id, @RequestBody CartDto cartDto) {
        return cartService.updateCart(id, cartDto);
    }

    @DeleteMapping("/api/carts/delete/{id}")
    public void deleteCart(@PathVariable int id) {
        boolean deleted = cartService.deleteCart(id);
        if (!deleted) {
            throw new RuntimeException("삭제할 Cart 찾을 수 없음");
        }
    }
}
