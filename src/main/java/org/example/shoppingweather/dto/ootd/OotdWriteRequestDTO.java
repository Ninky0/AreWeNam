package org.example.shoppingweather.dto.ootd;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OotdWriteRequestDTO {
    private Long productId;
    private Long customerId;
    private String tag;
    private MultipartFile picture;
    private LocalDateTime localDateTime;
}
