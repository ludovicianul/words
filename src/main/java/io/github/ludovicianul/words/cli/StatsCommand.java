package io.github.ludovicianul.words.cli;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ludovicianul.words.game.stats.Stats;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    name = "stats",
    version = "stats 1.1",
    description = "Display high scores for each game")
public class StatsCommand implements Runnable {
  @Override
  public void run() {
    try {
      File statsFile = Paths.get(System.getProperty("user.home"), ".words", "stats.json").toFile();
      Type statsType = new TypeToken<HashSet<Stats>>() {}.getType();

      Set<Stats> existingAllGamesStats =
          new Gson().fromJson(Files.readString(statsFile.toPath()), statsType);
      ConsoleUtil.printStats(existingAllGamesStats);
    } catch (IOException e) {
      System.err.println("There was a problem reading Words stats: " + e.getMessage());
    }
  }
}
