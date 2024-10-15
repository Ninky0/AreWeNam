package org.example.shoppingweather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
}
