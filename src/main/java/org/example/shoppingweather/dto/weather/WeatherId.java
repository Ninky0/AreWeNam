package org.example.shoppingweather.dto.weather;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class WeatherId implements Serializable {

    private String regionParent; // 상위지역
    private String regionChild; // 하위지역

    public WeatherId() {}

    public WeatherId(String regionParent, String regionChild) {
        this.regionParent = regionParent;
        this.regionChild = regionChild;
    }

}
