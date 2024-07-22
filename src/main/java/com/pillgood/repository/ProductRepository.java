package com.pillgood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pillgood.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>{

    List<Product> findByActive(boolean active);
    
    List<Product> findByOrderByProductRegistrationDateDesc();
    
    @Query("SELECT p FROM Product p JOIN p.nutrient n JOIN DeficiencyNutrient dn ON n.nutrientId = dn.nutrient.nutrientId WHERE dn.deficiency.deficiencyId IN :deficiencyIds")
    List<Product> findByDeficiencyIds(@Param("deficiencyIds") List<Integer> deficiencyIds);

}
