package com.yyggee.eggs.controller;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.dto.common.BaseApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.BASE_URL)
public class RootController {

  /**
   * @return response entity
   */
  @GetMapping(value = "/")
  public ResponseEntity<BaseApiResponse> getRoot() {
    return new ResponseEntity<>(new BaseApiResponse("OK", "Service is running."), HttpStatus.OK);
  }

  /**
   * Returns a 200 response at the '/health-check' path. Check if service is running
   *
   * @return response entity
   */
  @GetMapping(value = Endpoints.HEALTH_CHECK)
  public ResponseEntity<BaseApiResponse> getHealthCheck() {
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "Service is running health and strong."), HttpStatus.OK);
  }
}
