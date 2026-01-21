package com.yyggee.pluto;

import com.yyggee.pluto.commands.PlutoCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class PlutoApplication implements CommandLineRunner, ExitCodeGenerator {

  private final IFactory factory;
  private final PlutoCommand plutoCommand;
  private int exitCode;

  public PlutoApplication(IFactory factory, PlutoCommand plutoCommand) {
    this.factory = factory;
    this.plutoCommand = plutoCommand;
  }

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(PlutoApplication.class, args)));
  }

  @Override
  public void run(String... args) {
    exitCode = new CommandLine(plutoCommand, factory).execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }
}
