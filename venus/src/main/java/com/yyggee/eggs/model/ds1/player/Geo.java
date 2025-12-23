package com.yyggee.eggs.model.ds1.player;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Data
@AllArgsConstructor
@Embeddable
public class Geo {

    private String lat;
    private String lng;

    public Geo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geo geo = (Geo) o;
        return Objects.equals(lat, geo.lat) &&
                Objects.equals(lng, geo.lng);
    }
}
