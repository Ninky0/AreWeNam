package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.ootd.OotdWriteRequestDTO;
import org.example.shoppingweather.dto.purchase.PurchaseProductDTO;
import org.example.shoppingweather.dto.purchase.PurchaseRequestDTO;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.customer.SignUpRequestDTO;
import org.example.shoppingweather.service.*;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final ProductService productService;
    private final AdminService adminService;

    @PostMapping("/join")
    public ResponseEntity<UrlResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        customerService.save(signUpRequestDTO); // 회원가입 진행 (DB 저장)
        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/user/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .build()
        );
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

    @PostMapping("/shoppingcart")
    public ResponseEntity<UrlResponseDTO> addCart(@RequestBody Map<String, Object> payload) {
        try {

            Long customerId = Long.parseLong(payload.get("customerId").toString());
            Long productId = Long.parseLong(payload.get("productId").toString());
            Integer quantity = Integer.parseInt(payload.get("quantity").toString());

            cartService.addProductToCart(customerId, productId, quantity);
            return ResponseEntity.ok(
                    UrlResponseDTO.builder().message("상품이 장바구니에 추가되었습니다. 장바구니를 확인하시겠습니까?").build()
            );

        } catch (NumberFormatException e) {
            e.printStackTrace(); // 로그에 예외 출력
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    UrlResponseDTO.builder().message("입력 형식이 올바르지 않습니다.").build()
            );
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 예외 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UrlResponseDTO.builder().message("상품 추가 중 오류가 발생했습니다.").build()
            );
        }
    }

    @PostMapping("/purchase/direct")
    public ResponseEntity purchaseDirectly(@RequestBody PurchaseProductDTO request) {
        try{
            Long customerId = request.getCustomerId();
            boolean isSuccess = cartService.directPurchase(customerId, request.getProductId(), request.getQuantity());

            if (isSuccess) {
                return ResponseEntity.ok(Map.of("message", "선택한 제품이 성공적으로 구매되었습니다. 주문 내역을 확인하시겠습니까?"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "상품 구매 처리에 실패했습니다."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류 발생"));
        }
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseProducts(@RequestBody PurchaseRequestDTO request) {
        try {
            Long customerId = request.getCustomerId();
            // 구매 서비스 로직을 호출하여 각 상품의 ID와 수량을 처리
            boolean isSuccess = cartService.processPurchase(customerId, request.getProducts(), request.getGrandTotal());

            if (isSuccess) {
                return ResponseEntity.ok(Map.of("message", "선택한 제품이 성공적으로 구매되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "상품 구매 처리에 실패했습니다."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류 발생"));
        }
    }

}