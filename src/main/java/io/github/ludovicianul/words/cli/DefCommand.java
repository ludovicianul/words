package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import picocli.CommandLine;

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
      arity = "1")
  private String word;

  @CommandLine.Option(
      names = {"-l", "--language"},
      description = "Language of the dictionary to be used. Default: ${DEFAULT-VALUE}")
  private Language language = Language.EN;

  @Override
  public void run() {
    System.out.println();
    ConsoleUtil.printSelectedWorDefinition(word, language);
  }
}
