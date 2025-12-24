package com.yyggee.eggs.controller.ds2;

import com.yyggee.eggs.constants.Endpoints;
import com.yyggee.eggs.dto.common.BaseApiResponse;
import com.yyggee.eggs.model.ds2.Sequence;
import com.yyggee.eggs.service.ds2.PaymentOptionService;
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
public class SequencePayoptController {

  @Autowired private PaymentOptionService paymentOptionService;

  @GetMapping("/sequence")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  public ResponseEntity<BaseApiResponse> getCurrencies() {

    List<Sequence> sequences = paymentOptionService.findAll();

    return new ResponseEntity<>(
        new BaseApiResponse("OK", "sequence testing", sequences), HttpStatus.OK);
  }
}
