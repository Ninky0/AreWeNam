package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.service.ProductService;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/seasonproduct_list")
    public String fourseason(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String season,
                             Model model) {
        int pageSize = 5; // 페이지 크기를 5로 설정
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        Page<ProdReadResponseDTO> productPage;

        // season 파라미터가 있을 경우 필터링
        if (season != null && !season.isEmpty()) {
            productPage = productService.getProductsBySeason(season, pageable); // 계절에 따른 필터링
        } else {
            productPage = productService.findAll(pageable); // 전체 상품 목록
        }

        List<ProdReadResponseDTO> products = productPage.getContent();
        int totalPages = productPage.getTotalPages();
        int pageBlock = 3;
        int startPage = (page / pageBlock) * pageBlock;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages - 1);

        model.addAttribute("products", products);
        model.addAttribute("productPage", productPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPrevious", startPage > 0);
        model.addAttribute("showNext", endPage < totalPages - 1);
        model.addAttribute("season", season);

        return "fourseason";
    }

}
