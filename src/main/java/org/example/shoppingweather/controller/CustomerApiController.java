package org.example.shoppingweather.controller;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final WeatherService weatherService;

    @PostMapping("/join")
    public ResponseEntity<UrlResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {

        customerService.save(signUpRequestDTO); // 회원가입 진행(db 저장)

        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/user/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .build()
        );
    }

    @PostMapping("/shoppingcart")
    public ResponseEntity<UrlResponseDTO> addCart(@RequestBody Map<String, Object> payload) {
        try {
            // null 및 올바른 데이터 타입 검사 후 변환
            if (payload.get("customerId") == null || payload.get("productId") == null || payload.get("quantity") == null) {
                return ResponseEntity.badRequest().body(
                        UrlResponseDTO.builder().message("요청에 필요한 모든 값을 포함해야 합니다.").build()
                );
            }

            // 캐스팅 대신 파싱을 사용하여 타입 변환 처리
            Long customerId = Long.parseLong(payload.get("customerId").toString());
            Long productId = Long.parseLong(payload.get("productId").toString());
            Integer quantity = Integer.parseInt(payload.get("quantity").toString());

            customerService.addProductToCart(customerId, productId, quantity);
            return ResponseEntity.ok(
                    UrlResponseDTO.builder().message("상품이 장바구니에 추가되었습니다. 장바구니를 확인하시겠습니까?").build()
            );
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    UrlResponseDTO.builder().message("입력 형식이 올바르지 않습니다.").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UrlResponseDTO.builder().message("상품 추가 중 오류가 발생했습니다.").build()
            );
        }
    }

}