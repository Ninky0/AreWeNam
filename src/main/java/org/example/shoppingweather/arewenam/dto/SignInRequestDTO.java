package org.example.shoppingweather.arewenam.dto;

import lombok.Getter;
import lombok.ToString;
import org.example.shoppingweather.arewenam.model.Member;

@Getter
@ToString
public class SignInRequestDTO {
    private String userId;
    private String password;

    public Member toMember() {
        return Member.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
