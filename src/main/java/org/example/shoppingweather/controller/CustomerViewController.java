package org.example.shoppingweather.controller;

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
@RequestMapping("/user")
public class CustomerViewController {

    private final WeatherService weatherService;

    public CustomerViewController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/join")
    public String signUp() {
        return "join";
    }

    @GetMapping("/login")
    public String signIn() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        //그냥 디폴트 값으로 (60,127) 일단 넘겨줌.
        model.addAttribute("weather", weatherService.getWeatherData(60, 127));
        return "home";
    }



}