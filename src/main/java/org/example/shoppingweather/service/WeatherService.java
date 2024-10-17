package org.example.shoppingweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.client.WeatherClient;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String serviceKey;

    public WeatherResponse getWeatherData(int nx, int ny) {
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String baseDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));

        try {
            String weatherData = weatherClient.getWeatherData(
                    serviceKey, numOfRows, pageNo, dataType, baseDate, baseTime, nx, ny
            );

            return objectMapper.readValue(weatherData, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Region> getRegionsFromCSV() {
        List<Region> regionList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("storage/regionList.csv")))) {
            String line;

            // 첫 번째 라인(헤더)을 건너뛰기
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Region region = new Region(values[1], values[2], Integer.parseInt(values[3]), Integer.parseInt(values[4]));
                regionList.add(region);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionList;
    }
}
