package org.smu.capstone_smu_spring.controller;

import org.smu.capstone_smu_spring.dto.FastApiResponse;
import org.smu.capstone_smu_spring.service.FastApiClient;
import org.smu.capstone_smu_spring.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private FastApiClient fastApiClient;

    @Autowired
    private GptService gptService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nickname") String nickname) {

        try {
            FastApiResponse result = fastApiClient.sendToFastApi(nickname, file);
            String category = result.getResult();
            String guide = gptService.generateGuide(category);

            return ResponseEntity.ok(Map.of(
                    "nickname", nickname,
                    "category", category,
                    "guide", guide
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "처리 중 오류 발생: " + e.getMessage()));
        }
    }
}