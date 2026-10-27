package com.twitter.service;

import com.twitter.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final int MAX_IMAGES = 4;
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public List<String> store(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        if (files.length > MAX_IMAGES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TOO_MANY_IMAGES", "最多上传 " + MAX_IMAGES + " 张图片");
        }

        List<String> paths = new ArrayList<>();
        List<Path> written = new ArrayList<>();
        try {
            Files.createDirectories(uploadPath);
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "IMAGE_TOO_LARGE", "单张图片不能超过 5MB");
                }
                String extension = extensionFor(file.getContentType());
                if (extension == null) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_IMAGE", "仅支持 JPG、PNG、WebP、GIF 图片");
                }
                String filename = UUID.randomUUID().toString().replace("-", "") + extension;
                Path target = uploadPath.resolve(filename).normalize();
                if (!target.startsWith(uploadPath)) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_FILENAME", "文件名不合法");
                }
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                written.add(target);
                paths.add("/uploads/" + filename);
            }
            return paths;
        } catch (IOException ex) {
            deleteFiles(written);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_FAILED", "图片保存失败");
        } catch (ApiException ex) {
            deleteFiles(written);
            throw ex;
        }
    }

    public void delete(List<String> imagePaths) {
        if (imagePaths == null) {
            return;
        }
        List<Path> paths = new ArrayList<>();
        for (String imagePath : imagePaths) {
            if (imagePath == null || !imagePath.startsWith("/uploads/")) {
                continue;
            }
            String filename = imagePath.substring("/uploads/".length());
            Path target = uploadPath.resolve(filename).normalize();
            if (target.startsWith(uploadPath)) {
                paths.add(target);
            }
        }
        deleteFiles(paths);
    }

    private void deleteFiles(List<Path> paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
                // Best effort: a leftover file is preferable to hiding the original error.
            }
        }
    }

    private String extensionFor(String contentType) {
        if (contentType == null) {
            return null;
        }
        switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/webp":
                return ".webp";
            case "image/gif":
                return ".gif";
            default:
                return null;
        }
    }
}
