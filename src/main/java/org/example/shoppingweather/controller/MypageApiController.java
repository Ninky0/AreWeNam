package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.UrlResponseDTO;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.OotdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageApiController {

    private final CustomerService customerService;
    private final OotdService ootdService;

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

}
