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

    // ✅ 이미지 업로드 후 Firebase Storage URL 반환
    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream content = file.getInputStream();

            // Firebase Storage에 업로드
            StorageClient.getInstance().bucket().create(fileName, content, file.getContentType());

            // Firebase Storage 다운로드 URL 생성
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            String imageUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    StorageClient.getInstance().bucket().getName(),
                    encodedFileName);

            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Cloud Firestore에 사용자별 이미지 URL 저장
    public void saveImageToFirestore(String nickname, String imageUrl) {
        try {
            Firestore db = FirestoreClient.getFirestore(); // ← 권장 방식

            DocumentReference docRef = db.collection("users")
                    .document(nickname)
                    .collection("images")
                    .document(); // 자동 생성 문서 ID

            Map<String, Object> data = new HashMap<>();
            data.put("image", imageUrl);
            data.put("timestamp", Timestamp.now());

            ApiFuture<WriteResult> result = docRef.set(data);
            result.get(); // optional: blocking, wait for write result

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
