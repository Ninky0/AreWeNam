package org.example.shoppingweather.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingweather.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {

    private final WeatherService weatherService;

    @Value("${schedule.cron}")
    private String cronExpression;

    @Scheduled(cron = "${schedule.cron}")
    public void fetchWeather() {
        try {
            weatherService.saveWeatherDataFromCSV(); // CSV에서 데이터를 저장
            log.info("Weather data saved successfully."); // 저장 완료 로그
        } catch (Exception e) {
            log.error("Error fetching weather data: ", e);
        }
    }

}