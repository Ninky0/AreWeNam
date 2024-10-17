package org.example.shoppingweather.dto.weather;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Item {
    private String baseDate;
    private String baseTime;
    private String category;
    private int nx;
    private int ny;
    private String obsrValue;
}
