package com.pillgood.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pillgood.dto.PointDto;
import com.pillgood.service.PointService;

import jakarta.servlet.http.HttpSession;

@RestController
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/api/points/list")
    public ResponseEntity<List<PointDto>> getPointsByMemberUniqueId(HttpSession session) {
        String memberUniqueId = (String) session.getAttribute("memberId");
        if (memberUniqueId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        List<PointDto> pointsList = pointService.getPointsByMemberUniqueId(memberUniqueId);
        return new ResponseEntity<>(pointsList, HttpStatus.OK);
    }

    @GetMapping("/api/points/totalPoints")
    public ResponseEntity<Integer> getTotalPointsByMemberUniqueId(HttpSession session) {
        String memberUniqueId = (String) session.getAttribute("memberId");
        if (memberUniqueId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        Integer totalPoints = pointService.getTotalPointsByMemberUniqueId(memberUniqueId);
        return new ResponseEntity<>(totalPoints, HttpStatus.OK);
    }

    @PostMapping("/api/points/use")
    public ResponseEntity<Void> usePoints(HttpSession session, @RequestParam Integer pointsToUse, @RequestParam String referenceId) {
        String memberUniqueId = (String) session.getAttribute("memberId");
        if (memberUniqueId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        try {
            pointService.usePoints(memberUniqueId, pointsToUse, referenceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    @PostMapping("/api/points/refund")
    public ResponseEntity<Void> refundPoints(HttpSession session, @RequestParam Integer pointsToRefund, @RequestParam String referenceId) {
        String memberUniqueId = (String) session.getAttribute("memberId");
        if (memberUniqueId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        try {
            pointService.refundPoints(memberUniqueId, pointsToRefund, referenceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}
