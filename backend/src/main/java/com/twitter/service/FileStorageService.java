package com.twitter.service;

import com.twitter.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public List<StoredImage> store(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        if (files.length > MAX_IMAGES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TOO_MANY_IMAGES",
                    "最多上传 " + MAX_IMAGES + " 张图片");
        }

        List<StoredImage> stored = new ArrayList<>();
        List<Path> written = new ArrayList<>();
        try {
            Files.createDirectories(uploadPath);
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "IMAGE_TOO_LARGE",
                            "单张图片不能超过 5MB");
                }
                byte[] bytes = file.getBytes();
                String mimeType = normalizeMime(file.getContentType());
                String extension = extensionFor(mimeType);
                if (extension == null || !looksLikeImage(bytes, extension)) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_IMAGE",
                            "仅支持 JPG、PNG、WebP、GIF 图片");
                }

                String filename = UUID.randomUUID().toString().replace("-", "") + extension;
                Path target = uploadPath.resolve(filename).normalize();
                if (!target.startsWith(uploadPath)) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_FILENAME", "文件名不合法");
                }
                Files.write(target, bytes);
                written.add(target);
                stored.add(new StoredImage("/uploads/" + filename,
                        imageWidth(bytes), imageHeight(bytes), mimeType, bytes.length));
            }
            return stored;
        } catch (IOException ex) {
            deleteFiles(written);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_FAILED", "图片保存失败");
        } catch (ApiException ex) {
            deleteFiles(written);
            throw ex;
        }
    }

    public void delete(List<StoredImage> images) {
        if (images == null) {
            return;
        }
        List<Path> paths = new ArrayList<>();
        for (StoredImage image : images) {
            if (image == null || image.getPath() == null || !image.getPath().startsWith("/uploads/")) {
                continue;
            }
            String filename = image.getPath().substring("/uploads/".length());
            Path target = uploadPath.resolve(filename).normalize();
            if (target.startsWith(uploadPath)) {
                paths.add(target);
            }
        }
        deleteFiles(paths);
    }

    public void deletePaths(List<String> imagePaths) {
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

    private String normalizeMime(String contentType) {
        if (contentType == null) {
            return null;
        }
        String value = contentType.toLowerCase(Locale.ROOT).trim();
        return "image/jpg".equals(value) ? "image/jpeg" : value;
    }

    private String extensionFor(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        switch (mimeType) {
            case "image/jpeg":
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

    private boolean looksLikeImage(byte[] bytes, String extension) {
        if (bytes == null || bytes.length < 12) {
            return false;
        }
        switch (extension) {
            case ".jpg":
                return (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8
                        && (bytes[2] & 0xFF) == 0xFF;
            case ".png":
                return (bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E
                        && bytes[3] == 0x47 && bytes[4] == 0x0D && bytes[5] == 0x0A
                        && bytes[6] == 0x1A && bytes[7] == 0x0A;
            case ".gif":
                return bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x38;
            case ".webp":
                return bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                        && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
            default:
                return false;
        }
    }

    private Integer imageWidth(byte[] bytes) {
        BufferedImage image = readImage(bytes);
        return image == null ? null : image.getWidth();
    }

    private Integer imageHeight(byte[] bytes) {
        BufferedImage image = readImage(bytes);
        return image == null ? null : image.getHeight();
    }

    private BufferedImage readImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            return null;
        }
    }
}
