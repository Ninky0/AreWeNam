package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shoppingweather.dto.Customer.CustomerReviewResponseDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;

@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String picturePath;
    private String content;

    // Product와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    // Customer와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    public CustomerReviewResponseDTO toCustomerReviewResponseDTO() {
        return CustomerReviewResponseDTO.builder()
                .id(id)
                .picturePath(picturePath)
                .content(content)
                .product(product)
                .customer(customer)
                .build();
    }
}
