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
import org.example.shoppingweather.service.CartService;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AdminService adminService;

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
        model.addAttribute("customer", customer);

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customer.getId());
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<Product> products = cartService.getProductsFromCart(cart);
            model.addAttribute("products", products);
            model.addAttribute("cart", cart);
        } else {
            model.addAttribute("products", new ArrayList<>());
        }

        return "shoppingcart";
    }

    @GetMapping("/shoppingcart/ordercomplete")
    public String ordercomplete() {
        return "ordercomplete";
    }

    // 상품 상세 정보 JSON 형식으로 제공
    @GetMapping("/product/detail/{id}")
    @ResponseBody
    public ResponseEntity<ProdReadResponseDTO> getProductDetail(@PathVariable Long id) {
        ProdReadResponseDTO product = customerService.findById(id);
        if (product.getMainPicture() != null) {
            String mainPicturePath = product.getMainPicture().replace("\\", "/");
            product.setMainPicture(mainPicturePath);
        }
        return ResponseEntity.ok(product);
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
