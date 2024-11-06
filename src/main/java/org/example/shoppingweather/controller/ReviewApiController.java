package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerReviewResponseDTO;
import org.example.shoppingweather.dto.ReviewWriteRequestDTO;
import org.example.shoppingweather.entity.Review;
import org.example.shoppingweather.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewApiController {
    private final ReviewService reviewService;

    @GetMapping("/list/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        long startTime = System.currentTimeMillis(); // 시작 시간 기록

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<CustomerReviewResponseDTO> reviewPage = reviewService.findReviewsByProductId(productId, pageable);

        int totalPages = reviewPage.getTotalPages();
        int pageBlock = 10;
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);
        response.put("showPrevious", startPage > 0);
        response.put("showNext", endPage < totalPages - 1);

        long endTime = System.currentTimeMillis(); // 종료 시간 기록
        long loadTime = endTime - startTime; // 로드 시간 계산

        System.out.println("Page load time: " + loadTime + " ms"); // 로드 시간 출력

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/{purchaseId}")
    public ResponseEntity<Map<String, String>> writeReview(
            @PathVariable Long productId,
            @PathVariable Long purchaseId,
            @ModelAttribute ReviewWriteRequestDTO requestDTO) {
        System.out.println(requestDTO.getCustomerId()+" "+requestDTO.getProductId()+" "+requestDTO.getContent()+" "+requestDTO.getPicture());

        Map<String, String> response = new HashMap<>();

        try {
            String picturePath = reviewService.handleFileUpload(requestDTO.getPicture());
            System.out.println(picturePath+"변환됨~!");
            reviewService.saveReview(requestDTO, picturePath);

            response.put("url", "/mypage/history");
            response.put("message", "리뷰 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CustomerReviewResponseDTO>> getReviewsByCustomerId(
            @RequestParam("customerId") Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CustomerReviewResponseDTO> reviewPage = reviewService.findReviewsByCustomerId(customerId, pageable);

        List<CustomerReviewResponseDTO> reviews = reviewPage.getContent();
        return ResponseEntity.ok(reviews);
    }
}
