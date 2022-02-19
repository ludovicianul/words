package io.github.ludovicianul.words.game.util;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.definition.WordDefinition;
import io.github.ludovicianul.words.game.stats.Stats;
import org.fusesource.jansi.Ansi;

import java.util.Locale;
import java.util.Set;

import static java.lang.System.out;

public abstract class ConsoleUtil {

  private static final String STATS_LINE =
      " ------------------------------------------------------------ ";
  private static final String STATS_HEADER =
      "| Game        | Letters | Total | Lost | Attempts | Time     |";
  private static final String STATS_ROW = "| %-12s| %-8s| %-6s| %-5s| %-9s| %-9s|";

  public static void printStats(Set<Stats> statsList) {
    out.println();
    out.println(Ansi.ansi().bold().a(STATS_LINE).reset());
    out.println(Ansi.ansi().bold().a(STATS_HEADER).reset());
    out.println(Ansi.ansi().bold().a(STATS_LINE).reset());
    statsList.forEach(
        stats -> {
          out.println(
              Ansi.ansi()
                  .bold()
                  .a(
                      String.format(
                          STATS_ROW,
                          stats.getGameType(),
                          stats.getWordLength(),
                          stats.getTotal(),
                          stats.getFails(),
                          stats.getBestMoves(),
                          stats.getBestTime() + "secs"))
                  .reset());
          out.println(Ansi.ansi().bold().a(STATS_LINE).reset());
        });
  }

  public static String formatString(String input, Ansi.Color color) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      builder.append(ConsoleUtil.formatResult(color, input.charAt(i)));
    }
    return builder.toString();
  }

  public static String formatResult(Ansi.Color color, char currentChar) {
    return Ansi.ansi()
        .fgBlack()
        .bg(color)
        .bold()
        .a((" " + currentChar + " ").toUpperCase(Locale.ROOT))
        .reset()
        .toString();
  }

  public static void printSelectedWorDefinition(String word, Language language) {
    String selectedWordFormat = Ansi.ansi().fgYellow().bold().a(word).reset().toString();
    String wordDefinition =
        Ansi.ansi()
            .a(Ansi.Attribute.INTENSITY_FAINT)
            .a(WordDefinition.getDefinition(word, language))
            .reset()
            .toString();

    out.printf("%n %s - %s%n", selectedWordFormat, wordDefinition);
  }
}
