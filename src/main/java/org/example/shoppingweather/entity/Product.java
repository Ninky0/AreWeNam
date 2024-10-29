package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;

    @Column(columnDefinition = "MEDIUMTEXT") // MySQL의 TEXT 유형 사용
    private String description;

    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;

    // 추가된 필드: mainPicturePath
    private String mainPicturePath;

    @Builder
    public Product(Long id, String name, Integer price, String mainPicturePath, String description, String quantity, String category, Integer season, Integer temperature) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.mainPicturePath = mainPicturePath; // mainPicturePath 추가
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.season = season;
        this.temperature = temperature;
    }

    public ProdReadResponseDTO toProdReadResponseDTO() {
        return ProdReadResponseDTO.builder()
                .id(id)
                .name(name)
                .price(price)
                .mainPicturePath(mainPicturePath) // mainPicturePath 추가
                .description(description)
                .quantity(quantity)
                .category(category)
                .season(season)
                .temperature(temperature)
                .build();
    }
}
