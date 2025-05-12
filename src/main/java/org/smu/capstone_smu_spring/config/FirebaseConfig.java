package org.smu.capstone_smu_spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.ByteArrayInputStream;

@Service
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            String bucket = System.getenv("my-capstone-keys");       // e.g., "my-capstone-keys"
            String key = System.getenv("serviceAccountKey.json");             // e.g., "serviceAccountKey.json"
            String region = System.getenv("ap-southeast-2");           // e.g., "ap-southeast-2"

            // 1. S3에서 serviceAccountKey.json 다운로드
            S3Client s3 = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build();

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseBytes<?> objectBytes = s3.getObjectAsBytes(request);
            ByteArrayInputStream credentialsStream = new ByteArrayInputStream(objectBytes.asByteArray());

            // 2. Firebase App 초기화 (Firestore용)
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();  // Firestore는 Database URL 필요 없음

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase (Cloud Firestore) initialized.");
            }

        } catch (Exception e) {
            System.err.println("❌ Firebase initialization failed.");
            e.printStackTrace();
        }
    }
}