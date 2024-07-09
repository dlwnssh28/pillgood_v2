package com.pillgood.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class FileUploadController {

    // 실제 업로드 경로 설정 (예: 프로젝트 루트 디렉토리의 uploads 폴더)
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/api/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 업로드 폴더가 존재하지 않으면 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 및 저장 경로 설정
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + "_" + originalFileName;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 파일 URL 설정
            String fileUrl = "/uploads/" + filename;
            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Image upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api/delete/image")
    public ResponseEntity<String> deleteImage(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            System.out.println("삭제 요청 이미지 URL: " + url);

            // URL에서 파일 경로만 추출
            String filePathStr = url.replace("http://localhost:9095/uploads", "");
            filePathStr = URLDecoder.decode(filePathStr, StandardCharsets.UTF_8.name()); // URL 디코딩
            Path filePath = Paths.get(uploadDir, filePathStr);

            System.out.println("파일 경로: " + filePath.toString());

            // 파일이 존재하는지 확인하고 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return new ResponseEntity<>("Image deleted successfully", HttpStatus.OK);
            } else {
                System.out.println("파일을 찾을 수 없음: " + filePath.toString());
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("이미지 삭제 실패: " + e.getMessage());
            return new ResponseEntity<>("Image deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}