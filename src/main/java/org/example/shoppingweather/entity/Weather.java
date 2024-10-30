package org.example.shoppingweather.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.example.shoppingweather.dto.weather.WeatherId;
import org.example.shoppingweather.dto.weather.WeatherResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "weather", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"regionParent", "regionChild"}) // 복합 키
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Weather {

    @EmbeddedId
    private WeatherId id; // 복합 키 ( regionParent + regionChild )

    @NotNull
    @Column(nullable = false)
    private int nx; // x 좌표

    @NotNull
    @Column(nullable = false)
    private int ny; // y 좌표

    @NotNull
    @Column(nullable = false)
    private double temperature; // 온도

    @NotNull
    @Column(nullable = false)
    private String description; // 날씨 상태 설명

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") // 서울 시간으로 연월일시분초
    private LocalDateTime fetchedAt;  // 데이터 수집 시각

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }


    @Builder
    public Weather(WeatherId id, int nx, int ny, double temperature, String description, LocalDateTime fetchedAt) {
        this.id = id;
        this.nx = nx;
        this.ny = ny;
        this.temperature = temperature;
        this.description = description;
        this.fetchedAt = fetchedAt;
    }

    public WeatherResponse toWeatherResponse() {
        return WeatherResponse.builder()
                .regionParent(id.getRegionParent())
                .regionChild(id.getRegionChild())
                .nx(nx)
                .ny(ny)
                .description(this.description)
                .temperature(this.temperature)
                .fetchedAt(fetchedAt)
                .build();
    }
}
