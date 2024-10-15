package org.example.shoppingweather.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDTO {
    private boolean isLoggedIn;
    private String url;
    private String name;
    private String login_id;
    private String message;
}
