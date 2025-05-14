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
    private final String fastApiUrl = "http://13.238.218.99:8000/predict"; // <-- EC2 주소로 바꾸기

    public FastApiResponse sendToFastApi(String nickname, MultipartFile file) throws IOException {
        // ✅ nickname null 또는 빈 문자열 체크
        if (nickname == null || nickname.trim().isEmpty()) {
            System.err.println("❌ [FastApiClient] nickname이 null이거나 비어 있습니다.");
            throw new IllegalArgumentException("nickname 값이 유효하지 않습니다.");
        }

        System.out.println("📤 [FastApiClient] nickname: " + nickname + ", file: " + file.getOriginalFilename());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        body.add("nickname", nickname);  // 정상 nickname 전송

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