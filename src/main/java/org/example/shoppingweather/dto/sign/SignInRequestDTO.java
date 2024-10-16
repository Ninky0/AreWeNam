package org.example.shoppingweather.dto.sign;

import lombok.Getter;
import lombok.ToString;
import org.example.shoppingweather.entity.Customer;

@Getter
@ToString
public class SignInRequestDTO {
    private String loginId;
    private String password;

    public Customer toCustomer() {
        return Customer.builder()
                .loginId(loginId)
                .password(password)
                .build();
    }
}
