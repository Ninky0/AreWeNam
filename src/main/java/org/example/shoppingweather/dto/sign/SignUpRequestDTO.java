package org.example.shoppingweather.dto.sign;

import lombok.Getter;
import org.example.shoppingweather.entity.Customer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class SignUpRequestDTO {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Customer toCustomer(BCryptPasswordEncoder bCryptPasswordEncoder){
        return Customer.builder()
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }

}
