package org.example.shoppingweather.config;

import org.example.shoppingweather.config.security.CustomAuthenticationFailureHandler;
import org.example.shoppingweather.config.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationSuccessHandler successHandler,
            CustomAuthenticationFailureHandler failureHandler
    ) throws Exception {
        http
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**"
                                ).permitAll() // 정적 리소스 접근 허용
                                .requestMatchers(
                                        "/user/login", "/user/join", "/join", "/home/**",
                                        "/user/ootd_list", "/user/product/search",
                                        "/user/product/detail/**", // 로그인 없이 상품 상세 페이지 접근 허용
                                        "/user/product/list", "/user/api/ootd-images",
                                        "/user/ootd/detail/**", "/user/api/ootd/detail/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/admin/product/list", "/admin/product/upload", "/admin/**"
                                ).hasRole("ADMIN") // 관리자 접근 허용
                                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(
                        form -> form
                                .loginPage("/user/login")
                                .loginProcessingUrl("/user/login")
                                .successHandler(successHandler)
                                .failureHandler(failureHandler)
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/user/login")
                )
                .csrf(AbstractHttpConfigurer::disable); // 테스트 용도에서만 CSRF 비활성화

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}