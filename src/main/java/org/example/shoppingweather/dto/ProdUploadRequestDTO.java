package org.example.shoppingweather.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ProdUploadRequestDTO {
    private String name;
    private Integer price;
    private String quantity;
    private String category;
    private Integer season;
    private Integer temperature;
    private String description;
    private MultipartFile mainPicture; // Change to MultipartFile
    private List<MultipartFile> detailPictures; // Change to List<MultipartFile>
}