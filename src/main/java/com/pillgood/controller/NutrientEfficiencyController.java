package com.pillgood.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pillgood.dto.NutrientEfficiencyDto;
import com.pillgood.service.NutrientEfficiencyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NutrientEfficiencyController {

    private final NutrientEfficiencyService nutrientEfficiencyService;

    @GetMapping("/api/nutrientefficiencies/list")
    public List<NutrientEfficiencyDto> getAllNutrientEfficiencies() {
        return nutrientEfficiencyService.getAllNutrientEfficiencies();
    }

    @PostMapping("/admin/nutrientefficiencies/create")
    public ResponseEntity<NutrientEfficiencyDto> createNutrientEfficiency(@RequestBody NutrientEfficiencyDto nutrientEfficiencyDTO) {
        NutrientEfficiencyDto createdNutrientEfficiency = nutrientEfficiencyService.createNutrientEfficiency(nutrientEfficiencyDTO);
        return ResponseEntity.ok(createdNutrientEfficiency);
    }

    @PutMapping("/admin/nutrientefficiencies/update/{id}")
    public ResponseEntity<NutrientEfficiencyDto> updateNutrientEfficiency(
            @PathVariable int id,
            @RequestBody NutrientEfficiencyDto updatedNutrientEfficiencyDTO) {
        Optional<NutrientEfficiencyDto> updatedNutrientEfficiency = nutrientEfficiencyService.updateNutrientEfficiency(id, updatedNutrientEfficiencyDTO);
        return updatedNutrientEfficiency.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/nutrientefficiencies/delete/{id}")
    public ResponseEntity<Void> deleteNutrientEfficiency(@PathVariable int id) {
        boolean isDeleted = nutrientEfficiencyService.deleteNutrientEfficiency(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
