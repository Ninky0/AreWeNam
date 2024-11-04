package org.example.shoppingweather.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOotdImageResponseDTO {
    private Long id; // OOTD 고유 식별자 추가
    private String picture;
    private String tag;
    private Long productId;

    // Product-related fields
    private String productPrice;
    private String productCategory;
    private String productSeason;
    private String productTemperature;
    private String mainPicturePath; // The main picture of the product

    // 추가: picture만을 받는 생성자
    public CustomerOotdImageResponseDTO(String picture) {
        this.picture = picture;
    }
}
