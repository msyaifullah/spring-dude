package com.yyggee.eggs.service.ds1.ordero;

import com.yyggee.eggs.model.ds1.ordero.Ordero;
import com.yyggee.eggs.repositories.ds1.ordero.OrderoRepository;
import com.yyggee.eggs.repositories.ds1.ordero.PersonRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderoService {

  private final OrderoRepository orderoRepository;
  private final PersonRepository personRepository;

  public OrderoService(OrderoRepository orderoRepository, PersonRepository personRepository) {
    this.orderoRepository = orderoRepository;
    this.personRepository = personRepository;
  }

  public Iterable<Ordero> list() {
    return orderoRepository.findAll();
  }

  public Ordero save(Ordero ordero) {
    return orderoRepository.save(ordero);
  }

  public void save(List<Ordero> orderos) {
    orderos.forEach(this::save);
  }
}
