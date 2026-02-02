package com.yyggee.eggs.service.ds2;

import com.yyggee.eggs.model.ds2.Currency;
import com.yyggee.eggs.repositories.ds2.CurrencyJPARepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

  @Autowired private CurrencyJPARepository currencyJPARepository;

  public List<Currency> findAll() {
    return currencyJPARepository.findAll();
  }

  public Optional<Currency> findById(long id) {
    return currencyJPARepository.findById(id);
  }
}
