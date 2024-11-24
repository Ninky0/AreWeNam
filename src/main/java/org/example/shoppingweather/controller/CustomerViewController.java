package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class CustomerViewController {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/join")
    public String signUp() {
        return "join";
    }

    @GetMapping("/login")
    public String signIn() {
        return "login";
    }

    // customer 상품 상세 정보 매핑 추가
    @GetMapping("/product/detail/{id}")
    public String detail(HttpSession session, @PathVariable Long id, Model model) {
        // 상품 정보 찾기
        ProdReadResponseDTO product = productService.findById(id);
        Integer count = reviewService.countReview(id);

        // mainPicture 경로에서 역슬래시(`\`)를 슬래시(`/`)로 변경
        if (product.getMainPicturePath() != null) {
            String mainPicturePath = product.getMainPicturePath().replace("\\", "/");
            product.setMainPicturePath(mainPicturePath);
        }

        // 세션을 통해 로그인 여부 확인 (세션이 null일 때도 처리)
        Customer customer = (session != null) ? customerService.findBySession(session) : null;
        model.addAttribute("product", product);
        model.addAttribute("customer", customer);
        model.addAttribute("reviewCount", count);

        return "detail";
    }

    @GetMapping("/product/list")
    public String productList(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 15;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        Page<ProdReadResponseDTO> productPage = productService.findAll(pageable);
        List<ProdReadResponseDTO> products = productPage.getContent();

        int totalPages = productPage.getTotalPages();
        int pageBlock = 10;
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        model.addAttribute("products", products);
        model.addAttribute("productPage", productPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPrevious", startPage > 0);
        model.addAttribute("showNext", endPage < totalPages - 1);

        return "total_list";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

}