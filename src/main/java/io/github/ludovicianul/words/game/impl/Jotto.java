package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static java.lang.System.lineSeparator;
import static java.lang.System.out;

@Singleton
public class Jotto implements Game {
  private final List<String> gameState = new ArrayList<>();
  private GameContext gameContext;
  private GameOutcome gameOutcome;
  private int attempts;
  private int additionalRedraw = 1;

  private void startGame() {
    Scanner scanner = new Scanner(System.in);
    String failed = "";
    boolean guessed = false;

    while (!guessed) {
      attempts++;
      promptSystemIn(failed);
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      if (!gameContext.isValidWord(word)) {
        failed = " (Not a valid word)";
        additionalRedraw++;
      } else {
        failed = "";
        guessed = gameContext.isGuessed(word);
        gameState.add(formatWord(word));
      }
      clearScreen();
      printGameState();
    }
    finishGame();
  }

  private void promptSystemIn(String additional) {
    out.printf("Enter you guess. Attempt %s%s: %n", attempts, additional);
    additionalRedraw++;
  }

  private void printGameState() {
    gameState.forEach(out::println);
  }

  private void finishGame() {
    gameOutcome = GameOutcome.SUCCESS;
    out.println(lineSeparator());
    out.println("Congrats! Guessed word:");
    out.println(ConsoleUtil.formatString(gameContext.getSelectedWord(), Ansi.Color.GREEN));
    ConsoleUtil.printSelectedWorDefinition(
        gameContext.getSelectedWord(), gameContext.getLanguage());
  }

  private String formatWord(String word) {
    int counter = 0;
    String selectedWord = gameContext.getSelectedWord();
    for (int i = 0; i < word.length(); i++) {
      if (selectedWord.indexOf(word.charAt(i)) != -1) {
        selectedWord = selectedWord.replaceFirst(String.valueOf(word.charAt(i)), "");
        counter++;
      }
    }
    return ConsoleUtil.formatString(word, Ansi.Color.WHITE)
        + ConsoleUtil.formatString(String.valueOf(counter), Ansi.Color.YELLOW);
  }

  private void clearScreen() {
    out.println(
        Ansi.ansi()
            .cursorUp(gameState.size())
            .cursorUp(additionalRedraw)
            .eraseScreen(Ansi.Erase.FORWARD));
    additionalRedraw = 1;
  }

  @Override
  public void play(GameContext gameContext) {
    this.gameContext = gameContext;
    startGame();
  }

  @Override
  public GameType gameType() {
    return GameType.JOTTO;
  }

  @Override
  public int getAttempts() {
    return attempts;
  }

  @Override
  public GameOutcome outcome() {
    return gameOutcome;
  }
}
