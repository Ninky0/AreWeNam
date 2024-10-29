package org.example.shoppingweather.dto.weather;

public enum Weatherdescription {
    CLEAR("맑음"),
    CLOUDY("흐림"),
    RAIN("비"),
    SNOW("눈"),
    SHOWER("소나기");

    private final String description;

    Weatherdescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Weatherdescription fromSkyValue(String skyValue) {
        switch (skyValue) {
            case "1":
                return CLEAR; // 맑음
            case "3":
                return CLOUDY; // 흐림
            default:
                return CLOUDY; // 기본값으로 흐림
        }
    }

    public static Weatherdescription fromPtyValue(String ptyValue) {
        switch (ptyValue) {
            case "0":
                return CLOUDY; // 흐림
            case "1":
                return RAIN;   // 비
            case "3":
                return SNOW;   // 눈
            case "4":
                return SHOWER; // 소나기
            default:
                return CLOUDY; // 기본값
        }
    }
}