package com.yyggee.eggs.event;

import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class GenericSpringEventListener implements ApplicationListener<GenericSpringEvent<String>> {

  @Override
  public void onApplicationEvent(@NonNull GenericSpringEvent<String> event) {
    System.out.println("Received spring generic event - " + event.getSource());
  }
}
