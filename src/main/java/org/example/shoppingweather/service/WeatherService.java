package org.example.shoppingweather.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingweather.client.WeatherClient;
import org.example.shoppingweather.dto.weather.Region;
import org.example.shoppingweather.dto.weather.WeatherId;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.dto.weather.Weatherdescription;
import org.example.shoppingweather.entity.Weather;
import org.example.shoppingweather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;
    private final ObjectMapper objectMapper;
    private final WeatherRepository weatherRepository;

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

            // WeatherResponse로 변환
            System.out.println("API Response: " + weatherData);
            WeatherResponse response = objectMapper.readValue(weatherData, WeatherResponse.class);

            if (response != null && response.getResponse() != null) {
                WeatherResponse.Response.Body body = response.getResponse().getBody();
                if (body != null && body.getItems() != null) {
                    List<WeatherResponse.Response.Body.Items.Item> items = body.getItems().getItem();
                    double temperature = 0.0;
                    String description = "정보 없음"; // 기본값 설정

                    for (WeatherResponse.Response.Body.Items.Item item : items) {
                        if ("T1H".equals(item.getCategory())) {
                            temperature = Double.parseDouble(item.getObsrValue());
                        } else if ("SKY".equals(item.getCategory())) {
                            String skyValue = item.getObsrValue();
                            // 기본값으로 흐림을 설정
                            description = "정보 없음";

                            if ("1".equals(skyValue)) {
                                description = "맑음"; // 맑음
                            }
                        } else if ("PTY".equals(item.getCategory())) {
                            String ptyValue = item.getObsrValue();
                            // PTY 값에 따라 날씨 상태를 설정
                            if ("0".equals(ptyValue)) {
                                // 비가 아닌 경우 (소나기 등)
                                if (!description.equals("맑음")) {
                                    description = "흐림"; // 기본적으로 흐림
                                }
                            } else if ("1".equals(ptyValue)) {
                                description = "비"; // 비
                            } else if ("3".equals(ptyValue)) {
                                description = "눈"; // 눈
                            } else if ("4".equals(ptyValue)) {
                                description = "소나기"; // 소나기
                            }
                        }
                    }

                    if (description.equals("정보 없음")) {
                        description = "현재 날씨 정보가 없습니다."; // 기본 메시지로 변경
                    }

                    // 최종 설정:  null이 아닐 때 사용
                    response.setTemperature(temperature);
                    response.setDescription(description); // 이미 기본값이 설정되어 있으므로 이 줄만으로 충분함
                }
            }
            return response;
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
                System.out.println("Reading line: " + line);
                // 라인이 비어 있거나 공백만 있는 경우 건너뜀
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",");
                // values 배열의 길이가 5인지 확인
                if (values.length < 5) {
                    System.out.println("Invalid line: " + line); // 잘못된 줄 로그
                    continue; // 잘못된 형식의 줄 건너뛰기
                }

                try {
                    Region region = new Region(values[1].trim(), values[2].trim(), Integer.parseInt(values[3].trim()), Integer.parseInt(values[4].trim()));
                    regionList.add(region);
                } catch (NumberFormatException e) {
                    System.out.println("Number format error in line: " + line); // 숫자 형식 오류 로그
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionList;
    }


    public void saveWeatherDataFromCSV() {
        List<Region> regions = getRegionsFromCSV();
        for (Region region : regions) {
            try {
                WeatherResponse weatherResponse = getWeatherData(region.getNx(), region.getNy());
                System.out.println("Fetched At from WeatherResponse: " + weatherResponse.getFetchedAt());

                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                LocalDateTime fetchedAt = now.plusHours(9).truncatedTo(ChronoUnit.SECONDS);
//                ZonedDateTime zonedFetchedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
//                LocalDateTime fetchedAt = zonedFetchedAt.plusHours(9).truncatedTo(ChronoUnit.SECONDS).toLocalDateTime(); // 초 단위로 잘라냄


                // 복합 키를 생성
                WeatherId weatherId = new WeatherId(region.getRegionParent(), region.getRegionChild());

                // 기존 데이터를 조회
                Optional<Weather> existingWeather  = weatherRepository.findById(weatherId);

                // 데이터 존재 확인
                if (existingWeather .isPresent()) {
                    // 데이터 존재할 경우 업데이트
                    Weather weatherToUpdate = existingWeather.get();
                    weatherToUpdate.setTemperature(weatherResponse.getTemperature());
                    weatherToUpdate.setDescription(weatherResponse.getDescription() != null ? weatherResponse.getDescription() : "정보 없음");
                    weatherToUpdate.setFetchedAt(fetchedAt);

                    weatherRepository.save(weatherToUpdate);
                } else {
                    // 데이터가 존재하지 않을 경우 새로 저장
                    Weather newWeather = Weather.builder()
                            .id(weatherId)
                            .nx(region.getNx())
                            .ny(region.getNy())
                            .temperature(weatherResponse.getTemperature())
                            .description(weatherResponse.getDescription() != null ? weatherResponse.getDescription() : "정보 없음")
                            .fetchedAt(fetchedAt) // 현재 시각 설정
                            .build();

                    weatherRepository.save(newWeather); // 새로운 객체 저장
                }


                System.out.println("API Response: " + weatherResponse);
                System.out.println("Fetched Temperature: " + weatherResponse.getTemperature());
                System.out.println("Fetched Description: " + weatherResponse.getDescription());
                System.out.println("Fetched At time: " +  weatherResponse.getFetchedAt());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
