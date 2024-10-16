package org.example.shoppingweather.controller;


import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpResponseDTO;
import org.example.shoppingweather.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/join")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        customerService.save(signUpRequestDTO); // 회원가입 메소드 호출
        return ResponseEntity.ok(
                SignUpResponseDTO.builder()
                        .url("/user/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .build()
        );
    }

//    @PostMapping("/login")
//    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO signInRequestDTO, HttpSession session) {
//        return ResponseEntity.ok(
//                memberService.signIn(signInRequestDTO.toMember(), session)
//        );
//    }

}