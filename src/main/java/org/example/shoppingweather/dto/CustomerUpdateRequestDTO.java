package org.example.shoppingweather.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.shoppingweather.entity.Customer;

@Getter
@ToString
@RequiredArgsConstructor
public class CustomerUpdateRequestDTO {

    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Customer customer() {
        return Customer.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }

}
