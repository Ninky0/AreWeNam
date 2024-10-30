package org.example.shoppingweather.repository;

import org.example.shoppingweather.dto.weather.WeatherId;
import org.example.shoppingweather.dto.weather.WeatherResponse;
import org.example.shoppingweather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, WeatherId> {

    // 복합 키로 Weather를 조회할 수 있는 메서드 추가
    Optional<Weather> findById(WeatherId id);

    // 특정 지역과 날짜의 날씨 데이터를 조회하는 메서드 (필요할 경우 사용)
//    Optional<Weather> findByRegionParentAndRegionChildAndFetchedAt(String regionParent, String regionChild, LocalDateTime fetchedAt);

}
