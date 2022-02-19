package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.game.GameType;
import org.fusesource.jansi.Ansi;
import picocli.CommandLine;

import java.util.Arrays;

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    name = "games",
    version = "games 1.1",
    description = "Display available games")
public class GamesCommand implements Runnable {

  @Override
  public void run() {
    System.out.println();
    System.out.println("Available games:");
    Arrays.stream(GameType.values())
        .forEach(game -> System.out.println(Ansi.ansi().bold().fgGreen().a("• " + game).reset()));
  }
}
