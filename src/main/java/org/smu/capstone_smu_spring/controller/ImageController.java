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

        // 이미지 업로드 → URL 생성
        String imageUrl = firebaseImageService.uploadImage(file);

        // Firestore 저장
        firebaseImageService.saveImageToFirestore(nickname, imageUrl);

        return imageUrl;
    }
}