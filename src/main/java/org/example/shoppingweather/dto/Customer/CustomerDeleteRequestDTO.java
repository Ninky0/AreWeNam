package org.example.shoppingweather.dto.Customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CustomerDeleteRequestDTO {

    private String loginId;
    private String password;

}
