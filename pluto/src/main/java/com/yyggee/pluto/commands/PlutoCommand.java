package com.yyggee.pluto.commands;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Component
@Command(
    name = "pluto",
    mixinStandardHelpOptions = true,
    version = "pluto 1.0",
    description = "Pluto CLI - Command-line tool for various operations",
    subcommands = {HelloCommand.class, GreetCommand.class, InfoCommand.class})
public class PlutoCommand implements Runnable {

  @Spec CommandSpec spec;

  @Override
  public void run() {
    spec.commandLine().usage(System.out);
  }
}
