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
        customer.setRole("ROLE_ADMIN");
        customerRepository.save(customer);
    }

    public void updateUser(Long id, CustomerUpdateRequestDTO dto) {
        // 기존 고객 정보 조회
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

}
