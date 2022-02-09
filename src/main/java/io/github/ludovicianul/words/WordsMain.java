package io.github.ludovicianul.words;

import io.github.ludovicianul.words.cli.WordsCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
public class WordsMain implements QuarkusApplication {

  @Inject
  WordsCommand wordsCommand;

  @Inject
  CommandLine.IFactory factory;

  @Override
  public int run(String... args) {
    return new CommandLine(wordsCommand, factory)
        .setCaseInsensitiveEnumValuesAllowed(true)
        .execute(args);
  }
}
