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
            @RequestParam("name") String name,
            @RequestParam("price") Integer price,
            @RequestParam("quantity") String quantity,
            @RequestParam("category") String category,
            @RequestParam("season") Integer season,
            @RequestParam("temperature") Integer temperature,
            @RequestParam("description") String description,
            @RequestParam(value = "detailPicture", required = false) List<MultipartFile> detailPictures,
            @RequestParam(value = "mainPicture", required = false) MultipartFile mainPicture) {

        System.out.println("상품명: " + name);
        System.out.println("가격: " + price);
        System.out.println("수량: " + quantity);
        System.out.println("카테고리: " + category);
        System.out.println("시즌: " + season);
        System.out.println("온도: " + temperature);
        System.out.println("설명: " + description);
        System.out.println("메인 이미지: " + (mainPicture != null ? mainPicture.getOriginalFilename() : "없음"));
        System.out.println("디테일 이미지 수: " + (detailPictures != null ? detailPictures.size() : 0));

        List<String> detailPicturePaths = new ArrayList<>();
        String mainPicturePath = null;

        try {
            // Process detail pictures
            if (detailPictures != null) {
                for (MultipartFile detailPicture : detailPictures) {
                    if (!detailPicture.isEmpty()) {
                        String path = adminService.saveFile(detailPicture); // Save file method
                        detailPicturePaths.add(path);
                    }
                }
            }

            // Process main picture
            if (mainPicture != null && !mainPicture.isEmpty()) {
                mainPicturePath = adminService.saveFile(mainPicture); // Save file method
            }

            // Create request DTO
            ProdUploadRequestDTO requestDTO = ProdUploadRequestDTO.builder()
                    .name(name)
                    .price(price)
                    .quantity(quantity)
                    .category(category)
                    .season(season)
                    .temperature(temperature)
                    .description(description) // Use description for Quill content
                    .mainPicture(mainPicture) // Pass the MultipartFile
                    .detailPictures(detailPictures) // Pass the List<MultipartFile>
                    .build();

            // Save product information
            adminService.save(requestDTO);

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
