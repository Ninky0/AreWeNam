package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.entity.Purchase;
import org.example.shoppingweather.entity.Review;
import org.example.shoppingweather.repository.PurchaseRepository;
import org.example.shoppingweather.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public List<Purchase> getPurchasesByCustomer(Customer customer) {
        return purchaseRepository.findByCustomer(customer);
    }

    public void enrichPurchasesWithProducts(List<Purchase> purchases) {
        ObjectMapper objectMapper = new ObjectMapper();

        for (Purchase purchase : purchases) {
            try {
                Map<String, Integer> productMap = objectMapper.readValue(purchase.getProductList(), new TypeReference<Map<String, Integer>>() {});
                List<Product> products = new ArrayList<>();
                List<Review> reviews = reviewRepository.findByPurchaseId(purchase.getId());

                // 각 제품 ID에 대한 리뷰 여부를 매핑
                Map<Long, Boolean> reviewedProductIds = new HashMap<>();
                for (Review review : reviews) {
                    reviewedProductIds.put(review.getProduct().getId(), true);
                }

                for (Map.Entry<String, Integer> entry : productMap.entrySet()) {
                    Long productId = Long.valueOf(entry.getKey());
                    Integer quantity = entry.getValue();
                    Product product = productService.getProductById(productId);
                    product.setQuantity(String.valueOf(quantity));

                    // 각 Product 객체에 현재 Purchase의 리뷰 상태 저장
                    boolean isReviewed = reviewedProductIds.getOrDefault(productId, false);
                    product.setReviewedForPurchase(purchase.getId(), isReviewed);

                    products.add(product);
                }
                purchase.setProducts(products);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing product list from purchase", e);
            }
        }
    }


}
