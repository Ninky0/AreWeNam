package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.Customer.CustomerPostResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.Customer.CustomerOotdImageResponseDTO; // OOTD 이미지 응답 DTO 임포트
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.*;
import org.example.shoppingweather.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.example.shoppingweather.entity.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OotdRepository ootdRepository;  // OOTD 저장을 위한 리포지토리 추가
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    public void save(SignUpRequestDTO dto) {
        Customer customer = dto.toCustomer(bCryptPasswordEncoder);
        customer.setRole("ROLE_CUSTOMER");  // 권한 설정
        customerRepository.save(customer);
    }

    public Customer findBySession(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        return customerRepository.findByLoginId(loginId);
    }

    public ProdReadResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id))
                .toProdReadResponseDTO();
    }

    public Page<ProdReadResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(Product::toProdReadResponseDTO);
    }


    // 상품 이름으로 검색하는 메서드 추가
    public Page<ProdReadResponseDTO> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(Product::toProdReadResponseDTO);
    }

    public void updateUser(Long id, CustomerUpdateRequestDTO dto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        existingCustomer.setName(dto.getName());
        existingCustomer.setEmail(dto.getEmail());
        existingCustomer.setPhone(dto.getPhone());
        existingCustomer.setAddress(dto.getAddress());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingCustomer.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        customerRepository.save(existingCustomer);
    }

    public void deleteUser(Long id, CustomerDeleteRequestDTO dto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (bCryptPasswordEncoder.matches(dto.getPassword(), existingCustomer.getPassword())) {
            customerRepository.delete(existingCustomer);
        } else {
            throw new RuntimeException("Incorrect password.");
        }
    }

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

    // OOTD 게시글 저장 기능 추가
    public void saveOotdPost(Long customerId, String tag, String picturePath, Long productId) {
        // customerId를 사용하여 Customer 객체를 가져옵니다.
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID: " + customerId));

        Ootd ootd = new Ootd();
        ootd.setCustomer(customer); // Customer 객체를 설정합니다.
        ootd.setPicture(picturePath); // 이미지 경로 설정
        ootd.setTag(tag); // 태그 설정
        ootd.setProductId(productId); // 선택된 상품 ID 설정

        ootdRepository.save(ootd); // OOTD 데이터 저장
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

    public Page<CustomerOotdImageResponseDTO> getOotdImages(Pageable pageable) {
        Page<Ootd> ootdPage = ootdRepository.findAll(pageable);
        return ootdPage.map(ootd -> {
            CustomerOotdImageResponseDTO responseDTO = new CustomerOotdImageResponseDTO();
            responseDTO.setId(ootd.getId()); // OOTD ID 설정
            responseDTO.setPicture(ootd.getPicture()); // 이미지 경로 설정
            // 필요한 필드가 있으면 추가로 설정
            return responseDTO;
        });
    }

    public CustomerOotdImageResponseDTO findOotdById(Long id) {
        Ootd ootd = ootdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No OOTD found for the given ID"));

        // Create the DTO and populate fields from Ootd
        CustomerOotdImageResponseDTO responseDTO = new CustomerOotdImageResponseDTO();
        responseDTO.setId(ootd.getId());
        responseDTO.setPicture(ootd.getPicture());
        responseDTO.setTag(ootd.getTag());
        responseDTO.setProductId(ootd.getProductId());

        // Fetch and add product information if productId is available
        if (ootd.getProductId() != null) {
            Product product = productService.getProductById(ootd.getProductId()); // Use the new method here
            if (product != null) {
                responseDTO.setProductPrice(String.valueOf(product.getProductPrice()));
                responseDTO.setProductCategory(product.getProductCategory());
                responseDTO.setProductSeason(product.getProductSeason());
                responseDTO.setProductTemperature(product.getProductTemperature());
                responseDTO.setMainPicturePath(product.getMainPicturePath().replace("\\", "/")); // Clean the path
            }
        }

        return responseDTO;
    }

    public List<CustomerPostResponseDTO> getOotdPostsByCustomerId(Long customerId) {
        return ootdRepository.findByCustomerId(customerId).stream()
                .map(ootd -> new CustomerPostResponseDTO(ootd.getPicture(), ootd.getTag(), ootd.getDate()))
                .collect(Collectors.toList());
    }
}