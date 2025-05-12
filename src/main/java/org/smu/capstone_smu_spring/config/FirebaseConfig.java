package org.smu.capstone_smu_spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
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
            // ✅ 환경변수에서 값 읽기
            String bucket = System.getenv("S3_BUCKET_NAME");
            String key = System.getenv("FIREBASE_KEY_OBJECT_NAME");
            String region = System.getenv("AWS_REGION");

            String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
            String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

            // 유효성 확인
            if (bucket == null || key == null || region == null || accessKey == null || secretKey == null) {
                throw new IllegalArgumentException("필수 환경변수(S3_BUCKET_NAME, FIREBASE_KEY_OBJECT_NAME, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)가 누락되었습니다.");
            }

            // ✅ AWS 자격 증명 객체 생성
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

            // ✅ S3Client 생성
            S3Client s3 = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();

            // ✅ S3에서 Firebase 키 다운로드
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseBytes<?> objectBytes = s3.getObjectAsBytes(request);
            ByteArrayInputStream credentialsStream = new ByteArrayInputStream(objectBytes.asByteArray());

            // ✅ Firebase 초기화
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .setStorageBucket("your-bucket-name.appspot.com") // ✅ 여기에 버킷 이름 추가!
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase (Cloud Firestore) 초기화 완료");
            }

        } catch (Exception e) {
            System.err.println("❌ Firebase 초기화 실패");
            e.printStackTrace();
        }
    }
}
