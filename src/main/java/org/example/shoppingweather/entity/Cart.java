package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 이 생성자만 유지
@Getter
@Setter
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String productList; // JSON 형태로 제품 목록을 저장

    public static Cart createEmptyCartForCustomer(Customer customer) {
        Cart cart = new Cart(); // Lombok으로 생성된 protected 생성자 사용
        cart.customer = customer;
        cart.productList = "{}"; // 초기 빈 JSON 문자열 설정
        return cart;
    }
}
