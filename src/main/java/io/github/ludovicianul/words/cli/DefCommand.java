package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import io.github.ludovicianul.words.game.util.FileUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    name = "def",
    version = "def 1.1",
    description = "Display the definition of the given word")
public class DefCommand implements Runnable {
  @CommandLine.Parameters(
      index = "0",
      paramLabel = "<word>",
      description = "The given word",
      arity = "0..1")
  private String word;

  @CommandLine.Option(
      names = {"-l", "--language"},
      description = "Language of the dictionary to be used. Default: ${DEFAULT-VALUE}")
  private Language language = Language.EN;

  @CommandLine.Option(
      names = {"-r", "--random"},
      description = "Define a random word in the given language")
  private boolean random;

  @Override
  public void run() {
    try {
      if (word != null) {
        ConsoleUtil.printSelectedWorDefinition(word, language);
      } else if (random) {
        Collection<String> words = FileUtils.loadBuiltInDictionary(language);
        String selectedWord =
            words.stream()
                .skip(ThreadLocalRandom.current().nextInt(Math.max(words.size(), 1)))
                .findFirst()
                .get();

        ConsoleUtil.printSelectedWorDefinition(selectedWord, language);
      } else {
        System.err.println("You need to supply a word!");
      }
    } catch (IOException e) {
      System.err.println("Something went wrong: " + e.getMessage());
    }
  }
}
