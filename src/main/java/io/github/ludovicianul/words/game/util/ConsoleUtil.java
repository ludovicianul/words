package io.github.ludovicianul.words.game.util;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.definition.WordDefinition;
import org.fusesource.jansi.Ansi;

import java.util.Locale;

import static java.lang.System.out;

public abstract class ConsoleUtil {

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
