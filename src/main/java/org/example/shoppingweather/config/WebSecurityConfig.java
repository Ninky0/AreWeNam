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
                                        "/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/static/uploads/**" // Allow access to static resources
                                ).permitAll()
                                .requestMatchers(
                                        "/user/login", "/user/join", "/join", "/home/**",
                                        "/user/ootd_list", "/user/product/list",
                                        "/user/seasonproduct_list", "/user/seasonproduct_list?season=1",
                                        "/user/seasonproduct_list?season=2", "/user/seasonproduct_list?season=3",
                                        "/user/seasonproduct_list?season=4", "/user/product/detail/**",
                                        "/product/list", "/product/ootd_detail/**", "/ootd_list",
                                        "/product/detail/**", "/seasonproduct_list",
                                        "/user/product/search/**", "/user/api/ootd-images/**", "/user/api/ootd/detail/**",
                                        "/user/ootd/detail/**", "/user/api/ootd/detail/**",
                                        "/user/review_list" // Allow access to review list without login
                                ).permitAll()
                                .requestMatchers(
                                        "/user/cart/add", "/user/buy" // Require login for cart and purchase functions
                                ).authenticated()
                                .requestMatchers(
                                        "/admin/product/list", "/admin/product/upload", "/admin/**"
                                ).hasRole("ADMIN") // Allow admin access
                                .anyRequest().authenticated() // All other requests require authentication
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
                .csrf(AbstractHttpConfigurer::disable); // Disable CSRF for testing purposes only

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
