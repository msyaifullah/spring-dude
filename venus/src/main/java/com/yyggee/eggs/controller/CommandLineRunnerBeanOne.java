package com.yyggee.eggs.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyggee.eggs.model.ds1.ordero.Ordero;
import com.yyggee.eggs.model.ds1.player.Player;
import com.yyggee.eggs.service.ds1.book.BookService;
import com.yyggee.eggs.service.ds1.ordero.OrderoService;
import com.yyggee.eggs.service.ds1.payoption.POConfigService;
import com.yyggee.eggs.service.ds1.player.PlayerService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lib.yyggee.AggregateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@SpringBootApplication(scanBasePackages = "lib.yyggee")
public class CommandLineRunnerBeanOne implements CommandLineRunner {

  @Autowired PlayerService playerService;

  @Autowired BookService bookService;

  @Autowired OrderoService orderoService;

  @Autowired POConfigService poConfigService;

  @Autowired AggregateSource aggregateSource;

  Logger logger = LoggerFactory.getLogger(CommandLineRunnerBeanOne.class);

  @Override
  public void run(String... args) throws Exception {
    System.out.println("CommandLineRunnerBeanOne 1");

    logger.info(aggregateSource.getData("moss", "testing"));

    // Testing create book
    bookService.create();

    // Testing create player
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<Player>> typeReference = new TypeReference<List<Player>>() {};
    InputStream inputStream = TypeReference.class.getResourceAsStream("/seeds/players.json");
    try {
      List<Player> players = mapper.readValue(inputStream, typeReference);
      playerService.save(players);
      logger.debug("players Saved!");
    } catch (IOException e) {
      System.out.println("Unable to save players: " + e.getMessage());
    }

    // Testing create order
    ObjectMapper mapperOrder = new ObjectMapper();
    TypeReference<List<Ordero>> typeReferenceOrder = new TypeReference<List<Ordero>>() {};
    InputStream inputStreamOrder = TypeReference.class.getResourceAsStream("/seeds/orders.json");
    try {
      List<Ordero> orders = mapperOrder.readValue(inputStreamOrder, typeReferenceOrder);
      orderoService.save(orders);
      logger.debug("Orders Saved!");
    } catch (IOException e) {
      System.out.println("Unable to save orders: " + e.getMessage());
    }

    //        //Testing create order
    //        ObjectMapper mapperOrder = new ObjectMapper();
    //        TypeReference<List<POConfig>> typeReferenceOrder = new TypeReference<List<POConfig>>()
    // {};
    //        InputStream inputStreamOrder =
    // TypeReference.class.getResourceAsStream("/json/orders.json");
    //        try {
    //            List<POConfig> orders = mapperOrder.readValue(inputStreamOrder,
    // typeReferenceOrder);
    //            poConfigService.save(orders);
    //            logger.debug("Orders Saved!");
    //        } catch (IOException e) {
    //            System.out.println("Unable to save orders: " + e.getMessage());
    //        }
  }
}
