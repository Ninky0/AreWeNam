package org.example.shoppingweather.dto.product;

import lombok.Builder;
import lombok.Data;
import org.example.shoppingweather.entity.Product;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProdUploadRequestDTO {
    private String name;
    private Integer price;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature; //범위를 나눠 그룹화하고, 인덱스 부여
    private String description;
    private MultipartFile mainPicture; // Change to MultipartFile

    public Product toProduct(){
        return Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .category(category)
                .season(season)
                .temperature(temperature)
                .description(description)
                .build();
    }
}