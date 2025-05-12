package org.smu.capstone_smu_spring.controller;

import org.smu.capstone_smu_spring.service.FirebaseImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private FirebaseImageService firebaseImageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nickname") String nickname) {

        System.out.println("📥 [UPLOAD] /api/upload 요청 수신됨");
        System.out.println("👤 nickname: " + nickname);
        System.out.println("🖼️ file: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)");

        try {
            firebaseImageService.saveImageToFirestore(nickname, file);
            return ResponseEntity.ok("✅ Firestore 저장 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Firestore 저장 실패: " + e.getMessage());
        }
    }
}