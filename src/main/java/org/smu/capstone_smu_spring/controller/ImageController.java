package org.smu.capstone_smu_spring.controller;

import org.smu.capstone_smu_spring.service.FastApiClient;
import org.smu.capstone_smu_spring.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private FastApiClient fastApiClient;

    @Autowired
    private GptService gptService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        try {
            // JSON 문자열 그대로 받기
            String fastApiJson = fastApiClient.sendToFastApiRaw(file);

            // 문자열 → JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(fastApiJson);
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "미분류";
            String guide = jsonNode.has("guide") ? jsonNode.get("guide").asText() : "정보 없음";

            // GPT 활용
            String gptGuide = gptService.generateGuide(category);

            Map<String, String> response = new HashMap<>();
            response.put("category", category);
            response.put("guide", gptGuide + " (정확도: " + guide + ")");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}