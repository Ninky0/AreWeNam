package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.config.security.CustomUserDetails;
import org.example.shoppingweather.dto.Customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.service.CartService;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.OotdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageApiController {

    private final CustomerService customerService;
    private final OotdService ootdService;
    private final CartService cartService;

    @PutMapping("/edit/{id}")
    public ResponseEntity<UrlResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequestDTO dto) {

        // 서비스 메서드를 호출해 업데이트 실행
        customerService.updateUser(id, dto);

        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/mypage")
                        .build()
        );
    }

    @DeleteMapping("/quitout/{id}")
    public ResponseEntity<UrlResponseDTO> delete(
            @PathVariable Long id,
            @RequestBody CustomerDeleteRequestDTO dto) {

        customerService.deleteUser(id, dto);

        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/home")
                        .build()
        );
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/home";
    }

    // 특정 customerId 기준으로 작성한 게시물 조회
    @GetMapping("/posts/{customerId}")
    public ResponseEntity<Page<CustomerOotdImageResponseDTO>> getCustomerPosts(
            @PathVariable Long customerId,
            Pageable pageable) {
        Page<CustomerOotdImageResponseDTO> posts = ootdService.getOotdPostsByCustomerId(customerId, pageable);
        return ResponseEntity.ok(posts);
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

}
