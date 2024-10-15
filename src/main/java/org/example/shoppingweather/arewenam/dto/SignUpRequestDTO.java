package org.example.shoppingweather.arewenam.dto;

import lombok.Getter;
import org.example.shoppingweather.arewenam.model.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password))  // 비밀번호 암호화
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }
}
