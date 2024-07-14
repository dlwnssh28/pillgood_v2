package com.pillgood.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pillgood.dto.ProductDto;
import com.pillgood.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products/list")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/api/products/top-selling")
    public List<ProductDto> getTopSellingProducts() {
        return productService.getTopSellingProducts();
    }

    @GetMapping("/api/products/latest")
    public List<ProductDto> getLatestProducts() {
        return productService.getLatestProducts();
    }

    @PostMapping("/admin/products/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDTO) {
        System.out.println("----adding new product.");
        ProductDto createdProductDTO = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
    }

//    @PostMapping("/create")
//    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDTO) {
//        System.out.println("----adding new product.");
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonProduct = objectMapper.writeValueAsString(productDTO);
//            System.out.println("C 입력 제품 정보: " + jsonProduct);
//        } catch (Exception e) {
//            System.out.println("Error converting productDTO to JSON: " + e.getMessage());
//        }
//        ProductDto createdProductDTO = productService.createProduct(productDTO);
//        return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
//    }

    @PutMapping("/admin/products/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id, @RequestBody ProductDto updatedProductDTO) {
        Optional<ProductDto> productDTO = productService.updateProduct(id, updatedProductDTO);
        return productDTO
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/admin/products/{id}/status")
    public ResponseEntity<Void> updateProductStatus(@PathVariable int id, @RequestBody Map<String, Boolean> status) {
        boolean active = status.get("active");
        boolean result = productService.setActive(id, active);
        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 새로운 엔드포인트 추가
    @GetMapping("/api/products/detail/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        Optional<ProductDto> productDTO = productService.getProductById(id);
        return productDTO
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/api/products/by-deficiency")
    public ResponseEntity<List<ProductDto>> getProductsByDeficiency(@RequestParam List<Integer> deficiencyIds) {
    	System.out.println("(controller) Fetching products for deficiencies: {}"+ deficiencyIds);
        List<ProductDto> products = productService.getProductsByDeficiency(deficiencyIds);
        System.out.println("(controller) Found products: {}"+ products);
        return ResponseEntity.ok(products);
    }
}
