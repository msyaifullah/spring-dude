package com.yyggee.eggs.repositories.ds2;


import com.yyggee.eggs.model.ds2.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyJPARepository extends JpaRepository<Currency, Long> {
}

