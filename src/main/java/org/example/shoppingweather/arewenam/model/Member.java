package org.example.shoppingweather.arewenam.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString

public class Member {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;

}
