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
        String prompt = category + " 쓰레기 배출 방법에 대해 설명해줘. 예시를 줄테니깐 이런식으로 작성하면 돼. 버리는 법 앞에는 쓰레기 분류에 맞는 아이콘도 넣어줘. 예시 : \uD83D\uDCC4 종이류 분리수거 방법\\n\\n• 이물질 제거: 종이에 붙은 스티커, 테이프, 플라스틱 코팅, 철심 등을 제거합니다.\\n\\n• 오염된 종이: 기름·음식물이 묻은 종이는 일반 쓰레기로 버립니다.\\n\\n• 분리배출: 신문지, 책, 노트, 복사용지 등은 종류별로 묶어 분리합니다.\\n\\n• 종이팩: 우유팩, 주스팩 등은 일반 종이와 달리 따로 모아서 헹군 후 잘 말려 배출합니다.\\n\\n• 종이박스: 테이프, 송장 제거 후 평평하게 접어 배출합니다.";

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