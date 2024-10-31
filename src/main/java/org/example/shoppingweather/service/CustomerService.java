package org.example.shoppingweather.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.Customer.CustomerDeleteRequestDTO;
import org.example.shoppingweather.dto.Customer.CustomerUpdateRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.entity.*;
import org.example.shoppingweather.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(SignUpRequestDTO dto) {
        Customer customer = dto.toCustomer(bCryptPasswordEncoder);
        customer.setRole("ROLE_CUSTOMER");  // 권한 설정
        customerRepository.save(customer);
    }

    public Customer findBySession(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        return customerRepository.findByLoginId(loginId);
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
