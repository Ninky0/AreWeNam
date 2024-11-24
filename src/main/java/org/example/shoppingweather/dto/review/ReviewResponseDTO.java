package org.example.shoppingweather.dto.review;

import lombok.*;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
public class ReviewResponseDTO {
    private final Long id;
    private final String picturePath;
    private final String content;
    private final Product product;
    private final Customer customer;
    private final LocalDateTime date;
    private final String loginId;
}
