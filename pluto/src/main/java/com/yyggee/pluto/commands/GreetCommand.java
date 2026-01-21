package com.yyggee.pluto.commands;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(
    name = "greet",
    mixinStandardHelpOptions = true,
    description = "Greets users with customizable messages")
public class GreetCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "The name of the person to greet")
  private String name;

  @Option(
      names = {"-f", "--formal"},
      description = "Use formal greeting")
  private boolean formal;

  @Option(
      names = {"-t", "--time"},
      description = "Time of day: morning, afternoon, evening",
      defaultValue = "morning")
  private String timeOfDay;

  @Override
  public Integer call() {
    String greeting = getGreeting();
    String title = formal ? "Dear " : "";
    System.out.println(greeting + ", " + title + name + "!");
    return 0;
  }

  private String getGreeting() {
    return switch (timeOfDay.toLowerCase()) {
      case "afternoon" -> "Good afternoon";
      case "evening" -> "Good evening";
      default -> "Good morning";
    };
  }
}
