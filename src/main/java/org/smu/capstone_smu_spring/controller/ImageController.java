package org.smu.capstone_smu_spring.controller;

import org.smu.capstone_smu_spring.service.FirebaseImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private FirebaseImageService firebaseImageService;

    @PostMapping("/upload")
    public String uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nickname") String nickname) {

        System.out.println("📥 [UPLOAD] /api/upload 요청 수신됨");
        System.out.println("👤 nickname: " + nickname);
        System.out.println("🖼️ file: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");

        try {
            // 이미지 업로드 → URL 생성
            String imageUrl = firebaseImageService.uploadImage(file);
            System.out.println("✅ Firebase 업로드 성공: " + imageUrl);

            // Firestore 저장
            firebaseImageService.saveImageToFirestore(nickname, imageUrl);
            System.out.println("✅ Firestore 저장 완료 for nickname: " + nickname);

            return imageUrl;

        } catch (Exception e) {
            System.err.println("❌ 업로드 처리 중 예외 발생:");
            e.printStackTrace();
            return "업로드 실패: " + e.getMessage();
        }
    }
}