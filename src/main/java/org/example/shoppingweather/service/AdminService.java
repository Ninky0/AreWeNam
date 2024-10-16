package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ProdUploadRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;

    public Long save(ProdUploadRequestDTO dto) {
        return productRepository.save(Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .mainPicture(dto.getMainPicture())
                .detailPicture(dto.getDetailPicture())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .season(dto.getSeason())
                .temperature(dto.getTemperature())
                .build()).getId();
    }
}
