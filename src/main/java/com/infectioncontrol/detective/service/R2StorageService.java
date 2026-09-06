package com.infectioncontrol.detective.service;

import com.infectioncontrol.detective.config.AppProperties;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "r2")
public class R2StorageService implements StorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "svg");

    private final S3Client s3Client;
    private final String bucket;
    private final String publicBaseUrl;

    public R2StorageService(AppProperties appProperties) {
        AppProperties.R2 r2 = appProperties.getStorage().getR2();
        this.bucket = requireValue(r2.getBucket(), "R2_BUCKET");
        this.publicBaseUrl = normalizeBaseUrl(requireValue(r2.getPublicBaseUrl(), "R2_PUBLIC_BASE_URL"));
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(requireValue(r2.getEndpoint(), "R2_ENDPOINT")))
                .region(Region.of(defaultValue(r2.getRegion(), "auto")))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        requireValue(r2.getAccessKeyId(), "R2_ACCESS_KEY_ID"),
                        requireValue(r2.getSecretAccessKey(), "R2_SECRET_ACCESS_KEY")
                )))
                .build();
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일을 업로드해주세요.");
        }
        String contentType = defaultValue(file.getContentType(), "application/octet-stream");
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String extension = extensionOf(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 확장자입니다.");
        }

        String key = "uploads/" + UUID.randomUUID() + "." + extension;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength(file.getSize())
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return resolvePublicUrl(key);
        } catch (IOException | S3Exception e) {
            throw new IllegalStateException("이미지를 저장하지 못했습니다.", e);
        }
    }

    @Override
    public String resolvePublicUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isBlank() || isAbsoluteUrl(storedUrl)) {
            return storedUrl;
        }
        String key = storedUrl.startsWith("/") ? storedUrl.substring(1) : storedUrl;
        return publicBaseUrl + "/" + key;
    }

    private String extensionOf(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "png";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String requireValue(String value, String envName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(envName + " 환경 변수를 설정해주세요.");
        }
        return value.trim();
    }

    private String defaultValue(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private String normalizeBaseUrl(String baseUrl) {
        String trimmed = baseUrl.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private boolean isAbsoluteUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
