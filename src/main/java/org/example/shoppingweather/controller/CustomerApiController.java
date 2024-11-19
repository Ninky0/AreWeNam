package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.config.security.CustomUserDetails;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.OotdWriteRequestDTO;
import org.example.shoppingweather.dto.PurchaseProductDTO;
import org.example.shoppingweather.dto.PurchaseRequestDTO;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.service.*;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ReviewService reviewService;
    private final AdminService adminService;
    private final OotdService ootdService;
    private String request;

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

    @DeleteMapping("/shoppingcart")
    public ResponseEntity<Map<String, String>> deleteFromCart(@RequestBody List<Map<String, Object>> selectedProducts) {
        try {
            Long customerId = getCurrentCustomerId();
            List<Long> productIds = selectedProducts.stream()
                    .map(product -> Long.parseLong(product.get("productId").toString()))
                    .toList();
            cartService.removeFromCart(customerId, productIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "선택한 제품이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류 발생"));
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

    private Long getCurrentCustomerId() {
        // SecurityContext에서 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자가 있는지 확인합니다.
        if (authentication != null && authentication.isAuthenticated()) {
            // 사용자 정보를 가져옵니다. (예: UserDetails 객체에서 ID를 가져오는 방식)
            // 여기서는 UserDetails 인터페이스를 구현한 CustomUserDetails를 가정합니다.
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 사용자 ID를 반환합니다.
            // Customer 객체에서 ID를 가져옴
            return userDetails.getCustomer().getId();
        }

        // 인증되지 않은 경우, 예외를 던지거나 null을 반환합니다.
        throw new RuntimeException("사용자가 인증되지 않았습니다");
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
    // ootd리스트에서 클릭하면 열리는 모달창안의 이미지
    @GetMapping("/api/ootd-images")
    public ResponseEntity<Map<String, Object>> getOotdImages(@RequestParam int offset, @RequestParam int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit , Sort.by("date").descending());
        Page<CustomerOotdImageResponseDTO> ootdImages = customerService.getOotdImages(pageable);

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
    @GetMapping("/product/ootd_detail/{id}")
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
    @GetMapping("/api/ootd/detail/{id}")
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

    // AJAX 요청에 대한 JSON 응답
    @PostMapping("/seasonproduct_list")
    @ResponseBody
    public Page<ProdReadResponseDTO> filterProductsBySeason(@RequestParam String season, int page) {
        Pageable pageable = PageRequest.of(page, 5); // 페이지당 5개
        return productService.getProductsBySeason(season, pageable);
    }

}