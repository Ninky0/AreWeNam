package org.example.shoppingweather.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class ProdReadResponseDTO {
    private Long id;
    private String name;
    private Integer price;
    private String mainPicture;
    private String description;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;
}