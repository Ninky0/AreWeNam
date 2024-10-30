package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.service.ProductService;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeViewController {

    private final WeatherService weatherService;
    private final ProductService productService;

    @GetMapping
    public String home() {
        return "main";
    }

    @GetMapping("/weather/regions")
    @ResponseBody
    public List<Region> getRegionList() {
        //csv파일 읽어옴(지역별 위도 경도)
        return weatherService.getRegionsFromCSV();
    }

    @GetMapping("/weather/search")
    public ResponseEntity searchWeather(@RequestParam String parent, @RequestParam String child) {
        WeatherResponse weather = weatherService.selectWeatherData(parent, child);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/recommend/temp")
    public ResponseEntity<List<Product>> getRecommendedProducts(@RequestParam("temperature") double temperature) {
        int tempIndex = mapTemperatureToIndex(temperature);
        List<Product> recommendedProducts = productService.findByTemperatureIndex(tempIndex);
        return ResponseEntity.ok(recommendedProducts);
    }

    private int mapTemperatureToIndex(double temperature) {
        if (temperature >= 28) {
            return 1;  // 28℃ 이상
        } else if (temperature >= 23 && temperature < 28) {
            return 2;  // 23℃ ~ 27℃
        } else if (temperature >= 20 && temperature < 23) {
            return 3;  // 20℃ ~ 22℃
        } else if (temperature >= 17 && temperature < 20) {
            return 4;  // 17℃ ~ 19℃
        } else if (temperature >= 12 && temperature < 17) {
            return 5;  // 12℃ ~ 16℃
        } else if (temperature >= 9 && temperature < 12) {
            return 6;  // 9℃ ~ 11℃
        } else if (temperature >= 5 && temperature < 9) {
            return 7;  // 5℃ ~ 8℃
        } else {
            return 8;  // 4℃ 이하
        }
    }


}
