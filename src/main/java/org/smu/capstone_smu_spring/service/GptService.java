package org.smu.capstone_smu_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateGuide(String category) {
        String prompt = category + " 쓰레기는 어떻게 배출해야 하나요? 2~3문장으로 설명해줘.";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions", request, Map.class
            );

            return ((Map)((Map)((java.util.List<?>) response.getBody().get("choices")).get(0)).get("message")).get("content").toString();

        } catch (Exception e) {
            return "배출 가이드를 생성하지 못했습니다. 일반 쓰레기로 분류해 주세요.";
        }
    }
}