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



// Getters and Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getName() { return name; }
public void setName(String name) { this.name = name; }

public Double getPrice() { return price; }
public void setPrice(Double price) { this.price = price; }

public String getPicture() { return picture; }
public void setPicture(String picture) { this.picture = picture; }

public Long getSeason() { return season; }
public void setSeason(Long season) { this.season = season; }

public Double getTemperature() { return temperature; }
public void setTemperature(Double temperature) { this.temperature = temperature; }
}
