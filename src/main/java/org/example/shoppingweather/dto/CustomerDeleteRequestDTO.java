package org.example.shoppingweather.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.shoppingweather.entity.Customer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@ToString
@RequiredArgsConstructor
public class CustomerDeleteRequestDTO {

    private String loginId;
    private String password;

    public Customer toCustomer(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Customer.builder()
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
    }

}
