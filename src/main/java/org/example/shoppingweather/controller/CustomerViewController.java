package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.dto.OotdWriteRequestDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class CustomerViewController {

    private final CustomerService customerService;
    private final AdminService adminService;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final OotdService ootdService;
    private final ReviewService reviewService;

    @GetMapping("/join")
    public String signUp() {
        return "join";
    }

    @GetMapping("/login")
    public String signIn() {
        return "login";
    }

    @GetMapping("/shoppingcart")
    public String cart(HttpSession session, Model model) {
        Customer customer = customerService.findBySession(session);
        if (customer == null) {
            return "redirect:/login";
        }

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customer.getId());
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<Product> products = cartService.getProductsFromCart(cart);
            model.addAttribute("products", products);
            model.addAttribute("cart", cart);
        } else {
            // 장바구니가 비어있는 경우, 빈 카트 객체 생성
            Cart emptyCart = Cart.createEmptyCartForCustomer(customer);
            model.addAttribute("cart", emptyCart); // 빈 카트 객체를 모델에 추가
            model.addAttribute("products", new ArrayList<Product>()); // 빈 제품 목록 추가
        }

        return "shoppingcart";
    }

    @GetMapping("/shoppingcart/ordercomplete")
    public String ordercomplete() {
        return "ordercomplete";
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
    @GetMapping("/product/api/list")
    @ResponseBody
    public Map<String, Object> getProductListJson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ProdReadResponseDTO> productPage = productService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("last", productPage.isLast());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", page);

        return response;
    }

    @GetMapping("/ootd_list")
    public String ootdList(Model model, Pageable pageable) {
        Page<CustomerOotdImageResponseDTO> ootdImages = ootdService.getOotdImages(pageable);
        model.addAttribute("ootdImages", ootdImages);
        return "ootd_list";
    }

    @GetMapping("/ootd_write")
    public String ootdWrite(Model model, HttpSession session) {
        Customer customer = customerService.findBySession(session);
        if (customer != null) {
            model.addAttribute("customerId", customer.getId());
        } else {
            return "redirect:/user/login";
        }
        return "ootd_write";
    }

    @PostMapping("/ootd_write")
    public ResponseEntity<Map<String, String>> saveOotdPost(
            @ModelAttribute OotdWriteRequestDTO requestDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            String picturePath = reviewService.handleFileUpload(requestDTO.getPicture());
            ootdService.saveOotdPost(requestDTO, picturePath); // Here, we save Product directly

            response.put("url", "/user/ootd_list");
            response.put("message", "상품 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 상품 목록을 JSON 형태로 반환하는 API, 이름 필터 추가
    @GetMapping("/product/search")
    @ResponseBody
    public Page<ProdReadResponseDTO> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name) {
        Pageable pageable = PageRequest.of(page, size);

        if (name != null && !name.isEmpty()) {
            return productService.searchProductsByName(name, pageable);
        } else {
            return adminService.findAll(pageable);
        }
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

    @GetMapping("/search")
    public String search() {
        return "search";
    }
}