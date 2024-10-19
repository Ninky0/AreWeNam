package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageApiController {

    private final CustomerService customerService;

    @PutMapping("/update/{id}")
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
                        .url("/user/login")
                        .build()
        );
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/user/login";
    }

}
