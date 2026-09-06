package com.infectioncontrol.detective.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String storeImage(MultipartFile file);

    String resolvePublicUrl(String storedUrl);
}
