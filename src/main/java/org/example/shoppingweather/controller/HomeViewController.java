package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeViewController {

    private final ProductService productService;

    @GetMapping
    public String home() {
        return "main";
    }

    @GetMapping("/seasonproduct_list")
    public String fourseason(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String season,
                             Model model) {
        int pageSize = 5; // 페이지 크기를 5로 설정
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        Page<ProdReadResponseDTO> productPage;

        // season 파라미터가 있을 경우 필터링
        if (season != null && !season.isEmpty()) {
            productPage = productService.getProductsBySeason(season, pageable); // 계절에 따른 필터링
        } else {
            productPage = productService.findAll(pageable); // 전체 상품 목록
        }

        List<ProdReadResponseDTO> products = productPage.getContent();
        int totalPages = productPage.getTotalPages();
        int pageBlock = 3;
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        model.addAttribute("products", products);
        model.addAttribute("productPage", productPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPrevious", startPage > 0);
        model.addAttribute("showNext", endPage < totalPages - 1);
        model.addAttribute("season", season);

        return "fourseason";
    }

}
