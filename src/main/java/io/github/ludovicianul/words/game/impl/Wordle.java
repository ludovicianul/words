package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import jakarta.inject.Singleton;
import java.util.*;

import static io.github.ludovicianul.words.game.util.ConsoleUtil.formatResult;
import static java.lang.System.in;
import static java.lang.System.out;

@Singleton
public class Wordle implements Game {

  private final List<String> guessMatrix = new ArrayList<>();
  private final Set<Character> unmatched = new HashSet<>();
  private int additionalRedraw = 1;
  private int attempts = 1;
  private GameContext gameContext;
  private GameOutcome gameOutcome;

  private void initGuessMatrix() {
    for (int i = 0; i < gameContext.getSelectedWord().length() + 1; i++) {
      StringBuilder row = new StringBuilder();
      for (int j = 0; j < gameContext.getSelectedWord().length(); j++) {
        row.append(formatResult(Ansi.Color.WHITE, '▢'));
      }
      guessMatrix.add(row.toString());
    }
  }

  private void printGuessMatrix() {
    guessMatrix.forEach(out::println);
  }

  private void printAttemptsMatrix() {
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
    guessMatrix.stream()
        .limit(attempts - 1)
        .forEach(line -> out.println(line.replaceAll("[A-Z]", " ")));
  }

  private void finishGame(boolean guessed) {
    if (guessed) {
      gameOutcome = GameOutcome.SUCCESS;
      printAttemptsMatrix();
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
      } else if (indexOfCurrentChar != -1
          && gameContext.getSelectedWord().charAt(indexOfCurrentChar)
              != word.charAt(indexOfCurrentChar)) {
        markedIndexes.add(indexOfCurrentChar);
        finalPrint.append(formatResult(Ansi.Color.YELLOW, word.charAt(i)));
      } else {
        unmatched.add(word.charAt(i));
        finalPrint.append(formatResult(Ansi.Color.WHITE, word.charAt(i)));
      }
    }
    guessMatrix.set(attempts - 1, finalPrint.toString());
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
    String failed = "";
    initGuessMatrix();
    printGuessMatrix();

    while (attempts <= maxTries && !guessed) {
      redrawScreen();
      printGuessMatrix();
      printUnmatched();
      printAttempts(maxTries, failed);
      String word = readFromSystemIn(scanner);
      if (!gameContext.isValidWord(word)) {
        failed = " (Not a valid word)";
        continue;
      } else {
        failed = "";
      }
      guessed = gameContext.isGuessed(word);
      match(word);
      attempts++;
    }
    redrawScreen();
    printGuessMatrix();

    finishGame(guessed);
  }

  private void printUnmatched() {
    out.println(
        "Unmatched: " + Ansi.ansi().bold().fgRgb(169, 169, 169).a(unmatched).reset().toString());
    additionalRedraw++;
    out.println();
    additionalRedraw++;
  }

  private String readFromSystemIn(Scanner scanner) {
    String word = scanner.nextLine().toUpperCase(Locale.ROOT);
    additionalRedraw++;
    return word;
  }

  private void printAttempts(int maxTries, String failed) {
    out.printf("Attempt %s / %s%s: %n", attempts, maxTries, failed);
    additionalRedraw++;
  }

  private void redrawScreen() {
    out.println(
        Ansi.ansi()
            .cursorUp(gameContext.getSelectedWord().length() + 1)
            .cursorUp(additionalRedraw)
            .eraseScreen(Ansi.Erase.FORWARD));
    additionalRedraw = 1;
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
