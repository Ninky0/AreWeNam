package org.example.shoppingweather.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PurchaseRequestDTO {
    private List<PurchaseProductDTO> products;
    private final Long customerId;
    private LocalDateTime localDateTime;
}
