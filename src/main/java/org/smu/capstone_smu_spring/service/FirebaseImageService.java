package org.smu.capstone_smu_spring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseImageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseImageService.class);

    // ✅ Base64로 인코딩 후 Firestore 저장
    public void saveImageToFirestore(String nickname, MultipartFile file) {
        try {
            logger.info("📝 Firestore 저장 호출됨 - 닉네임: {}, 파일명: {}", nickname, file.getOriginalFilename());

            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            Firestore db = FirestoreClient.getFirestore();

            DocumentReference docRef = db.collection("users")
                    .document(nickname)
                    .collection("images")
                    .document(); // 자동 생성 문서 ID

            Map<String, Object> data = new HashMap<>();
            data.put("image", base64Image);
            data.put("timestamp", Timestamp.now());

            ApiFuture<WriteResult> result = docRef.set(data);
            logger.info("✅ Firestore 저장 성공 - 시간: {}", result.get().getUpdateTime());

        } catch (Exception e) {
            logger.error("❌ Firestore 저장 실패", e);
            throw new RuntimeException("Firestore 저장 실패", e);
        }
    }
}