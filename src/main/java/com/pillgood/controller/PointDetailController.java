package com.pillgood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pillgood.dto.PointDetailDto;
import com.pillgood.service.PointDetailService;

@RestController
@RequestMapping("/api/point-details")
public class PointDetailController {

    @Autowired
    private PointDetailService pointDetailService;

    @PostMapping
    public ResponseEntity<PointDetailDto> createPointDetail(@RequestBody PointDetailDto pointDetailDto) {
        PointDetailDto createdPointDetail = pointDetailService.createPointDetail(pointDetailDto);
        return new ResponseEntity<>(createdPointDetail, HttpStatus.CREATED);
    }
}
