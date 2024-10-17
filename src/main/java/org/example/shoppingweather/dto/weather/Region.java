package org.example.shoppingweather.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Region {
    private String regionParent;
    private String regionChild;
    private int nx;
    private int ny;
}
