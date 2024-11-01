package org.example.shoppingweather.service;


import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ProdReadResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(Product::toProdReadResponseDTO);
    }

    public ProdReadResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id))
                .toProdReadResponseDTO();
    }

    public List<Product> findByTemperatureIndex(int tempIndex) {
        List<Product> products = productRepository.findByTemperature(tempIndex);
        return products;
    }

    public Page<ProdReadResponseDTO> getProductsBySeason(String season, Pageable pageable) {
        return productRepository.findBySeason(season, pageable).map(Product::toProdReadResponseDTO);
    }

    // 상품 이름으로 검색하는 메서드 추가
    public Page<ProdReadResponseDTO> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(Product::toProdReadResponseDTO);
    }
}
