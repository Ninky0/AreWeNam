package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import java.util.Map;
import java.util.HashMap;

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
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;
    private Integer weather;
    private String mainPicturePath;

    private Double productPrice;
    private String productCategory;
    private String productSeason;
    private String productTemperature;

    // Getter 메서드들
    public Double getProductPrice() {
        return productPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductSeason() {
        return productSeason;
    }

    public String getProductTemperature() {
        return productTemperature;
    }


    // 리뷰 여부를 관리하는 Map
    @Transient // 이 필드는 데이터베이스에 저장되지 않음
    private Map<Long, Boolean> reviewedPurchases = new HashMap<>();

    @Builder
    public Product(Long id, String name, Integer price, String mainPicturePath, String description, String quantity, String category, Integer season, Integer temperature, Integer weather) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.mainPicturePath = mainPicturePath;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.season = season;
        this.temperature = temperature;
        this.weather = weather;
    }

    // 특정 구매 ID에 대한 리뷰 상태를 반환
    public boolean isReviewedForPurchase(Long purchaseId) {
        return reviewedPurchases.getOrDefault(purchaseId, false);
    }

    // 특정 구매 ID의 리뷰 상태를 설정
    public void setReviewedForPurchase(Long purchaseId, boolean reviewed) {
        reviewedPurchases.put(purchaseId, reviewed);
    }

    public ProdReadResponseDTO toProdReadResponseDTO() {
        return ProdReadResponseDTO.builder()
                .id(id)
                .name(name)
                .price(price)
                .mainPicturePath(mainPicturePath)
                .description(description)
                .quantity(quantity)
                .category(category)
                .season(season)
                .temperature(temperature)
                .weather(weather)
                .build();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", mainPicturePath='" + mainPicturePath + '\'' +
                ", description='" + description + '\'' +
                ", quantity='" + quantity + '\'' +
                ", category='" + category + '\'' +
                ", season=" + season +
                ", temperature=" + temperature +
                ", weather=" + weather +
                '}';
    }
}
