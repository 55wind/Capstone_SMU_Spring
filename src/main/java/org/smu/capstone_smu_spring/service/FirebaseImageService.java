package org.smu.capstone_smu_spring.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseImageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseImageService.class);

    public void saveImageToFirestore(String nickname, MultipartFile file) {
        try {
            logger.info("📝 이미지 압축 후 Firestore 저장 - 닉네임: {}, 원본 파일명: {}", nickname, file.getOriginalFilename());

            // ✅ Firestore 인스턴스
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference imagesRef = db.collection("users").document(nickname).collection("images");

            // ✅ 1. 기존 문서 삭제 (모든 이미지 삭제)
            ApiFuture<QuerySnapshot> query = imagesRef.get();
            for (DocumentSnapshot doc : query.get().getDocuments()) {
                doc.getReference().delete();
                logger.info("🗑️ 기존 이미지 삭제됨: {}", doc.getId());
            }

            // ✅ 2. 이미지 리사이징
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int targetWidth = 640;
            int targetHeight = 480;

            Image resized = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(resized, 0, 0, null);
            g2d.dispose();

            // ✅ 3. JPEG 압축 + Base64 인코딩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "jpg", baos);
            byte[] compressedBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(compressedBytes);

            // ✅ 4. 새 이미지 저장 (문서 ID는 고정적으로 'latest' 사용해도 무방)
            DocumentReference docRef = imagesRef.document("latest");

            Map<String, Object> data = new HashMap<>();
            data.put("image", base64Image);
            data.put("timestamp", Timestamp.now());

            ApiFuture<WriteResult> result = docRef.set(data);
            logger.info("✅ Firestore 저장 완료 - 시간: {}", result.get().getUpdateTime());

        } catch (Exception e) {
            logger.error("❌ Firestore 저장 실패", e);
            throw new RuntimeException("Firestore 저장 실패", e);
        }
    }
}