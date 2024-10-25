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
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                        "/product_list", "/upload_product", "/uploads").permitAll()  // 정적 리소스에 대한 접근 허용
                                .requestMatchers("/user/login", "/user/join", "/join", "/home/**").permitAll()  // 로그인 및 회원가입과 홈페이지 관련 경로 접근 허용
                                .requestMatchers("/admin/product/list", "/admin/product/upload", "/admin/**").hasRole("ADMIN")  // 관리자 권한 요구 경로
                                .requestMatchers("/customer/mypage/**").hasRole("CUSTOMER")  // 고객 권한 요구 경로
                                .anyRequest().authenticated()  // 그 외의 모든 요청은 인증 요구
                )
                .formLogin(
                        form -> form
                                .loginPage("/user/login")
                                .loginProcessingUrl("/user/login")
                                .successHandler(successHandler)
                                .failureHandler(failureHandler)
                                .permitAll()  // 로그인 관련 페이지 접근 허용
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/user/login")  // 로그아웃 성공 시 로그인 페이지로 리다이렉트
                                .permitAll()  // 로그아웃 경로 접근 허용
                )
                .csrf(AbstractHttpConfigurer::disable);  // CSRF 보호 비활성화

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
