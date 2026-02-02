package com.yyggee.eggs.service.ds1.payoption;

import com.yyggee.eggs.model.ds1.payoption.POConfig;
import com.yyggee.eggs.repositories.ds1.payoption.POConfigRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class POConfigService {

  private final POConfigRepository poConfigRepository;

  public POConfigService(POConfigRepository poConfigRepository) {
    this.poConfigRepository = poConfigRepository;
  }

  public Iterable<POConfig> list() {
    return poConfigRepository.findAll();
  }

  public POConfig save(POConfig ordero) {
    return poConfigRepository.save(ordero);
  }

  public void save(List<POConfig> orderos) {
    orderos.forEach(this::save);
  }
}
