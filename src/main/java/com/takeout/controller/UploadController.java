package com.takeout.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return error(HttpStatus.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            return error(HttpStatus.PAYLOAD_TOO_LARGE, "图片不能超过 5MB");
        }

        String ext = resolveExtension(file.getContentType(), file.getOriginalFilename());
        if (ext == null) {
            return error(HttpStatus.BAD_REQUEST, "仅支持 jpg/png/webp/gif 图片");
        }

        String uploadPath = request.getServletContext().getRealPath("/" + UPLOAD_DIR);
        if (uploadPath == null) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "无法定位上传目录");
        }

        File dir = new File(uploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建上传目录失败: {}", dir.getAbsolutePath());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建上传目录");
        }

        String filename = "dish_" + System.currentTimeMillis() + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ext;
        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("保存上传图片失败", e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "图片保存失败");
        }

        String url = buildPublicUrl(request, filename);
        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("filename", filename);
        result.put("size", file.getSize());
        return ResponseEntity.ok(result);
    }

    private String resolveExtension(String contentType, String originalFilename) {
        String lowerType = contentType == null ? "" : contentType.toLowerCase();
        if ("image/jpeg".equals(lowerType) || "image/jpg".equals(lowerType)) {
            return ".jpg";
        }
        if ("image/png".equals(lowerType)) {
            return ".png";
        }
        if ("image/webp".equals(lowerType)) {
            return ".webp";
        }
        if ("image/gif".equals(lowerType)) {
            return ".gif";
        }
        String lowerName = originalFilename == null ? "" : originalFilename.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return ".jpg";
        if (lowerName.endsWith(".png")) return ".png";
        if (lowerName.endsWith(".webp")) return ".webp";
        if (lowerName.endsWith(".gif")) return ".gif";
        return null;
    }

    private String buildPublicUrl(HttpServletRequest request, String filename) {
        String contextPath = request.getContextPath();
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return base + contextPath + "/" + UPLOAD_DIR + "/" + filename;
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("code", status.value());
        return ResponseEntity.status(status).body(body);
    }
}
