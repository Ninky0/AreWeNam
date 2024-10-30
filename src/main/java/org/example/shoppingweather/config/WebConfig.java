package org.example.shoppingweather.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // "/uploads/**" 경로 요청을 "src/main/resources/static/uploads/" 디렉토리로 매핑
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:src/main/resources/static/uploads/");
        }

    }