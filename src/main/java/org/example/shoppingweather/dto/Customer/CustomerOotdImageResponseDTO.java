package org.example.shoppingweather.dto.Customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CustomerOotdImageResponseDTO {
    private Long id; // OOTD 고유 식별자
    private String picture;
    private String tag;
    private LocalDateTime date;
    private Long productId;

    private String loginId; // Customer login ID
    private String productName; // Product name

    // Product-related fields
    private String productPrice;
    private String productCategory;
    private String productSeason;
    private String productTemperature;
    private String mainPicturePath; // The main picture of the product

    // Constructor for required fields
    public CustomerOotdImageResponseDTO(Long id, String picture, String tag, LocalDateTime date, Long productId, String loginId, String productName) {
        this.id = id;
        this.picture = picture;
        this.tag = tag;
        this.date = date;
        this.productId = productId;
        this.loginId = loginId;
        this.productName = productName;
    }
}
