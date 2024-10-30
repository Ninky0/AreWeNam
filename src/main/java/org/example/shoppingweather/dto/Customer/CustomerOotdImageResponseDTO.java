package org.example.shoppingweather.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
public class CustomerOotdImageResponseDTO {
    private String picture; // 이미지 URL

    // 생성자 추가
    public CustomerOotdImageResponseDTO(String picture) {
        this.picture = picture;
    }
}