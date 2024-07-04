package com.pillgood.controller;

import com.pillgood.dto.EfficiencyDto;
import com.pillgood.service.EfficiencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class EfficiencyController {

    private final EfficiencyService efficiencyService;

    @PostMapping("/admin/efficiencies/create")
    public EfficiencyDto createEfficiency(@RequestBody EfficiencyDto efficiencyDto) {
        return efficiencyService.createEfficiency(efficiencyDto);
    }

    @GetMapping("/admin/efficiencies/find/{id}")
    public Optional<EfficiencyDto> getEfficiencyById(@PathVariable int id) {
        return efficiencyService.getEfficiencyById(id);
    }

    @GetMapping("/admin/efficiencies/list")
    public List<EfficiencyDto> getAllEfficiencies() {
        return efficiencyService.getAllEfficiencies();
    }

    @PutMapping("/admin/efficiencies/update/{id}")
    public Optional<EfficiencyDto> updateEfficiency(@PathVariable int id, @RequestBody EfficiencyDto efficiencyDto) {
        return efficiencyService.updateEfficiency(id, efficiencyDto);
    }

    @DeleteMapping("/admin/efficiencies/delete/{id}")
    public boolean deleteEfficiency(@PathVariable int id) {
        return efficiencyService.deleteEfficiency(id);
    }
}
