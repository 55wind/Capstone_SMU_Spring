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
            String bucket = System.getenv("S3_BUCKET_NAME");              // 예: "my-capstone-keys"
            String key = System.getenv("FIREBASE_KEY_OBJECT_NAME");      // 예: "serviceAccountKey.json"
            String region = System.getenv("AWS_REGION");                 // 예: "ap-southeast-2"

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

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();

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
