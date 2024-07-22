package com.pillgood.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pillgood.entity.Nutrient;
import com.pillgood.repository.NutrientRepository;
import com.pillgood.repository.OrderDetailRepository;

import org.springframework.stereotype.Service;

import com.pillgood.dto.ProductDto;
import com.pillgood.entity.Product;
import com.pillgood.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor 
public class ProductServiceImpl implements ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final NutrientRepository nutrientRepository;
    private final OrderDetailRepository orderDetailRepository;
  
    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getTopSellingProducts() {
        List<Object[]> results = orderDetailRepository.findTopSellingProducts();
        return results.stream()
                .map(result -> {
                    int productId = (int) result[0];
                    long salesCount = (long) result[1];
                    Product product = productRepository.findById(productId).orElse(null);
                    ProductDto productDto = convertToDTO(product);
                    productDto.setSalesCount(salesCount);  // salesCount를 ProductDto에 추가합니다.
                    return productDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getLatestProducts() {
        return productRepository.findByOrderByProductRegistrationDateDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDTO) {
        // dto -> entity 변환
        Product product = convertToEntity(productDTO);
        // nutrient 엔티티 병합
        Nutrient nutrient = nutrientRepository.findById(product.getNutrient().getNutrientId())
                .orElseThrow(() -> new RuntimeException("Nutrient를 찾을 수 없음"));
        // product 엔티티 저장
        product.setProductRegistrationDate(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    public Optional<ProductDto> updateProduct(int id, ProductDto updatedProductDTO) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setProductName(updatedProductDTO.getProductName());

                    // Fetch the Nutrient entity using the provided nutrient ID
                    Nutrient nutrient = nutrientRepository.findById(updatedProductDTO.getNutrientId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid nutrient ID: " + updatedProductDTO.getNutrientId()));
                    product.setNutrient(nutrient);
//                    product.setNutrient(updatedProductDTO.getNutrientId());
                    product.setProductImage(updatedProductDTO.getProductImage());
                    product.setPrice(updatedProductDTO.getPrice());
                    product.setStock(updatedProductDTO.getStock());
                    product.setProductRegistrationDate(updatedProductDTO.getProductRegistrationDate());
                    product.setTarget(updatedProductDTO.getTarget());
                    product.setActive(updatedProductDTO.isActive());
                    Product updatedProduct = productRepository.save(product);
                    return convertToDTO(updatedProduct);
                });
    }

    @Override
    public ProductDto convertToDTO(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getNutrient().getNutrientId(),
                product.getProductName(),
                product.getProductImage(),
                product.getPrice(),
                product.getStock(),
                product.getProductRegistrationDate(),
                product.getTarget(),
                product.isActive(),
                0 // 기본값으로 0을 설정 (판매량)
        );
    }

    @Override
    public Product convertToEntity(ProductDto productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        // nutrientId로 Nutrient 객체 설정
        Nutrient nutrient = new Nutrient();
        nutrient.setNutrientId(productDTO.getNutrientId());
        product.setNutrient(nutrient);

        product.setProductName(productDTO.getProductName());
        product.setProductImage(productDTO.getProductImage());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setProductRegistrationDate(productDTO.getProductRegistrationDate());
        product.setTarget(productDTO.getTarget());
      
        product.setActive(productDTO.isActive()); // boolean 타입으로 설정

        return product;
    }

    @Transactional
    @Override
    public boolean setActive(int productId, boolean active) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
            product.setActive(active);
            productRepository.save(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<ProductDto> getProductById(int id) {
        return productRepository.findById(id).map(ProductDto::new);
    }

    @Override
    public List<ProductDto> getProductsByDeficiency(List<Integer> deficiencyIds) {
//        System.out.println("Getting products by deficiency IDs: " + deficiencyIds);
        List<Product> products = productRepository.findByDeficiencyIds(deficiencyIds);
        List<ProductDto> productDtos = products.stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());
//        System.out.println("Mapped products to DTOs: " + productDtos);
        return productDtos;
    }
    
}
