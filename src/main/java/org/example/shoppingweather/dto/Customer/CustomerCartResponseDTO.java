package org.example.shoppingweather.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.shoppingweather.entity.Customer;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomerCartResponseDTO {

    private Long id;
    private Long customer;
    private String productList;

}
