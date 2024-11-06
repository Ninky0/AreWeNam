package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shoppingweather.dto.Customer.CustomerReviewResponseDTO;
import org.example.shoppingweather.dto.ReviewWriteRequestDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;

import java.time.LocalDateTime;

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
    private LocalDateTime date;

    // Purchase와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "purchaseId")
    private Purchase purchase;

    // Product와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    // Customer와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    // 정적 팩토리 메서드 추가
    public static Review fromDTO(ReviewWriteRequestDTO requestDTO, String picturePath, Product product, Customer customer, Purchase purchase) {
        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setPurchase(purchase);
        review.setPicturePath(picturePath);
        review.setContent(requestDTO.getContent());
        review.setDate(LocalDateTime.now());
        return review;
    }
}
