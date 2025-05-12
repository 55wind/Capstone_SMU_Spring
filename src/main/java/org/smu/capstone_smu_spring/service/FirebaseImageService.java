package org.smu.capstone_smu_spring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseImageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseImageService.class);

    // ✅ Firebase Storage 업로드
    public String uploadImage(MultipartFile file) {
        try {
            logger.info("⏫ FirebaseImageService.uploadImage() 호출됨");

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream content = file.getInputStream();

            logger.info("📂 Firebase Storage 업로드 시작 - 파일명: {}, 크기: {} bytes", fileName, file.getSize());

            // Storage 업로드
            StorageClient.getInstance().bucket().create(fileName, content, file.getContentType());

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            String imageUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    StorageClient.getInstance().bucket().getName(),
                    encodedFileName);

            logger.info("✅ Firebase Storage 업로드 성공 - URL: {}", imageUrl);
            return imageUrl;

        } catch (Exception e) {
            logger.error("❌ Firebase Storage 업로드 실패", e);
            throw new RuntimeException("Firebase Storage 업로드 실패", e);
        }
    }

    // ✅ Firestore 저장
    public void saveImageToFirestore(String nickname, String imageUrl) {
        try {
            logger.info("📝 Firestore 저장 호출됨 - 닉네임: {}, 이미지 URL: {}", nickname, imageUrl);

            Firestore db = FirestoreClient.getFirestore();

            DocumentReference docRef = db.collection("users")
                    .document(nickname)
                    .collection("images")
                    .document(); // 자동 생성 문서 ID

            Map<String, Object> data = new HashMap<>();
            data.put("image", imageUrl);
            data.put("timestamp", Timestamp.now());

            ApiFuture<WriteResult> result = docRef.set(data);
            WriteResult writeResult = result.get(); // 블로킹 (옵션)
            logger.info("✅ Firestore 저장 성공 - 시간: {}", writeResult.getUpdateTime());

        } catch (Exception e) {
            logger.error("❌ Firestore 저장 실패", e);
            throw new RuntimeException("Firestore 저장 실패", e);
        }
    }
}