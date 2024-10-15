package org.example.shoppingweather.dto;

import lombok.Getter;
import lombok.ToString;
import org.example.shoppingweather.entity.Customer;

@Getter
@ToString
public class SignInRequestDTO {
    private String login_id;
    private String password;

    public Customer toCustomer() {
        return Customer.builder()
                .login_id(login_id)
                .password(password)
                .build();
    }
}
