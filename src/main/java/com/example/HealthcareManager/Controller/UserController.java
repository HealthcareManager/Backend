package com.example.HealthcareManager.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Service.UserService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/upload-image/{id}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @PathVariable(value = "id") String id) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("請選擇一個檔案來上傳。");
        }

        final long MAX_SIZE = 10 * 1024 * 1024;
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest().body("圖片大小超過10MB限制");
        }

        try {
            String uploadDir = new File(System.getProperty("user.dir"))
                    .getAbsolutePath() + File.separator + "src"
                    + File.separator + "main" + File.separator + "assets"
                    + File.separator + "images"; // 確保該路徑正確

            // 查詢資料庫以獲取原有的圖片路徑
            User user = userService.getUserById(id);
            String url = user.getImagelink();
            deleteExistingImage(url, uploadDir);

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // 更新為正確的基本 URL
            String baseUrl = "http://10.0.2.2:8080/"; // 確保這裡的端口和地址正確
            String relativeImagePath = "images/" + fileName;
            String fullImageUrl = baseUrl + relativeImagePath;

            userService.saveImagePath(id, fullImageUrl);

            String jsonResponse = String.format("{\"filePath\":\"%s\"}", fullImageUrl);
            return ResponseEntity.ok().body(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上傳檔案時發生錯誤。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理請求時發生錯誤。");
        }
    }

    private void deleteExistingImage(String url, String uploadDir) {
        if (url != null && !url.isEmpty()) {
            try {
                URL urlObj = new URL(url);
                String path = urlObj.getPath();
                String relativePath = path.replace("/images", "");
                String localFilePath = uploadDir + relativePath.replace('/', File.separatorChar);

                Path existingFilePath = Paths.get(localFilePath);
                // 檢查文件是否存在
                if (Files.exists(existingFilePath)) {
                    Files.delete(existingFilePath);
                    System.out.println("成功刪除原有圖片：" + existingFilePath);
                } else {
                    System.out.println("原有圖片不存在：" + existingFilePath);
                }
            } catch (MalformedURLException e) {
                System.err.println("圖片 URL 格式錯誤：" + e.getMessage());
            } catch (IOException e) {
                System.err.println("刪除原有圖片時出錯：" + e.getMessage());
            } catch (SecurityException e) {
                System.err.println("無法刪除圖片，權限不足：" + e.getMessage());
            }
        }
    }
}
