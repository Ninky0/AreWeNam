package org.example.shoppingweather.dto.sign;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDTO {
    private boolean isLoggedIn;
    private String url;
    private String name;
    private String loginId;
    private String message;
}
