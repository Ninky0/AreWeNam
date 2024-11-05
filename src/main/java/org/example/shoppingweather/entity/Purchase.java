package org.example.shoppingweather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "purchase")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    private LocalDateTime date;

    private Integer grandTotal;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String productList; // JSON 형태로 제품 목록을 저장

    @Transient
    private List<Product> products;

    public static Purchase createPurchase(Customer customer, String productList, Integer grandTotal) {
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setProductList(productList);
        purchase.setGrandTotal(grandTotal);
        purchase.setDate(LocalDateTime.now());
        return purchase;
    }

}
