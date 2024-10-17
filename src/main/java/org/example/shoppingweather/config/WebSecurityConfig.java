package org.example.shoppingweather.config;


import org.example.shoppingweather.config.security.CustomAuthenticationFailureHandler;
import org.example.shoppingweather.config.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class WebSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/static/**", "/css/**", "/js/**" , "/images/**" , "/product_list","/upload_product"
                );
    }

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
                                        new AntPathRequestMatcher("/user/login"),
                                        new AntPathRequestMatcher("/user/join"),
                                        new AntPathRequestMatcher("/join"),
                                        new AntPathRequestMatcher("/update/**")
                                ).permitAll()
                                .requestMatchers("/admin/product/list", "/admin/product/upload"
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
