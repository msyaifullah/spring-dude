package com.yyggee.pluto.commands;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "hello",
    mixinStandardHelpOptions = true,
    description = "Prints a simple hello message")
public class HelloCommand implements Callable<Integer> {

  @Option(
      names = {"-n", "--name"},
      description = "Name to greet",
      defaultValue = "World")
  private String name;

  @Option(
      names = {"-c", "--count"},
      description = "Number of times to print",
      defaultValue = "1")
  private int count;

  @Override
  public Integer call() {
    for (int i = 0; i < count; i++) {
      System.out.println("Hello, " + name + "!");
    }
    return 0;
  }
}
