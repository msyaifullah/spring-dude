package com.yyggee.eggs.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GenericSpringEventPublisher {

  @Autowired private ApplicationEventPublisher applicationEventPublisher;

  public void publishCustomEvent(final String message) {
    System.out.println("Publishing custom event. ");
    GenericSpringEvent<String> customSpringEvent =
        new GenericSpringEvent<String>("ObjectEvent", true);
    applicationEventPublisher.publishEvent(customSpringEvent);
  }
}
