package org.example.shoppingweather.dto.Customer;

import lombok.*;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
public class CustomerReviewResponseDTO {
    private final Long id;
    private final String picturePath;
    private final String content;
    private final Product product;
    private final Customer customer;
}
