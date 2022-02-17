package io.github.ludovicianul.words.cli;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameType;
import org.fusesource.jansi.Ansi;
import picocli.AutoComplete;
import picocli.CommandLine;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.System.err;
import static java.lang.System.out;

@CommandLine.Command(
    name = "words",
    mixinStandardHelpOptions = true,
    header = "%n@|green Words version 1.1|@ %n",
    usageHelpAutoWidth = true,
    version = "words 1.1",
    subcommands = {AutoComplete.GenerateCompletion.class, CommandLine.HelpCommand.class})
@Dependent
@Default
public class WordsCommand implements Runnable {

  @Inject Instance<Game> games;

  private List<String> words = new ArrayList<>();
  private String selectedWord;

  @CommandLine.Option(
      names = {"-w", "--word-size"},
      description = "The size of the selected words. Default: ${DEFAULT-VALUE}")
  private int wordSize = 5;

  @CommandLine.Option(
      names = {"-l", "--language"},
      description = "Language of the dictionary to be used. Default: ${DEFAULT-VALUE}")
  private Language language = Language.EN;

  @CommandLine.Option(
      names = {"-d", "--dictionary"},
      description = "User provided dictionary")
  private File dictionary;

  @CommandLine.Option(
      names = {"-g", "--game"},
      description = "The game to play. Default: ${DEFAULT-VALUE}")
  private GameType game = GameType.WORDLE;

  @CommandLine.Option(
      names = {"-r", "--lettersRemoved"},
      description =
          "The number of letters to remove. Has effect only when playing THREE_WORDS. Default: ${DEFAULT-VALUE}")
  private int lettersRmoved = 2;

  private Game selectedGame;

  private InputStream getWordsInputStream() {
    InputStream wordsStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("words_" + language.name().toLowerCase(Locale.ROOT) + ".txt");
    if (wordsStream == null) {
      wordsStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("words_en.txt");
    }
    return wordsStream;
  }

  private void loadWords() throws IOException {
    if (dictionary != null) {
      loadUserProvidedDictionary();
    } else {
      loadBuiltInDictionary();
    }
    printStartingLines();
  }

  private void printStartingLines() {
    String lettersFormat = Ansi.ansi().fgGreen().bold().a(wordSize + " letter").reset().toString();
    String gameFormat = Ansi.ansi().fgYellow().bold().a(game).reset().toString();
    String languageFormat = Ansi.ansi().fgYellow().bold().a(language).reset().toString();
    String wordsFormat = Ansi.ansi().fgGreen().bold().a(words.size()).reset().toString();

    out.printf("Playing with %s words.%n", lettersFormat);
    out.printf("Game: %s. Language: %s. %n", gameFormat, languageFormat);
    out.printf("%s words loaded. %n%n", wordsFormat);
  }

  private void loadUserProvidedDictionary() {
    try (BufferedReader reader = new BufferedReader(new FileReader(dictionary))) {
      words =
          reader
              .lines()
              .filter(word -> word.length() == wordSize)
              .map(word -> word.toUpperCase(Locale.ROOT))
              .collect(Collectors.toList());
      language = Language.USER;
    } catch (IOException e) {
      System.err.printf(
          "Could not load user provided dictionary %s: %s %n", dictionary, e.getMessage());
    }
  }

  private void loadBuiltInDictionary() throws IOException {
    try (InputStream wordsStream = getWordsInputStream();
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(wordsStream)))) {
      words =
          reader
              .lines()
              .filter(word -> word.length() == wordSize)
              .map(word -> word.toUpperCase(Locale.ROOT))
              .collect(Collectors.toList());
    }
  }

  private void selectWord() {
    SecureRandom random = new SecureRandom();
    int item = random.nextInt(words.size());
    selectedWord = words.get(item);
  }

  private void setWordSize() {
    if (wordSize > 15 || wordSize < 4) {
      out.println("Length not supported. Defaulting to 5");
      wordSize = 5;
    }
  }

  private void startGame() {
    GameContext context = new GameContext();
    context
        .words(words)
        .selectedWord(selectedWord)
        .language(language)
        .removedLetters(lettersRmoved);
    selectedGame =
        games.stream()
            .filter(currentGame -> currentGame.gameType() == game)
            .findFirst()
            .orElseThrow(
                () -> {
                  throw new IllegalArgumentException("Game does not exist!");
                });
    selectedGame.play(context);
  }

  public void saveStats() {
    out.println("Saving stats...");
    selectedGame.saveState();
  }

  @Override
  public void run() {
    try {
      setWordSize();
      loadWords();
      selectWord();
      startGame();
    } catch (Exception e) {
      err.println("There was a problem starting the game: " + e.getMessage());
    }
  }
}
