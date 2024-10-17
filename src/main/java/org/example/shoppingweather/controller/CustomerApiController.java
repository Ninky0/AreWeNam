package org.example.shoppingweather.controller;


import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.sign.SignUpRequestDTO;
import org.example.shoppingweather.dto.sign.SignUpResponseDTO;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.service.CustomerService;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;
    private final WeatherService weatherService;

    @PostMapping("/join")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {

        customerService.save(signUpRequestDTO); // 회원가입 진행(db 저장)

        return ResponseEntity.ok(
                SignUpResponseDTO.builder()
                        .url("/user/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .build()
        );
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

}