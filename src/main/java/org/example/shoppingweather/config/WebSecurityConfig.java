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
                                        "/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/static/uploads/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/user/login", "/user/join", "/join", "/home/**",
                                        "/user/ootd_list", "/user/product/list",
                                        "/user/seasonproduct_list", "/user/seasonproduct_list?season=1",
                                        "/user/seasonproduct_list?season=2", "/user/seasonproduct_list?season=3",
                                        "/user/seasonproduct_list?season=4", "/user/product/detail/**", // <-- 여기 수정
                                        "/user/product/list", "/user/product/api/list", "/product/ootd_detail/**", "/ootd_list",
                                        "/user/review_list", "/user/api/ootd-images/**", "/user/api/ootd/detail/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/user/cart/add", "/user/buy"
                                ).authenticated()
                                .requestMatchers(
                                        "/admin/product/list", "/admin/product/upload", "/admin/**"
                                ).hasRole("ADMIN")
                                .anyRequest().authenticated()
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
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
