package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdUploadRequestDTO;
import org.example.shoppingweather.dto.product.ProdUploadResponseDTO;
import org.example.shoppingweather.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ProdUploadResponseDTO> uploadProduct(@ModelAttribute ProdUploadRequestDTO dto) {
        // 상품 정보 로깅
        System.out.println("상품명 : " + dto.getName() + '\n'
                + "가격 : " + dto.getPrice() + '\n'
                + "수량 : " + dto.getQuantity() + '\n'
                + "카테고리 : " + dto.getCategory() + '\n'
                + "시즌 : " + dto.getSeason() + '\n'
                + "온도 : " + dto.getTemperature() + '\n'
                + "설명 : " + dto.getDescription() + '\n');

        MultipartFile mainPicture = dto.getMainPicture();
        String mainPicturePath = null;

        // 설명 텍스트에서 절대 경로를 상대 경로로 변경
        String description = dto.getDescription();
        String formatDescription = description.replace("http://localhost:8080/uploads/", "/uploads/");
        dto.setDescription(formatDescription);

        System.out.println("메인 이미지: " + (mainPicture != null ? mainPicture.getOriginalFilename() : "없음"));

        try {
            if (mainPicture != null && !mainPicture.isEmpty()) {
                String fileNameExtension = mainPicture.getOriginalFilename().substring(mainPicture.getOriginalFilename().lastIndexOf("."));
                String standardizedFileName = "main_" + System.currentTimeMillis() + fileNameExtension;
                Path savePath = Paths.get("src/main/resources/static/uploads/", standardizedFileName);
                Files.createDirectories(savePath.getParent());

                int counter = 1;
                while (Files.exists(savePath)) {
                    standardizedFileName = "main_" + System.currentTimeMillis() + "_" + counter + fileNameExtension;
                    savePath = Paths.get("src/main/resources/static/uploads/", standardizedFileName);
                    counter++;
                }

                Files.copy(mainPicture.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
                mainPicturePath = "/uploads/" + standardizedFileName;
                dto.setMainPicturePath(mainPicturePath);

                System.out.println("저장된 메인 이미지 경로: " + mainPicturePath);
            }

            adminService.save(dto);

            return ResponseEntity.ok(
                    ProdUploadResponseDTO.builder()
                            .url("/admin/product/list")
                            .build()
            );

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    ProdUploadResponseDTO.builder()
                            .url("/admin/product/upload")
                            .build()
            );
        }
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filenameExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String standardizedFilename = "detail_" + System.currentTimeMillis() + filenameExtension;

            Path filePath = Paths.get("src/main/resources/static/uploads/", standardizedFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + standardizedFilename;
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/update/{id}")  // 수정된 경로
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @ModelAttribute ProdUploadRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminService.updateProduct(id, dto);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteProducts(@RequestBody List<Long> productIds) {
        try {
            adminService.deleteProductsByIds(productIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "상품이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "상품 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
    }

}