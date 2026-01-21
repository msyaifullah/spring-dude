package com.yyggee.pluto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerDocument {
  private String pnr;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  private String email;
  private String phone;

  @JsonProperty("booking_date")
  private String bookingDate;

  @JsonProperty("flight_number")
  private String flightNumber;

  @JsonProperty("seat_number")
  private String seatNumber;
}
