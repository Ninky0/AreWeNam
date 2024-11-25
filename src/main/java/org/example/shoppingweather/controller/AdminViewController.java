package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.purchase.PurchaseDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.service.CartService;
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
@RequestMapping("/admin")
public class AdminViewController {

    private final CartService cartService;
    private final ProductService productService;

    // 판매자가 판매하는 상품들
    @GetMapping("/product/list")
    public String productList(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        Page<ProdReadResponseDTO> productPage = productService.findAll(pageable);
        List<ProdReadResponseDTO> products = productPage.getContent();

        int totalPages = productPage.getTotalPages();
        int pageBlock = 10; // 페이지 블록 크기
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        model.addAttribute("products", products);
        model.addAttribute("productPage", productPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPrevious", startPage > 0);
        model.addAttribute("showNext", endPage < totalPages - 1);

        return "product_list";
    }

    @GetMapping("/product/upload")
    public String uploadProduct() {
        //상품 등록 폼으로 이동
        return "upload_product";
    }

    // admin 상품 상세 정보 매핑 추가
    @GetMapping("/product/detail_product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProdReadResponseDTO product = productService.findById(id); // id를 사용하여 상품 정보 조회
        model.addAttribute("product", product); // 조회된 상품 정보를 모델에 추가하여 템플릿으로 전달
        return "detail_product"; // 상세 페이지 HTML 파일 이름 반환
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        // 상품의 상세 정보를 조회하여 수정 폼에 표시할 수 있도록 모델에 추가
        ProdReadResponseDTO product = productService.findById(id);
        model.addAttribute("product", product);
        return "edit_product"; // 수정 페이지 HTML 파일 이름
    }

    @GetMapping("/orders")
    public String listOrders(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        Page<PurchaseDTO> orderPage = cartService.findAllPurchases(pageable);
        List<PurchaseDTO> orders = orderPage.getContent();

        // 총 수익 계산
        int totalRevenue = (int) orders.stream()
                .mapToDouble(PurchaseDTO::getGrandTotal)
                .sum();

        int totalPages = orderPage.getTotalPages();
        int pageBlock = 10; // 페이지 블록 크기
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        model.addAttribute("orders", orders);
        model.addAttribute("totalRevenue", totalRevenue); // 모델에 총 수익 추가
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPrevious", startPage > 0);
        model.addAttribute("showNext", endPage < totalPages - 1);

        return "order_list";
    }

}
