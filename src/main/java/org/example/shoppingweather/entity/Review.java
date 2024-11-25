package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shoppingweather.dto.review.ReviewWriteRequestDTO;

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

    // DTO로부터 데이터를 받아와 엔티티를 업데이트하는 메소드
    public void updateFromDTO(ReviewWriteRequestDTO dto, String newPicturePath) {
        this.content = dto.getContent();  // 리뷰 내용 업데이트
        this.picturePath = newPicturePath;  // 새 이미지 경로 업데이트
        this.date = LocalDateTime.now();
    }
}
