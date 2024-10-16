package org.example.shoppingweather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.shoppingweather.repository")

public class ShoppingWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingWeatherApplication.class, args);
    }

}
