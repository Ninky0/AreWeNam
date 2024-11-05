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
import org.example.shoppingweather.repository.ProductRepository;
import org.example.shoppingweather.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
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

    public List<Product> extractProductsFromPurchases(List<Purchase> purchases) {
        List<Product> productList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Purchase purchase : purchases) {
            try {
                Map<String, Integer> productMap = objectMapper.readValue(purchase.getProductList(), new TypeReference<Map<String, Integer>>() {});
                for (Map.Entry<String, Integer> entry : productMap.entrySet()) {
                    String productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    Product product = productService.getProductById(Long.valueOf(productId));
                    product.setQuantity(String.valueOf(quantity));
                    productList.add(product);
                }
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return productList;
    }

}
