package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeViewController {

    private final WeatherService weatherService;

    @GetMapping
    public String home(Model model) {
        //그냥 디폴트 값으로 (60,127) 일단 넘겨줌.
        model.addAttribute("weather", weatherService.getWeatherData(60, 127));
        return "weather_weather";
    }

    @GetMapping("/weather/regions")
    @ResponseBody
    public List<Region> getRegionList() {
        //csv파일 읽어옴(지역별 위도 경도)
        return weatherService.getRegionsFromCSV();
    }

    @GetMapping("/weather/search")
    public ResponseEntity searchWeather(@RequestParam int nx, @RequestParam int ny) {
        WeatherResponse weather = weatherService.getWeatherData(nx, ny);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/index")
    public String index() {
        return "main";
    }

}
