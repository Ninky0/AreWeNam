package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.repository.CustomerRepository;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // 장바구니 추가
    @Transactional
    public void addProductToCart(Long customerId, Long productId, Integer quantity) throws IOException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 고객 ID입니다."));

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> cartRepository.save(Cart.createEmptyCartForCustomer(customer)));

        Map<Long, Integer> products;
        if (cart.getProductList().isEmpty()) {
            products = new HashMap<>();
        } else {
            products = objectMapper.readValue(cart.getProductList(), new TypeReference<Map<Long, Integer>>() {
            });
        }

        products.merge(productId, quantity, Integer::sum);
        String updatedProductList = objectMapper.writeValueAsString(products);
        cart.setProductList(updatedProductList);
        cartRepository.save(cart);
    }

    // 장바구니 목록 불러오기
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

    public void removeFromCart(Long customerId, List<Long> productIds) {
        // 고객 ID로 장바구니 찾기
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        // JSON 형식의 productList를 Map으로 변환
        Map<Long, Integer> productMap = parseProductList(cart.getProductList());

        // 제품 삭제 로직
        for (Long productId : productIds) {
            productMap.remove(productId);
        }

        // 업데이트된 제품 목록을 다시 JSON 문자열로 변환
        String updatedProductList = convertMaptoJson(productMap);
        cart.setProductList(updatedProductList);

        // 업데이트된 장바구니를 저장
        cartRepository.save(cart);
    }

    // JSON 문자열을 Map으로 변환하는 메서드
    private Map<Long, Integer> parseProductList(String productList) {
        try {
            return objectMapper.readValue(productList, objectMapper.getTypeFactory().constructMapType(HashMap.class, Long.class, Integer.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("제품 목록을 파싱하는 중 오류가 발생했습니다.", e);
        }
    }

    // 위에꺼 반대
    private String convertMaptoJson(Map<Long, Integer> productMap) {
        try {
            return objectMapper.writeValueAsString(productMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("제품 목록을 JSON으로 변환하는 중 오류가 발생했습니다.", e);
        }
    }
}
