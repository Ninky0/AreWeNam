package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.ootd.OotdWriteRequestDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.OotdService;
import org.example.shoppingweather.service.ProductService;
import org.example.shoppingweather.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ootd")
public class OotdApiController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final OotdService ootdService;

    @PostMapping("/write")
    public ResponseEntity<Map<String, String>> saveOotdPost(
            @ModelAttribute OotdWriteRequestDTO requestDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            String picturePath = reviewService.handleFileUpload(requestDTO.getPicture());
            ootdService.saveOotdPost(requestDTO, picturePath); // Here, we save Product directly

            response.put("url", "/ootd/list");
            response.put("message", "상품 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // ootd리스트에서 클릭하면 열리는 모달창안의 이미지
    @GetMapping("/images")
    public ResponseEntity<Map<String, Object>> getOotdModalImage(@RequestParam int offset, @RequestParam int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit , Sort.by("date").descending());
        Page<CustomerOotdImageResponseDTO> ootdImages = ootdService.getOotdModalImage(pageable);

        // OOTD 이미지의 id 필드가 존재하는지 확인
        ootdImages.forEach(image -> {
            System.out.println("Image ID: " + image.getId());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("images", ootdImages.getContent());
        response.put("totalElements", ootdImages.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // ootd등록페이지에서 상품검색시 보여지는 모달창 안의 상세페이지내용
    @GetMapping("/detail/product/{id}")
    @ResponseBody
    public ResponseEntity<ProdReadResponseDTO> getOotdProductDetail(@PathVariable Long id) {
        ProdReadResponseDTO product = productService.findById(id); // ProductService를 사용하여 상품 정보를 조회

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 상품이 없는 경우 404 응답 반환
        }

        if (product.getMainPicture() != null) {
            String mainPicturePath = product.getMainPicture().replace("\\", "/");
            product.setMainPicture(mainPicturePath); // 경로 수정 후 다시 설정
        }

        return ResponseEntity.ok(product); // 상품 정보 반환
    }

    // ootd리스트에서 클릭하면 열리는 모달창안의 상세페이지
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOotdDetail(@PathVariable Long id) {
        // Retrieve OOTD entry by ID
        CustomerOotdImageResponseDTO ootdDetail = ootdService.findOotdById(id);

        if (ootdDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }

        // If there's a linked productId, retrieve the product details
        ProdReadResponseDTO product = null;
        if (ootdDetail.getProductId() != null) {
            product = productService.findById(ootdDetail.getProductId());
        }

        // Modify the picture path for OOTD
        if (ootdDetail.getPicture() != null) {
            String picturePath = ootdDetail.getPicture().replace("\\", "/");
            ootdDetail.setPicture(picturePath); // Correct the path format
        }

        // Prepare response data
        Map<String, Object> response = new HashMap<>();
        response.put("ootd", ootdDetail);
        response.put("product", product); // This can be null if not linked

        return ResponseEntity.ok(response);
    }

}
