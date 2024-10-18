package org.example.shoppingweather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CustomerResponseDTO {

    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;

}
