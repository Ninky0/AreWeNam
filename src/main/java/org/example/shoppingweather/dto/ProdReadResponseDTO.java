package org.example.shoppingweather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProdReadResponseDTO {
    private String name;
    private Integer price;
    private String mainPicture;
    private String description;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;
}
