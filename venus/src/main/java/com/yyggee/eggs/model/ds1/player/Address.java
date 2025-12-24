package com.yyggee.eggs.model.ds1.player;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class Address {

  private String street;
  private String suite;
  private String city;
  private String zipcode;

  @Embedded private Geo geo;

  public Address() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return Objects.equals(street, address.street)
        && Objects.equals(suite, address.suite)
        && Objects.equals(city, address.city)
        && Objects.equals(zipcode, address.zipcode);
  }
}
