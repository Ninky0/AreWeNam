package org.example.shoppingweather.dto.Customer;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Service
public class CustomerPostResponseDTO {

    private String picture;
    private String tag;
    private LocalDateTime date;
    private String loginId; // assuming this is for the customer's loginId
    private String productName;

    // Constructor without duplicates
    public CustomerPostResponseDTO(String picture, String tag, LocalDateTime date, String loginId, String productName) {
        this.picture = picture;
        this.tag = tag;
        this.date = date;
        this.loginId = loginId;
        this.productName = productName;
    }

    // Additional constructors (if needed) or overloaded methods should differ in parameter types or order
}



