package org.smu.capstone_smu_spring.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;

@Service
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://13.238.218.99:8000/predict";

    public String sendToFastApiRaw(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("file", resource);

        HttpEntity<LinkedHashMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                fastApiUrl, request, String.class
        );

        return response.getBody(); // JSON 문자열 그대로 리턴
    }
}