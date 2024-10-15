package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.AddUserRequest;
import org.example.shoppingweather.entity.Customer;
import org.example.shoppingweather.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository memberRepository;

    public Long save(AddUserRequest dto) {
        return memberRepository.save(Customer.builder()
                .login_id(dto.getLogin_id())
                .password(dto.getPassword())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build()).getId();
    }

}
