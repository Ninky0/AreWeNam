package org.example.shoppingweather.arewenam.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDTO {
    private boolean isLoggedIn;
    private String url;
    private String name;
    private String userId;
    private String message;
}
