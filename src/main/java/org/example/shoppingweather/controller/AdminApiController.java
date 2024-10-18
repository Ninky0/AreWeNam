package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ProdUploadRequestDTO;
import org.example.shoppingweather.dto.ProdUploadResponseDTO;
import org.example.shoppingweather.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/product/upload")
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
        System.out.println("메인 이미지: " + (mainPicture != null ? mainPicture.getOriginalFilename() : "없음"));

        String mainPicturePath = null;

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
}
