package com.yyggee.eggs.repositories.ds1.ordero;

import com.yyggee.eggs.model.ds1.ordero.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Person getOneByEmail(String email);
}
