package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.service.AdminService;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class CustomerViewController {

    private final CustomerService customerService;
    private final AdminService adminService;
    private final CartRepository cartRepository;
    private final ProductService productService;

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
        // 세션에서 고객 정보 가져오기
        Customer customer = customerService.findBySession(session);
        if (customer == null) {
            // 고객 정보가 없는 경우, 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customer.getId());
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<Product> products = customerService.getProductsFromCart(cart);
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

//    // 상품 상세 정보 JSON 형식으로 제공
//    @GetMapping("/product/detail/{id}")
//    @ResponseBody
//    public ResponseEntity<ProdReadResponseDTO> getProductDetail(@PathVariable Long id) {
//        ProdReadResponseDTO product = customerService.findById(id);
//        if (product.getMainPicture() != null) {
//            String mainPicturePath = product.getMainPicture().replace("\\", "/");
//            product.setMainPicture(mainPicturePath);
//        }
//        return ResponseEntity.ok(product);
//    }

    // customer 상품 상세 정보 매핑 추가
    @GetMapping("/product/detail/{id}")
    public String detail(HttpSession session, @PathVariable Long id, Model model) {
        // id로 상품 정보 찾기
        ProdReadResponseDTO product = productService.findById(id);

        // mainPicture 경로에서 역슬래시(`\`)를 슬래시(`/`)로 변경
        if (product.getMainPicturePath() != null) {
            String mainPicturePath = product.getMainPicturePath().replace("\\", "/");
            product.setMainPicturePath(mainPicturePath); // 경로 수정 후 다시 설정
        }

        Customer customer = customerService.findBySession(session);

        // 수정된 product 객체를 모델에 추가
        model.addAttribute("product", product);
        model.addAttribute("customer",customer);

        // 상세 페이지 HTML 파일로 반환
        return "detail";
    }


    @GetMapping("/product_list")
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

        return "total_list";
    }


    @GetMapping("/ootd_list")
    public String ootdList(Model model, Pageable pageable) {
        Page<CustomerOotdImageResponseDTO> ootdImages = customerService.getOotdImages(pageable);
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

    @PostMapping("/ootd_write/save") // URL을 고유하게 변경
    public String saveOotdPost(
            @RequestParam("tag") String tag,
            @RequestParam("picture") MultipartFile pictureFile,
            @RequestParam("productId") Long productId,
            HttpSession session) {

        Customer customer = customerService.findBySession(session);
        if (customer == null) {
            return "redirect:/user/login"; // 로그인하지 않은 경우 로그인 페이지로 리디렉션
        }

        try {
            String picturePath = null;
            if (pictureFile != null && !pictureFile.isEmpty()) {
                String fileExtension = pictureFile.getOriginalFilename().substring(pictureFile.getOriginalFilename().lastIndexOf("."));
                String fileName = "picture_" + System.currentTimeMillis() + fileExtension;
                Path savePath = Paths.get("src/main/resources/static/uploads/", fileName);

                Files.createDirectories(savePath.getParent());
                Files.copy(pictureFile.getInputStream(), savePath);
                picturePath = "/uploads/" + fileName;
            }

            customerService.saveOotdPost(customer.getId(), tag, picturePath, productId);

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/user/ootd_list";
    }

    // OOTD 이미지 API 엔드포인트
    @GetMapping("/api/ootd-images")
    public ResponseEntity<Map<String, Object>> getOotdImages(@RequestParam int offset, @RequestParam int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<CustomerOotdImageResponseDTO> ootdImages = customerService.getOotdImages(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("images", ootdImages.getContent());
        response.put("totalElements", ootdImages.getTotalElements());

        return ResponseEntity.ok(response);
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
            return customerService.searchProductsByName(name, pageable);
        } else {
            return adminService.findAll(pageable);
        }
    }
}
