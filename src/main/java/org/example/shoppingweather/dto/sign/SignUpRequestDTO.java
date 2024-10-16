package org.example.shoppingweather.dto.sign;

import lombok.Getter;

@Getter
public class SignUpRequestDTO {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
}
