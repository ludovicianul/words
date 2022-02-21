package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.util.FileUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    name = "check",
    version = "check 1.1",
    description =
        "Check if the given word is valid or display a list of words matching given regex")
public class CheckWordsCommand implements Runnable {
  @CommandLine.Parameters(
      index = "0",
      paramLabel = "<word>",
      description = "The given word",
      arity = "0..1")
  private String word;

  @CommandLine.Option(
      names = {"-r", "--regex"},
      description = "A regex to display all matching words")
  private String regex;

  @CommandLine.Option(
      names = {"-l", "--language"},
      description = "Language of the dictionary to be used. Default: ${DEFAULT-VALUE}")
  private Language language = Language.EN;

  @Override
  public void run() {
    System.out.println();
    try {
      Collection<String> words = FileUtils.loadBuiltInDictionary(language);
      if (regex != null) {
        List<String> matchingWords =
            words.stream()
                .filter(word -> word.matches(regex.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        System.out.println("The following words are matching the given regex: " + matchingWords);
      } else if (word != null) {
        boolean exists = words.contains(word.toLowerCase(Locale.ROOT));
        System.out.printf(
            "Word %s %s valid", word.toUpperCase(Locale.ROOT), exists ? "is" : "is not");
      } else {
        System.out.println("No word was supplied! Please try again!");
      }
    } catch (IOException e) {
      System.err.println("There was an error while checking for valid words: " + e.getMessage());
    }
  }
}
