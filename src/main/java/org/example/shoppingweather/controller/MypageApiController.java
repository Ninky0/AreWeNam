package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.CustomerUpdateResponseDTO;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageApiController {

    private final CustomerService customerService;

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerUpdateResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequestDTO dto) {

        // 서비스 메서드를 호출해 업데이트 실행
        customerService.updateUser(id, dto);

        return ResponseEntity.ok(
                CustomerUpdateResponseDTO.builder()
                        .url("/mypage")
                        .build()
                );
    }

}
