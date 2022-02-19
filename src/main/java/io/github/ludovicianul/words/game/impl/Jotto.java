package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import javax.inject.Singleton;
import java.util.Locale;
import java.util.Scanner;

import static java.lang.System.lineSeparator;
import static java.lang.System.out;

@Singleton
public class Jotto implements Game {
  private final StringBuilder gameState = new StringBuilder();
  private GameContext gameContext;
  private GameOutcome gameOutcome;
  private int attempts;

  private void startGame() {
    Scanner scanner = new Scanner(System.in);
    boolean guessed = false;
    while (!guessed) {
      attempts++;
      out.printf("Enter you guess. Attempt %s: %n", attempts);
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      if (!gameContext.isValidWord(word)) {
        out.println("Not a valid word!");
        continue;
      }
      guessed = gameContext.isGuessed(word);
      gameState.append(formatWord(word));
      gameState.append(System.lineSeparator());
      out.println(gameState);
    }
    finishGame();
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