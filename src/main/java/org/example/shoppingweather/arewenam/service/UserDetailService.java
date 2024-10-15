package org.example.shoppingweather.arewenam.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.arewenam.config.security.CustomUserDetails;
import org.example.shoppingweather.arewenam.model.Member;
import org.example.shoppingweather.arewenam.mapper.MemberMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberMapper.signIn(username);
        if (member == null) {
            throw new UsernameNotFoundException(username + " not found");
        }

        return CustomUserDetails.builder()
                .member(member)
                .build();
    }
}
