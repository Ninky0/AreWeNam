package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.Customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.Cart;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.CartRepository;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public void save(SignUpRequestDTO dto) {
        Customer customer = dto.toCustomer(bCryptPasswordEncoder);

        // 여기서 권한 조정하기 ROLE_ADMIN 또는 ROLE_CUSTOMER
        customer.setRole("ROLE_CUSTOMER");

        customerRepository.save(customer);
    }

    public Customer findBySession(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        return customerRepository.findByLoginId(loginId);
    }

    public void updateUser(Long id, CustomerUpdateRequestDTO dto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // DTO의 정보를 기존 고객 정보에 적용
        existingCustomer.setName(dto.getName());
        existingCustomer.setEmail(dto.getEmail());
        existingCustomer.setPhone(dto.getPhone());
        existingCustomer.setAddress(dto.getAddress());

        // 비밀번호 업데이트가 필요한 경우
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingCustomer.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        // 업데이트된 고객 정보 저장
        customerRepository.save(existingCustomer);
    }

    public void deleteUser(Long id, CustomerDeleteRequestDTO dto) {
        // 기존 고객 정보 조회
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // 비밀번호 검증 (입력된 비밀번호와 저장된 비밀번호 비교)
        if (bCryptPasswordEncoder.matches(dto.getPassword(), existingCustomer.getPassword())) {

            // 비밀번호가 일치할 경우 고객 삭제
            customerRepository.delete(existingCustomer);
        } else {
            throw new RuntimeException("Incorrect password.");
        }

    }

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

        products.merge(productId, quantity, Integer::sum); // 기존 수량에 추가

        String updatedProductList = objectMapper.writeValueAsString(products);
        cart.setProductList(updatedProductList);
        cartRepository.save(cart);
    }
}

// 비밀번호 검증 (입력된 비밀번호와 저장된 비밀번호 비교)
//    if (bCryptPasswordEncoder.matches(dto.getPassword(), existingCustomer.getPassword())) {
//        // 비밀번호가 일치할 경우 고객 삭제
//        customerRepository.delete(existingCustomer);
//    } else {
//            throw new RuntimeException("Incorrect password.");
//    }
