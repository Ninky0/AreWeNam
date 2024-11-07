package org.example.shoppingweather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PurchaseProductDTO {
    private Long customerId;
    private Long productId;
    private Integer quantity;
    private Integer price; // 가격 추가
}
