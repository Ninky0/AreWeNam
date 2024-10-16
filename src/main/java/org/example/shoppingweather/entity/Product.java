package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String mainPicture;
    private String detailPicture;
    private String description;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;

    @Builder
    public Product(String name, Integer price, String mainPicture, String detailPicture, String description, String quantity, String category, Integer season, Integer temperature) {
        this.name = name;
        this.price = price;
        this.mainPicture = mainPicture;
        this.detailPicture = detailPicture;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.season = season;
        this.temperature = temperature;
    }

}
