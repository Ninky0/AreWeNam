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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
