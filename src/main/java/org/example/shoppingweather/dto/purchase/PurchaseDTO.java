package org.example.shoppingweather.dto.purchase;

import lombok.*;
import org.example.shoppingweather.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PurchaseDTO {
    private Long purchaseId;              // 구매 ID
    private LocalDateTime date;           // 주문 날짜
    private List<Product> products;
    private String customerLoginId;
    private Integer grandTotal;
}
