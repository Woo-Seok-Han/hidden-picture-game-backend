package com.infectioncontrol.detective.service;

import com.infectioncontrol.detective.config.AppProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService implements StorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "svg");

    private final Path uploadDir;
    private final String publicBaseUrl;

    public FileStorageService(AppProperties appProperties) {
        this.uploadDir = appProperties.getUploadDir().toAbsolutePath().normalize();
        this.publicBaseUrl = normalizeBaseUrl(appProperties.getPublicBaseUrl());
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일을 업로드해주세요.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String extension = extensionOf(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 확장자입니다.");
        }

        try {
            Files.createDirectories(uploadDir);
            String storedFilename = UUID.randomUUID() + "." + extension;
            Path target = uploadDir.resolve(storedFilename).normalize();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + storedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("이미지를 저장하지 못했습니다.", e);
        }
    }

    @Override
    public String resolvePublicUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isBlank() || isAbsoluteUrl(storedUrl) || publicBaseUrl.isBlank()) {
            return storedUrl;
        }
        return publicBaseUrl + (storedUrl.startsWith("/") ? storedUrl : "/" + storedUrl);
    }

    private String extensionOf(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "png";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }
        String trimmed = baseUrl.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    private boolean isAbsoluteUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
