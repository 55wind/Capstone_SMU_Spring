package org.smu.capstone_smu_spring.controller;

import org.smu.capstone_smu_spring.dto.FastApiResponse;
import org.smu.capstone_smu_spring.service.FastApiClient;
import org.smu.capstone_smu_spring.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private FastApiClient fastApiClient;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    @Autowired
    private GptService gptService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        try {
            FastApiResponse result = fastApiClient.sendToFastApi(file);

            String category = result.getCategory();
            String guide = result.getGuide();

            logger.info("📥 FastAPI 응답 수신: category = {}, guide = {}", category, guide);

            Map<String, String> response = new HashMap<>();
            response.put("category", category != null ? category : "미분류");
            response.put("guide", guide != null ? guide : "정보 없음");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ FastAPI 통신 중 예외 발생", e);

            Map<String, String> error = new HashMap<>();
            error.put("error", "처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}