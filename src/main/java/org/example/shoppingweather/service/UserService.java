package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.AddUserRequest;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;

    public Long save(AddUserRequest dto) {
        return customerRepository.save(Customer.builder()
                .login_id(dto.getLogin_id())  // dto 필드명에 맞춰 수정
                .password(dto.getPassword())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build()).getId();
    }
}
