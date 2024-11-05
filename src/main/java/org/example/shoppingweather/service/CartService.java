package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.PurchaseProductDTO;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.entity.Purchase;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.repository.CustomerRepository;
import org.example.shoppingweather.repository.ProductRepository;
import org.example.shoppingweather.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
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

//    public void PurchaseCart(Long customerId, List<Long> productIds) {
//        System.out.println("Customer ID Type: " + ((Object) customerId).getClass().getName());
//        System.out.println("Customer ID Value: " + customerId);
//        Cart cart = cartRepository.findByCustomerId(customerId)
//                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
//
//        // 장바구니의 선택된 상품 목록에 대한 구매 처리
//        for (Long productId : productIds) {
//            // 상품 정보 가져오기
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
//
//            // 장바구니 데이터를 Purchase로 옮기기기
//            Purchase purchase = Purchase.createPurchase(cart.getCustomer(), cart.getProductList());
//            purchase.setDate(LocalDateTime.now()); // 현재 시간 저장
//
//            // Purchase 엔티티 저장
//            purchaseRepository.save(purchase);
//
//        }
//
//        // 장바구니 초기화
//        cart.setProductList("{}"); // 비워두기
//        cartRepository.save(cart);
//
//    }

//    public void PurchaseCart(Long customerId, List<Long> selectedProductIds) {
//        System.out.println("Customer ID Type: " + ((Object) customerId).getClass().getName());
//        System.out.println("Customer ID Value: " + customerId);
//        Cart cart = cartRepository.findByCustomerId(customerId)
//                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
//
//        // 장바구니의 선택된 상품 목록에 대한 구매 처리
//        for (Long productId : selectedProductIds) {
//            // 상품 정보 가져오기
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
//
//            // 장바구니 데이터를 Purchase로 옮기기
//            Purchase purchase = Purchase.createPurchase(cart.getCustomer(), cart.getProductList());
//            purchase.setDate(LocalDateTime.now()); // 현재 시간 저장
//
//            // Purchase 엔티티 저장
//            purchaseRepository.save(purchase);
//        }
//
//        // 선택되지 않은 상품들만 장바구니에 남기기
//        List<Long> currentProductIds = getProductIdsFromCart(cart.getProductList());
//        currentProductIds.removeAll(selectedProductIds); // 선택된 상품 제거
//
//        // 남겨진 상품들로 장바구니 업데이트
//        cart.setProductList(convertIdsToProductList(currentProductIds));
//        cartRepository.save(cart);
//    }


    public void PurchaseCart(Long customerId, List<Long> selectedProductIds) {
        System.out.println("Customer ID Type: " + ((Object) customerId).getClass().getName());
        System.out.println("Customer ID Value: " + customerId);
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        // 장바구니에서 기존 상품 ID 가져오기
        List<Long> existingProductIds = getProductIdsFromCart(cart.getProductList());

        // 선택된 상품 ID를 제외한 남겨둘 상품 ID 리스트 작성
        List<Long> remainingProductIds = existingProductIds.stream()
                .filter(id -> !selectedProductIds.contains(id))
                .collect(Collectors.toList());

        // 구매 처리
        for (Long productId : selectedProductIds) {
            // 상품 정보 가져오기
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            // Purchase 엔티티 생성 및 저장
            Purchase purchase = Purchase.createPurchase(cart.getCustomer(), cart.getProductList());
            purchase.setDate(LocalDateTime.now());
            purchaseRepository.save(purchase);
        }

        // 장바구니 초기화 (남겨진 상품 ID로 업데이트)
        String updatedProductList = convertIdsToProductList(remainingProductIds);
        cart.setProductList(updatedProductList);
        cartRepository.save(cart);
    }

    // 장바구니에서 상품 ID를 가져오는 메서드
    private List<Long> getProductIdsFromCart(String productList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 List<Long>으로 변환
            return objectMapper.readValue(productList, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new RuntimeException("장바구니에서 상품 ID를 가져오는 중 오류가 발생했습니다.", e);
        }
    }

    // 상품 ID 목록을 문자열로 변환하는 메서드
    private String convertIdsToProductList(List<Long> productIds) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // List<Long>을 JSON 문자열로 변환
            return objectMapper.writeValueAsString(productIds);
        } catch (Exception e) {
            throw new RuntimeException("상품 ID 목록을 JSON 문자열로 변환하는 중 오류가 발생했습니다.", e);
        }
    }

    public boolean processPurchase(Long customerId, List<PurchaseProductDTO> products) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 고객 ID입니다."));
            Cart cart = cartRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new IllegalStateException("장바구니를 찾을 수 없습니다."));

            ObjectMapper objectMapper = new ObjectMapper();
            Map<Long, Integer> cartProducts = objectMapper.readValue(cart.getProductList(), new TypeReference<Map<Long, Integer>>() {});

            Map<Long, Integer> productMap = new HashMap<>();
            for (PurchaseProductDTO productDTO : products) {
                Product product = productRepository.findById(productDTO.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                int currentQuantity = Integer.parseInt(product.getQuantity());
                if (currentQuantity < productDTO.getQuantity()) {
                    throw new IllegalArgumentException("재고 부족");
                }

                // 장바구니에서 상품 제거
                Integer cartQuantity = cartProducts.get(productDTO.getProductId());
                if (cartQuantity != null) {
                    if (cartQuantity > productDTO.getQuantity()) {
                        cartProducts.put(productDTO.getProductId(), cartQuantity - productDTO.getQuantity());
                    } else {
                        cartProducts.remove(productDTO.getProductId());
                    }
                }

                productMap.put(product.getId(), productDTO.getQuantity());
                product.setQuantity(String.valueOf(currentQuantity - productDTO.getQuantity()));
                productRepository.save(product);
            }

            // 장바구니 업데이트
            String updatedProductList = objectMapper.writeValueAsString(cartProducts);
            cart.setProductList(updatedProductList);
            cartRepository.save(cart);

            String productListJson = objectMapper.writeValueAsString(productMap);
            Purchase purchase = Purchase.createPurchase(customer, productListJson);
            purchaseRepository.save(purchase);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
