package com.pillgood.controller;

import com.pillgood.dto.DeficiencyDto;
import com.pillgood.dto.DeficiencyNutrientDto;
import com.pillgood.service.DeficiencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DeficiencyController {

    private final DeficiencyService deficiencyService;

    // 새로운 부족 생성 폼
    @GetMapping("/admin/deficiencies/create")
    public String deficiencyForm(Model model) {
        model.addAttribute("deficiencyDto", new DeficiencyDto());
        return "deficiencyForm";
    }

    // 새로운 부족 생성
    @PostMapping("/admin/deficiencies/create")
    public String createDeficiency(@RequestBody DeficiencyDto deficiencyDto, Model model) {
        DeficiencyDto createdDeficiency = deficiencyService.createDeficiency(deficiencyDto);
        model.addAttribute("deficiencyDto", createdDeficiency);
        return "redirect:/admin/deficiencies";
    }

    // ID로 부족 조회
    @GetMapping("/api/deficiencies/find/{id}")
    public String getDeficiencyById(@PathVariable int id, Model model) {
        Optional<DeficiencyDto> deficiencyDto = deficiencyService.getDeficiencyById(id);
        if (deficiencyDto.isPresent()) {
            model.addAttribute("deficiencyDto", deficiencyDto.get());
            return "deficiencyList";
        } else {
            return "deficiencyNotFound";
        }
    }

    // 모든 부족 조회
    @GetMapping("/api/deficiencies/list")
    public List<DeficiencyDto> getAllDeficiencies() {
        return deficiencyService.getAllDeficiencies();
    }

    @GetMapping("/api/deficiencies/nutrients")
    public ResponseEntity<List<DeficiencyNutrientDto>> getDeficiencyNutrientsWithNames() {
//        System.out.println("GET /api/deficiencies/nutrients called");

        List<DeficiencyNutrientDto> deficiencyNutrients = deficiencyService.getDeficiencyNutrientsWithNames();
//        System.out.println("Deficiency Nutrients: {}"+ deficiencyNutrients);

        return ResponseEntity.ok(deficiencyNutrients);
    }

    // 부족 수정 폼
    @GetMapping("/admin/deficiencies/update/{id}")
    public String updateDeficiency(@PathVariable int id, Model model) {
        Optional<DeficiencyDto> deficiencyDto = deficiencyService.getDeficiencyById(id);
        if (deficiencyDto.isPresent()) {
            model.addAttribute("deficiencyDto", deficiencyDto.get());
            return "deficiencyForm";
        } else {
            return "deficiencyNotFound";
        }
    }

    // 부족 수정
    @PutMapping("/admin/deficiencies/update/{id}")
    public String updateDeficiency(@PathVariable int id, @RequestBody DeficiencyDto deficiencyDto, Model model) {
        Optional<DeficiencyDto> updatedDeficiency = deficiencyService.updateDeficiency(id, deficiencyDto);
        if (updatedDeficiency.isPresent()) {
            model.addAttribute("deficiencyDto", updatedDeficiency.get());
            return "redirect:/admin/deficiencies";
        } else {
            return "deficiencyNotFound";
        }
    }

    // 부족 삭제
    @DeleteMapping("/admin/deficiencies/delete/{id}")
    public void deleteDeficiency(@PathVariable int id) {
        boolean deleted = deficiencyService.deleteDeficiency(id);
        if (!deleted) {
            throw new RuntimeException("삭제할 Deficiency 찾을 수 없음");
        }
    }
}
