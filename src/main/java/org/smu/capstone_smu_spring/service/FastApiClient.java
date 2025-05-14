package org.smu.capstone_smu_spring.service;

import org.smu.capstone_smu_spring.dto.FastApiResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://13.238.218.99:8000/predict";

    public FastApiResponse sendToFastApi(MultipartFile file) throws IOException {
        System.out.println("📤 [FastApiClient] file: " + file.getOriginalFilename());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<FastApiResponse> response = restTemplate.postForEntity(
                fastApiUrl, request, FastApiResponse.class
        );

        return response.getBody();
    }

    // 내부 클래스
    private static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }
    }
}