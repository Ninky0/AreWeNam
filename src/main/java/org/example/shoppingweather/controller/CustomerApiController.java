package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.config.security.CustomUserDetails;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final ProductService productService;
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

//    @PostMapping("/shoppingcart")
//    public ResponseEntity<UrlResponseDTO> addCart(@RequestBody Map<String, Object> payload) {
//        System.out.println("POST 요청이 도착했습니다: " + payload);
//        try {
//            // 구매 요청 처리
//            if (payload.get("action") != null && payload.get("action").equals("purchase")) {
//                // 고객 ID가 있는지 확인
//                if (payload.get("customerId") == null) {
//                    return ResponseEntity.badRequest().body(
//                            UrlResponseDTO.builder().message("고객 ID가 필요합니다.").build()
//                    );
//                }
//
//                Long customerId = Long.parseLong(payload.get("customerId").toString());
//                // 선택된 상품 ID 가져오기
//                List<String> productIdStrings = (List<String>) payload.get("productIds");
//                if (productIdStrings == null || productIdStrings.isEmpty()) {
//                    return ResponseEntity.badRequest().body(
//                            UrlResponseDTO.builder().message("선택된 상품이 필요합니다.").build()
//                    );
//                }
//
//                List<Long> productIds = productIdStrings.stream()
//                        .map(Long::valueOf)
//                        .collect(Collectors.toList());
//
//
//                // 장바구니 구매 처리
//                cartService.PurchaseCart(customerId, productIds); // 장바구니 구매 처리
//
//                return ResponseEntity.ok(
//                        UrlResponseDTO.builder().message("구매가 완료되었습니다.").build()
//                );
//            }
//
//            // 장바구니에 상품 추가 요청 처리
//            if (payload.get("customerId") == null || payload.get("productId") == null || payload.get("quantity") == null) {
//                return ResponseEntity.badRequest().body(
//                        UrlResponseDTO.builder().message("요청에 필요한 모든 값을 포함해야 합니다.").build()
//                );
//            }
//
//            Long customerId = Long.parseLong(payload.get("customerId").toString());
//            Long productId = Long.parseLong(payload.get("productId").toString());
//            Integer quantity = Integer.parseInt(payload.get("quantity").toString());
//
//            cartService.addProductToCart(customerId, productId, quantity);
//            return ResponseEntity.ok(
//                    UrlResponseDTO.builder().message("상품이 장바구니에 추가되었습니다. 장바구니를 확인하시겠습니까?").build()
//            );
//
//        } catch (NumberFormatException e) {
//            e.printStackTrace(); // 로그에 예외 출력
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    UrlResponseDTO.builder().message("입력 형식이 올바르지 않습니다.").build()
//            );
//        } catch (RuntimeException e) {
//            e.printStackTrace(); // 로그에 예외 출력
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    UrlResponseDTO.builder().message("구매 처리 중 오류가 발생했습니다.").build()
//            );
//        } catch (Exception e) {
//            e.printStackTrace(); // 로그에 예외 출력
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    UrlResponseDTO.builder().message("상품 추가 중 오류가 발생했습니다.").build()
//            );
//        }
//    }

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

//    // OOTD 이미지 API 엔드포인트
//    @GetMapping("/api/ootd-images")
//    public ResponseEntity<Map<String, Object>> getOotdImages(@RequestParam int offset, @RequestParam int limit) {
//        Pageable pageable = PageRequest.of(offset / limit, limit);
//        Page<CustomerOotdImageResponseDTO> ootdImages = ootdService.getOotdImages(pageable);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("images", ootdImages.getContent());
//        response.put("totalElements", ootdImages.getTotalElements());
//
//        return ResponseEntity.ok(response);
//    }

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

//    @PostMapping("/ootd_write")
//    public ResponseEntity<Map<String, String>> createPost(
//            HttpSession session,
//            @RequestParam("tag") String tag,
//            @RequestParam("picture") MultipartFile pictureFile,
//            @RequestParam("productId") Long productId) {
//
//        Map<String, String> response = new HashMap<>();
//
//        try {
//            // 로그인된 사용자 확인
//            String loginId = (String) session.getAttribute("loginId");
//            if (loginId == null) {
//                response.put("message", "로그인이 필요합니다.");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//            }
//
//            // 세션에서 사용자 가져오기
//            Customer customer = customerService.findBySession(session);
//            Long customerId = customer.getId();
//
//            // 이미지 경로 설정
//            String picturePath = null;
//            if (pictureFile != null && !pictureFile.isEmpty()) {
//                String fileExtension = pictureFile.getOriginalFilename().substring(pictureFile.getOriginalFilename().lastIndexOf("."));
//                String fileName = "picture_" + System.currentTimeMillis() + fileExtension;
//                Path savePath = Paths.get("src/main/resources/static/uploads/", fileName);
//
//                Files.createDirectories(savePath.getParent());
//                Files.copy(pictureFile.getInputStream(), savePath);
//                picturePath = "/uploads/" + fileName;
//            }
//
//            // OOTD 게시물 데이터와 이미지 경로를 저장
//            customerService.saveOotdPost(customerId, tag, picturePath, productId);
//
//            response.put("url", "/user/ootd_list");
//            response.put("message", "상품 등록이 완료되었습니다.");
//            return ResponseEntity.ok(response);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.put("message", "이미지 업로드 중 오류가 발생했습니다.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

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