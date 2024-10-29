package org.example.shoppingweather.dto.weather;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class WeatherResponse {
    private Response response;

    @Getter
    public static class Response {
        private Header header;
        private Body body;

        @Getter
        public static class Body {
            private Items items;

            @Getter
            public static class Items {
                private List<Item> item;

                @Getter
                public static class Item {
                    private String baseDate;
                    private String baseTime;
                    private String category;
                    private int nx;
                    private int ny;
                    private String obsrValue;
                }
            }
        }
    }

    private Long id;
    private String regionParent;
    private String regionChild;
    private int nx;
    private int ny;
    private double temperature;
    private String description;
    private LocalDateTime fetchedAt;


}
