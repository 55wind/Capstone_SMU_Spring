package org.smu.capstone_smu_spring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
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

    // ✅ Firebase Storage 업로드
    public String uploadImage(MultipartFile file) {
        try {
            System.out.println("⏫ FirebaseImageService.uploadImage() 호출됨");

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream content = file.getInputStream();

            System.out.println("📂 Firebase Storage 업로드 시작 - 파일명: " + fileName + ", 크기: " + file.getSize() + " bytes");

            // Storage 업로드
            StorageClient.getInstance().bucket().create(fileName, content, file.getContentType());

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            String imageUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    StorageClient.getInstance().bucket().getName(),
                    encodedFileName);

            System.out.println("✅ Firebase Storage 업로드 성공 - URL: " + imageUrl);
            return imageUrl;

        } catch (Exception e) {
            System.err.println("❌ Firebase Storage 업로드 실패:");
            e.printStackTrace();
            throw new RuntimeException("Firebase Storage 업로드 실패", e);
        }
    }

    // ✅ Firestore 저장
    public void saveImageToFirestore(String nickname, String imageUrl) {
        try {
            System.out.println("📝 Firestore 저장 호출됨 - 닉네임: " + nickname + ", 이미지 URL: " + imageUrl);

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
            System.out.println("✅ Firestore 저장 성공 - 시간: " + writeResult.getUpdateTime());

        } catch (Exception e) {
            System.err.println("❌ Firestore 저장 실패:");
            e.printStackTrace();
            throw new RuntimeException("Firestore 저장 실패", e);
        }
    }
}