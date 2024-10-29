package org.example.shoppingweather.dto.product;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class ProdUploadResponseDTO {
    private String url;
    private String mainPicturePath; // mainPicturePath 필드 추가
}
