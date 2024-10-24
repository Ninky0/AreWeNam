package org.example.shoppingweather.service;

import jakarta.servlet.http.HttpSession;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
// 필요한 다른 임포트

@Service
public class CartService {

    private final ProductRepository productRepository;
    private CartRepository cartRepository;
    private HttpSession httpSession;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductsFromCart(Cart cart) {
        List<Product> products = new ArrayList<>();
        String productList = cart.getProductList();

        // 중괄호를 제거하고 문자열을 쉼표로 나눔
        String cleanList = productList.replaceAll("[{}]", "");
        String[] items = cleanList.split(",");

        // 제품 ID와 수량을 저장할 맵 생성
        Map<Long, Integer> productMap = new HashMap<>();

        // 각 항목을 반복하며 제품 ID와 수량 파싱
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                Long productId = Long.parseLong(parts[0].replace("\"", "").trim());
                Integer quantity = Integer.parseInt(parts[1].trim());
                productMap.put(productId, quantity);
            }
        }

        // 제품 ID로 제품을 조회하고 리스트에 추가
        for (Long productId : productMap.keySet()) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                // 수량을 설정하고 제품을 추가
                product.setQuantity(String.valueOf(productMap.get(productId))); // 수량 설정
                products.add(product);
            }
        }

        return products;
    }


}
