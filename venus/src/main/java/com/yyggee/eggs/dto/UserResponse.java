package com.yyggee.eggs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyggee.eggs.model.ds1.Role;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

  private Integer id;
  private String username;
  private String email;
  List<Role> roles;
}
