package com.yyggee.eggs.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringEventListener implements ApplicationListener<CustomSpringEvent> {

    String eventMessage = "";
    @Override
    public void onApplicationEvent(CustomSpringEvent event) {
        eventMessage = event.getMessage();
        System.out.println("Received spring custom event on listener file with override - " + event.getMessage());
    }

    @EventListener
    public void handleSuccessful(CustomSpringEvent event) {
        System.out.println("Received spring custom event on listener file with even listener - "+event.getMessage());
    }

    public String getMessage(){
        return eventMessage;
    }
}

