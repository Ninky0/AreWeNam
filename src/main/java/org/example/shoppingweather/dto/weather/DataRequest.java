package org.example.shoppingweather.dto.weather;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataRequest {
    private String name;
    private int value;
}
