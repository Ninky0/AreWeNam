package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String login_id) {
        return memberRepository.findByLoginId(login_id)
                .orElseThrow(() -> new IllegalArgumentException((login_id)+" not found"));
    }
}
