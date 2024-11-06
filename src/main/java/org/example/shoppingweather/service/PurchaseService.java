package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.PurchaseDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.entity.Purchase;
import org.example.shoppingweather.entity.Review;
import org.example.shoppingweather.repository.ProductRepository;
import org.example.shoppingweather.repository.PurchaseRepository;
import org.example.shoppingweather.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

//    // 특정 구매의 제품 목록 가져오기
//    public List<Product> getProductsFromPurchase(Long purchaseId) throws Exception {
//        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow();
//        String productListJson = purchase.getProductList();
//        return objectMapper.readValue(productListJson, new TypeReference<List<Product>>() {});
//    }
//
//    // 고객 ID로 구매 상세 내역과 제품 정보 가져오기
//    public List<PurchaseDTO> getPurchaseDetailsByCustomerId(Long customerId) throws Exception {
//        List<Purchase> purchases = purchaseRepository.findByCustomerId(customerId);
//        List<PurchaseDTO> purchaseDTOs = new ArrayList<>();
//
//        for (Purchase purchase : purchases) {
//            List<Product> products = getProductsFromPurchase(purchase.getId());
//            PurchaseDTO purchaseDTO = new PurchaseDTO(
//                    purchase.getId(),
//                    purchase.getDate(),
//                    products
//            );
//            purchaseDTOs.add(purchaseDTO);
//        }
//        return purchaseDTOs;
//    }

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
