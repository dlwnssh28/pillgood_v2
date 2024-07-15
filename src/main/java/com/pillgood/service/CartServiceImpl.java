package com.pillgood.service;

import com.pillgood.dto.CartDto;
import com.pillgood.entity.Cart;
import com.pillgood.entity.Product;
import com.pillgood.repository.CartRepository;
import com.pillgood.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository; // 추가

    @Override
    public CartDto createCart(CartDto cartDto) {
        Cart cart = convertToEntity(cartDto);
        cartRepository.save(cart);
        return convertToDto(cart);
    }

    @Override
    public Optional<CartDto> getCartById(int id) {
        Optional<Cart> cart = cartRepository.findById(id);
        return cart.map(this::convertToDto);
    }

    @Override
    public List<CartDto> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartDto> getCartByMemberId(String memberId) {
        System.out.println(memberId + ": 상품 조회");
        return cartRepository.findByMemberUniqueId(memberId).stream()
                .map(this::convertToDtoWithProductName)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CartDto> updateCart(int id, CartDto cartDto) {
        Optional<Cart> cart = cartRepository.findById(id);
        if (cart.isPresent()) {
            Cart updatedCart = cart.get();
            updatedCart.setProductQuantity(cartDto.getProductQuantity());
            cartRepository.save(updatedCart);
            return Optional.of(convertToDto(updatedCart));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteCart(int cartId, String memberId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent() && cart.get().getMemberUniqueId().equals(memberId)) {
            cartRepository.delete(cart.get());
            return true;
        }
        return false;
    }
    
    @Override
    public void deleteCarts(List<Integer> productIds, String memberId) {
        List<Cart> carts = cartRepository.findByMemberUniqueId(memberId);
        for (Cart cart : carts) {
            if (productIds.contains(cart.getProductId())) {
                cartRepository.delete(cart);
            }
        }
    }
    
    @Override
    public Optional<CartDto> addOrUpdateCart(CartDto cartDto) {
        List<Cart> existingCarts = cartRepository.findByMemberUniqueId(cartDto.getMemberUniqueId());
        Optional<Cart> existingCart = existingCarts.stream()
                .filter(cart -> cart.getProductId() == cartDto.getProductId())
                .findFirst();

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setProductQuantity(cart.getProductQuantity() + cartDto.getProductQuantity());
            cartRepository.save(cart);
            return Optional.of(convertToDto(cart));
        } else {
            return Optional.of(createCart(cartDto));
        }
    }

    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setCartNo(cart.getCartNo());
        cartDto.setMemberUniqueId(cart.getMemberUniqueId());
        cartDto.setProductId(cart.getProductId());
        cartDto.setProductQuantity(cart.getProductQuantity());
        
     // productId를 통해 products 테이블에서 상품 정보를 가져옴
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + cart.getProductId()));
        cartDto.setPrice(product.getPrice());
        cartDto.setProductName(product.getProductName()); // 상품 이름 설정
        
        return cartDto;
    }

    private CartDto convertToDtoWithProductName(Cart cart) {
        CartDto cartDto = convertToDto(cart);
        // productId를 통해 products 테이블에서 productName 정보를 가져옴
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + cart.getProductId()));
        cartDto.setProductName(product.getProductName());
        
        return cartDto;
    }

    private Cart convertToEntity(CartDto cartDto) {
        Cart cart = new Cart();
        cart.setCartNo(cartDto.getCartNo());
        cart.setMemberUniqueId(cartDto.getMemberUniqueId());
        cart.setProductId(cartDto.getProductId());
        cart.setProductQuantity(cartDto.getProductQuantity());
        return cart;
    }
}