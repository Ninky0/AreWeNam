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
    public ResponseEntity<ProdUploadResponseDTO> uploadProduct(
            @ModelAttribute ProdUploadRequestDTO dto) {

        System.out.println("상품명 : " + dto.getName() + '\n'
                + "가격 : " + dto.getPrice() + '\n'
                + "수량 : " + dto.getQuantity() + '\n'
                + "카테고리 : " + dto.getCategory() + '\n'
                + "시즌 : " + dto.getSeason() + '\n'
                + "온도 : " + dto.getTemperature() + '\n'
                + "설명 : " + dto.getDescription() + '\n'
        );

        MultipartFile mainPicture = dto.getMainPicture();

        String description = dto.getDescription();
        System.out.println(description);
        String formatDescription = description.replace("src/main/resources/static/uploads/","");
        System.out.println(formatDescription);

        //설명에 있는 사진 태그에 대해서만 사진 이름을 변경해야됨

        System.out.println("메인 이미지: " + (mainPicture != null ? mainPicture.getOriginalFilename() : "없음"));

        String mainPicturePath = null;
        String descriptionPath = null;

        try {
            // Process main picture
            if (mainPicture != null && !mainPicture.isEmpty()) {
                mainPicturePath = adminService.saveFile(mainPicture); // Save file method
            }

            // Save product information
            adminService.save(dto);

            // Return response with redirection URL
            return ResponseEntity.ok(
                    ProdUploadResponseDTO.builder()
                            .url("/admin/product/list") // Redirect URL to admin product list
                            .build()
            );

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    ProdUploadResponseDTO.builder()
                            .url("/admin/product/upload") // Error handling URL
                            .build()
            );
        }
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String detailPicturePath = null;

        try {
            // Unique name generation using timestamp
            String filenameExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            String standardizedFilename = "image_" + System.currentTimeMillis() + filenameExtension;
            System.out.println(standardizedFilename);

            Path filePath = Paths.get("src/main/resources/static/uploads/", standardizedFilename);
            System.out.println(filePath);


            detailPicturePath = adminService.saveDetail(file,standardizedFilename); // Save file method

            System.out.println(detailPicturePath);


            // Construct the URL that will be used in the Quill editor
            String imageUrl = "src/main/resources/static/uploads/" + standardizedFilename;
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }




    }
//
//    @PostMapping("/uploadImage")
//    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            String standardizedFilename = "image_" + System.currentTimeMillis() + ".png"; // Unique name
//            Path filePath = Paths.get("path/to/save/location", standardizedFilename);
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//            // Construct the URL that will be used in the Quill editor
//            String imageUrl = "/" + standardizedFilename;
//            Map<String, String> response = new HashMap<>();
//            response.put("imageUrl", imageUrl);
//            return ResponseEntity.ok(response);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }


    @PostMapping("/admin/product/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @ModelAttribute ProdUploadRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 상품 정보 업데이트
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
    //상품 상세페이지에서 넘겨줄때도 매개변수 타입을 리스트로 넘겨주면 좋겠음
    public ResponseEntity<?> deleteProducts(@RequestBody List<Long> productIds) {
        // productIds 잘 넘어오나 확인용
//        for (Long productId : productIds) {
//            System.out.println(productId);
//        }
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
