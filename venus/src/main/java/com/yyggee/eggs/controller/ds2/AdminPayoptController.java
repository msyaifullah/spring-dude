package com.yyggee.eggs.controller.ds2;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.dto.AdminPayoptRequest;
import com.yyggee.eggs.dto.common.BaseApiResponse;
import com.yyggee.eggs.model.ds2.Currency;
import com.yyggee.eggs.service.ds2.AdminService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(Endpoints.BASE_URL + "/admin/payopt")
public class AdminPayoptController {

  @Autowired private AdminService adminService;

  @PostMapping("/test")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<BaseApiResponse> createCurrency(@RequestBody AdminPayoptRequest payoptReq) {
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "adm testing", "string testing"), HttpStatus.OK);
  }

  @GetMapping("/test")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  public ResponseEntity<BaseApiResponse> getCurrencies() {
    List<Currency> currencies = adminService.findAll();
    return new ResponseEntity<>(
        new BaseApiResponse("OK", "adm testing", currencies), HttpStatus.OK);
  }
}
