package com.pillgood.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillgood.dto.PointDto;
import com.pillgood.service.PointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/api/points/list")
    public List<PointDto> getAllPoints() {
        return pointService.getAllPoints();
    }

    @GetMapping("/api/points/{id}")
    public ResponseEntity<PointDto> getPointById(@PathVariable int id) {
        Optional<PointDto> pointDto = pointService.getPointById(id);
        return pointDto
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/points/create")
    public ResponseEntity<PointDto> createPoint(@RequestBody PointDto pointDto) {
        PointDto createdPointDto = pointService.createPoint(pointDto);
        return new ResponseEntity<>(createdPointDto, HttpStatus.CREATED);
    }

    @PutMapping("/api/points/update/{id}")
    public ResponseEntity<PointDto> updatePoint(@PathVariable int id, @RequestBody PointDto updatedPointDto) {
        Optional<PointDto> pointDto = pointService.updatePoint(id, updatedPointDto);
        return pointDto
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/api/points/delete/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable int id) {
        boolean deleted = pointService.deletePoint(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
