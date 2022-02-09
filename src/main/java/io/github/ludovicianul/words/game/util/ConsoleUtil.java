package io.github.ludovicianul.words.game.util;

import io.github.ludovicianul.words.definition.WordDefinition;
import io.github.ludovicianul.words.game.GameContext;
import org.fusesource.jansi.Ansi;

import java.util.Locale;

import static java.lang.System.out;

public abstract class ConsoleUtil {

  public static String formatResult(Ansi.Color color, char currentChar) {
    return Ansi.ansi()
        .fgBlack()
        .bg(color)
        .bold()
        .a((" " + currentChar + " ").toUpperCase(Locale.ROOT))
        .reset()
        .toString();
  }

  public static void printSelectedWorDefinition(GameContext gameContext) {
    String selectedWordFormat =
        Ansi.ansi().fgYellow().bold().a(gameContext.getSelectedWord()).reset().toString();
    String wordDefinition =
        Ansi.ansi()
            .a(Ansi.Attribute.INTENSITY_FAINT)
            .a(
                WordDefinition.getDefinition(
                    gameContext.getSelectedWord(), gameContext.getLanguage()))
            .reset()
            .toString();

    out.printf(
        "%nBetter luck next time. Selected word was: %s - %s%n",
        selectedWordFormat, wordDefinition);
  }
}
