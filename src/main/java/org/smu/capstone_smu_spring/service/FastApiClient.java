package org.smu.capstone_smu_spring.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

@Service
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://13.238.218.99:8000/predict";

    public String sendToFastApiRaw(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);  // ✅ key 이름은 FastAPI에서 file= 로 받기 때문

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                fastApiUrl,
                requestEntity,
                String.class
        );

        return response.getBody();  // raw JSON 문자열 반환
    }
}