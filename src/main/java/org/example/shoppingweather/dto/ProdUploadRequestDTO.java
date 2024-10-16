package org.example.shoppingweather.dto;

import lombok.Getter;

@Getter
public class ProdUploadRequestDTO {
    private String name;
    private Integer price;
    private String mainPicture;
    private String detailPicture;
    private String description;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;
}
