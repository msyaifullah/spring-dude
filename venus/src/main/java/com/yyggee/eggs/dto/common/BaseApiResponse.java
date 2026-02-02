package com.yyggee.eggs.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseApiResponse {
  protected String status;
  protected String message;
  protected Object data;

  public BaseApiResponse(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public BaseApiResponse(String status, String message, Object data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }
}
