package com.zalmuk.swwim.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 파일 업로드 서비스
 */
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket:swwim-storage-5273}")
    private String bucketName;

    @Value("${aws.region:ap-northeast-2}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param folder 저장할 폴더 (예: "profiles", "posts")
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 고유 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String key = folder + "/" + UUID.randomUUID() + extension;

            // S3에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            // 공개 URL 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 프로필 이미지 업로드
     */
    public String uploadProfileImage(MultipartFile file, String userId) {
        return uploadFile(file, "profiles/" + userId);
    }

    /**
     * 게시글 이미지 업로드
     */
    public String uploadPostImage(MultipartFile file, String postId) {
        return uploadFile(file, "posts/" + postId);
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 key 추출
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            // 삭제 실패해도 무시 (파일이 이미 없을 수 있음)
        }
    }
}
