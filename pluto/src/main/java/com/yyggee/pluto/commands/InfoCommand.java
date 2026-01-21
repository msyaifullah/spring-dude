package com.yyggee.pluto.commands;

import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "info",
    mixinStandardHelpOptions = true,
    description = "Display application information")
public class InfoCommand implements Callable<Integer> {

  @Value("${spring.application.name:pluto}")
  private String appName;

  @Value("${pluto.version:1.0.0}")
  private String version;

  @Option(
      names = {"-v", "--verbose"},
      description = "Show detailed information")
  private boolean verbose;

  @Override
  public Integer call() {
    System.out.println("Application: " + appName);
    System.out.println("Version: " + version);

    if (verbose) {
      System.out.println("Java Version: " + System.getProperty("java.version"));
      System.out.println("OS: " + System.getProperty("os.name"));
      System.out.println("User: " + System.getProperty("user.name"));
      System.out.println("Working Directory: " + System.getProperty("user.dir"));
    }

    return 0;
  }
}
