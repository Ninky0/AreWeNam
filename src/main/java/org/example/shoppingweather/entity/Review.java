package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String picture;

    // Product와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    // Customer와의 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;
}
