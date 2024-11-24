package org.example.shoppingweather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.ootd.CustomerOotdImageResponseDTO; // OOTD 이미지 응답 DTO 임포트
import org.example.shoppingweather.dto.customer.SignUpRequestDTO;
import org.example.shoppingweather.entity.*;
import org.example.shoppingweather.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        // role이 ROLE_ADMIN인지 ROLE_CUSTOMER인지 설정
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

}