package org.example.shoppingweather.dto.weather;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Region {
    private String regionParent;
    private String regionChild;
    private int nx;
    private int ny;
}
