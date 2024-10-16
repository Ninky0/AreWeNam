package org.example.shoppingweather.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.CustomerUpdateRequestDTO;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class MypageApiController {

    private final CustomerService customerService;

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequestDTO dto,
            HttpSession session) {
        // 현재 로그인한 고객의 ID 가져오기 (세션 또는 인증된 사용자에서 가져옴)
        String loggedInCustomerId = (String) session.getAttribute("loggedInCustomerId");

        // 서비스 메서드를 호출해 업데이트 실행
        customerService.updateUser(id, dto, loggedInCustomerId);

        return ResponseEntity.ok("회원정보수정완료");
    }

}
