package org.example.shoppingweather.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomerCartRequestDTO {

    private Long id;
    private Long customerId;
    private List<Product> products;

}
