package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import static io.github.ludovicianul.words.game.util.ConsoleUtil.formatResult;
import static java.lang.System.*;

@Singleton
public class Wordle implements Game {

  private final StringBuilder GUESS_MATRIX = new StringBuilder();
  private final Set<Character> unmatched = new HashSet<>();
  private int attempts = 1;
  private GameContext gameContext;
  private GameOutcome gameOutcome;

  private void printGuessMatrix() {
    String sequenceFormat =
        Ansi.ansi()
            .fgGreen()
            .bold()
            .a(Ansi.Attribute.UNDERLINE)
            .a("Congrats! Guess sequence:")
            .reset()
            .toString();
    out.println("   ");
    out.println(sequenceFormat);
    out.println(GUESS_MATRIX);
  }

  private void finishGame(boolean guessed) {
    if (guessed) {
      gameOutcome = GameOutcome.SUCCESS;
      printGuessMatrix();
    } else {
      gameOutcome = GameOutcome.FAIL;
      out.println(Ansi.ansi().bold().fgYellow().a("Better luck next time!").reset());
    }
    ConsoleUtil.printSelectedWorDefinition(
        gameContext.getSelectedWord(), gameContext.getLanguage());
  }

  private void match(String word) {
    StringBuilder finalPrint = new StringBuilder();
    Set<Integer> markedIndexes = new HashSet<>();

    for (int i = 0; i < word.length(); i++) {
      int indexOfCurrentChar = nextIndexOf(word.charAt(i), markedIndexes);
      if (gameContext.getSelectedWord().charAt(i) == word.charAt(i)) {
        markedIndexes.add(i);
        finalPrint.append(formatResult(Ansi.Color.GREEN, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.GREEN, ' '));
      } else if (indexOfCurrentChar != -1
          && gameContext.getSelectedWord().charAt(indexOfCurrentChar)
              != word.charAt(indexOfCurrentChar)) {
        markedIndexes.add(indexOfCurrentChar);
        finalPrint.append(formatResult(Ansi.Color.YELLOW, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.YELLOW, ' '));
      } else {
        unmatched.add(word.charAt(i));
        finalPrint.append(formatResult(Ansi.Color.WHITE, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.WHITE, ' '));
      }
    }
    GUESS_MATRIX.append(lineSeparator());
    out.println(finalPrint);
    out.println(
        "Unmatched: " + Ansi.ansi().bold().fgRgb(169, 169, 169).a(unmatched).reset().toString());
    out.println();
  }

  private int nextIndexOf(char currentChar, Set<Integer> markedIndexes) {
    int index = gameContext.getSelectedWord().indexOf(currentChar);

    if (markedIndexes.contains(index)) {
      index = gameContext.getSelectedWord().indexOf(currentChar, index + 1);
    }
    return index;
  }

  @Override
  public void play(GameContext gameContext) {
    this.gameContext = gameContext;
    boolean guessed = false;
    int maxTries = gameContext.getSelectedWord().length() + 1;
    Scanner scanner = new Scanner(in);

    while (attempts <= maxTries && !guessed) {
      out.printf("Attempt %s / %s: %n", attempts, maxTries);
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      if (!gameContext.isValidWord(word)) {
        out.println("Not a valid word!");
        continue;
      }
      guessed = gameContext.isGuessed(word);
      match(word);
      attempts++;
    }

    finishGame(guessed);
  }

  @Override
  public GameType gameType() {
    return GameType.WORDLE;
  }

  @Override
  public int getAttempts() {
    return attempts - 1;
  }

  @Override
  public GameOutcome outcome() {
    return gameOutcome;
  }
}