package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerReviewResponseDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {
    private final ReviewService reviewService;

    @GetMapping("/review/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        System.out.println(page+" 여기봐봐 "+size);
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

        System.out.println(response);

        return ResponseEntity.ok(response);
    }


}