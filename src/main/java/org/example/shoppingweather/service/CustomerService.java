package org.example.shoppingweather.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(SignUpRequestDTO dto) {
        Customer customer = dto.toCustomer(bCryptPasswordEncoder);
        customer.setRole("ROLE_CUSTOMER");
        customerRepository.save(customer);
    }

    public void updateUser(Long id, CustomerUpdateRequestDTO dto, String  loggedInCustomerId) {
        if (!id.equals(loggedInCustomerId)) {
            throw new RuntimeException("You are not authorized to update this user.");
        }

        // 고객을 데이터베이스에서 찾기
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // 고객 정보 업데이트
        customerRepository.save(Customer.builder()
                // dto 필드명에 맞춰 수정
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build()).getId();
    }
}
