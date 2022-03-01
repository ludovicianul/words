package io.github.ludovicianul.words.cli;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.stats.Stats;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import io.github.ludovicianul.words.game.util.FileUtils;
import org.fusesource.jansi.Ansi;
import picocli.AutoComplete;
import picocli.CommandLine;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.lang.System.*;

@CommandLine.Command(
    name = "words",
    mixinStandardHelpOptions = true,
    header = "%n@|green Words version 1.2|@ %n",
    usageHelpAutoWidth = true,
    version = "words 1.2",
    subcommands = {
      AutoComplete.GenerateCompletion.class,
      CommandLine.HelpCommand.class,
      StatsCommand.class,
      GamesCommand.class,
      DictionariesCommand.class,
      CheckWordsCommand.class,
      DefCommand.class
    })
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
  private Set<Stats> existingAllGamesStats;
  private long startTime;

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
    words =
        FileUtils.loadBuiltInDictionary(language).stream()
            .filter(word -> word.length() == wordSize)
            .map(word -> word.toUpperCase(Locale.ROOT))
            .collect(Collectors.toList());
  }

  private void selectWord() {
    int item = ThreadLocalRandom.current().nextInt(words.size());
    selectedWord = words.get(item);
  }

  private void setWordSize() {
    if (wordSize > 15 || wordSize < 4) {
      out.println("Length not supported. Defaulting to 5");
      wordSize = 5;
    }
  }

  private void startGame() {
    startTime = currentTimeMillis();
    GameContext context =
        new GameContext()
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

  public void loadStats() throws IOException {
    String userDir = System.getProperty("user.home");
    Path words = Paths.get(userDir, ".words");
    if (!words.toFile().exists()) {
      Files.createDirectories(words);
    }
    File statsFile = new File(words.toFile().getAbsolutePath(), "stats.json");
    if (!statsFile.exists() && !statsFile.createNewFile()) {
      err.println(
          "Something went wrong while creating the stats.json file at "
              + statsFile.getAbsolutePath());
    }
    Type statsType = new TypeToken<HashSet<Stats>>() {}.getType();

    existingAllGamesStats = new Gson().fromJson(Files.readString(statsFile.toPath()), statsType);
    if (existingAllGamesStats == null) {
      existingAllGamesStats = new HashSet<>();
    }
  }

  private void finishGame() throws IOException {
    long gameTime = (System.currentTimeMillis() - startTime) / 1000;
    int moves = selectedGame.getAttempts();

    Stats existingGameStats = this.getExistingGameStatsForSelectedGame(gameTime, moves);

    existingGameStats.increaseTotal();
    existingGameStats.increaseFails(selectedGame.outcome());
    boolean bestMovesUpdated = existingGameStats.updateBestMoves(moves);
    boolean bestTimeUpdated = existingGameStats.updateBestTime(gameTime);

    this.writeGameStats(existingGameStats);
    out.println();
    if (bestMovesUpdated | bestTimeUpdated) {
      out.println(Ansi.ansi().fgBlue().bold().a("New High Score!").reset());
    }
    ConsoleUtil.printStats(
        existingAllGamesStats.stream()
            .filter(stats -> stats.getGameType() == selectedGame.gameType())
            .collect(Collectors.toSet()));
  }

  private void writeGameStats(Stats existingGameStats) throws IOException {
    existingAllGamesStats.add(existingGameStats);
    Files.writeString(
        Paths.get(System.getProperty("user.home"), ".words", "stats.json"),
        new Gson().toJson(existingAllGamesStats));
  }

  private Stats getExistingGameStatsForSelectedGame(long gameTime, int moves) {
    return existingAllGamesStats.stream()
        .filter(
            stats ->
                stats.getGameType() == selectedGame.gameType()
                    && stats.getLanguage() == language
                    && stats.getWordLength() == wordSize)
        .findFirst()
        .orElse(
            new Stats()
                .bestMoves(moves)
                .bestTime(gameTime)
                .total(0)
                .fails(0)
                .gameType(selectedGame.gameType())
                .language(language)
                .wordLength(wordSize));
  }

  @Override
  public void run() {
    try {
      loadStats();
      setWordSize();
      loadWords();
      selectWord();
      startGame();
      finishGame();
    } catch (Exception e) {
      err.println("There was a problem starting the game: " + e.getMessage());
    }
  }
}
