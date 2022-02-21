package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.util.FileUtils;
import org.fusesource.jansi.Ansi;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Collection;

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    name = "dictionaries",
    version = "dictionaries 1.1",
    description = "Display builtin dictionaries and number of words")
public class DictionariesCommand implements Runnable {
  private static final int MIN_WORDS = 4;
  private static final int MAX_WORDS = 15;

  @Override
  public void run() {
    try {
      System.out.println();
      System.out.println("Available dictionaries:");
      for (Language language : Language.values()) {
        if (language != Language.USER) {
          Collection<String> words = FileUtils.loadBuiltInDictionary(language);
          System.out.println(
              Ansi.ansi()
                  .bold()
                  .fgGreen()
                  .a(String.format("• %s - total words: %s", language, words.size()))
                  .reset());
          for (int i = MIN_WORDS; i <= MAX_WORDS; i++) {
            final long wordSize = i;
            long noOfWords = words.stream().filter(word -> word.length() == wordSize).count();
            System.out.println(
                Ansi.ansi()
                    .bold()
                    .a("    ■ " + String.format("%s letter words: %s", wordSize, noOfWords)));
          }
        }
      }
    } catch (IOException e) {
      System.err.println("There was a problem reading the builtin dictionaries: " + e.getMessage());
    }
  }
}
