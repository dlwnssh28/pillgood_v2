package com.pillgood.service;

import com.pillgood.dto.DeficiencyDto;
import com.pillgood.dto.DeficiencyNutrientDto;
import com.pillgood.entity.Deficiency;
import com.pillgood.entity.DeficiencyNutrient;
import com.pillgood.repository.DeficiencyNutrientRepository;
import com.pillgood.repository.DeficiencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeficiencyServiceImpl implements DeficiencyService {

    @Autowired
    private final DeficiencyRepository deficiencyRepository;

    @Override
    public DeficiencyDto createDeficiency(DeficiencyDto deficiencyDto){
        Deficiency deficiency = new Deficiency();
        deficiency.setDeficiencyName(deficiencyDto.getDeficiencyName());
        deficiency = deficiencyRepository.save(deficiency);
        deficiencyDto.setDeficiencyId(deficiency.getDeficiencyId());
        return deficiencyDto;
    }

    @Override
    public Optional<DeficiencyDto> getDeficiencyById(int id) {
        return deficiencyRepository.findById(id)
                .map(deficiency -> new DeficiencyDto(
                        deficiency.getDeficiencyId(),
                        deficiency.getDeficiencyName()
                ));
    }

    @Override
    public List<DeficiencyDto> getAllDeficiencies() {
        List<Deficiency> deficiencies = deficiencyRepository.findAll();
        List<DeficiencyDto> deficiencyDtos = new ArrayList<>();
        for (Deficiency deficiency : deficiencies) {
            DeficiencyDto deficiencyDto = new DeficiencyDto(
                    deficiency.getDeficiencyId(),
                    deficiency.getDeficiencyName()
            );
            deficiencyDtos.add(deficiencyDto);
        }
        return deficiencyDtos;
    }

    @Override
    public Optional<DeficiencyDto> updateDeficiency(int id, DeficiencyDto deficiencyDto) {
        return deficiencyRepository.findById(id)
                .map(deficiency -> {
                    deficiency.setDeficiencyName(deficiencyDto.getDeficiencyName());
                    deficiency = deficiencyRepository.save(deficiency);
                    return new DeficiencyDto(deficiency.getDeficiencyId(), deficiency.getDeficiencyName());
                });
    }

    @Override
    public boolean deleteDeficiency(int id) {
        if (deficiencyRepository.existsById(id)) {
            deficiencyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Autowired
    private final DeficiencyNutrientRepository deficiencyNutrientRepository;

    @Override
    public List<DeficiencyNutrientDto> getDeficiencyNutrientsWithNames() {
        List<DeficiencyNutrient> deficiencyNutrients = deficiencyNutrientRepository.findAll();
        System.out.println("Fetched Deficiency Nutrients: {}"+ deficiencyNutrients);

        return deficiencyNutrients.stream().map(dn -> {
            Deficiency deficiency = dn.getDeficiency();
//            logger.debug("Deficiency: {}", deficiency);
            System.out.println("Deficiency: {}"+ deficiency);

            int deficiencyNutrientId = dn.getDeficiencyNutrientId();
            int deficiencyId = deficiency != null ? deficiency.getDeficiencyId() : -1;
            String deficiencyName = deficiency != null ? deficiency.getDeficiencyName() : "N/A";
            int nutrientId = dn.getNutrient().getNutrientId();

//            logger.debug("Mapping DTO: deficiencyNutrientId={}, deficiencyId={}, nutrientId={}, deficiencyName={}",
//                    deficiencyNutrientId, deficiencyId, nutrientId, deficiencyName);
            System.out.println("Mapping DTO: deficiencyNutrientId={}, deficiencyId={}, nutrientId={}, deficiencyName={}"+
                    deficiencyNutrientId+deficiencyId+nutrientId+deficiencyName);

            DeficiencyNutrientDto dto = new DeficiencyNutrientDto(
                    deficiencyNutrientId,
                    deficiencyId,
                    nutrientId,
                    deficiencyName
            );

//            logger.debug("Mapped DTO: {}", dto);
            System.out.println("Mapped DTO: {}"+ dto);
            return dto;
        }).collect(Collectors.toList());
    }

}
