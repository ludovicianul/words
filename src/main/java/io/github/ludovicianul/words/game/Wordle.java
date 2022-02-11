package io.github.ludovicianul.words.game;

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
  private int counter = 1;
  private final Set<Character> unmatched = new HashSet<>();
  private GameContext gameContext;

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

  private boolean isValidWord(String word) {
    return word.length() == gameContext.getSelectedWord().length()
        && gameContext.getWords().contains(word.toLowerCase(Locale.ROOT));
  }

  private boolean isGuessed(String word) {
    return gameContext.getSelectedWord().equalsIgnoreCase(word);
  }

  private void finishGame(boolean guessed) {
    if (guessed) {
      printGuessMatrix();
    } else {
      ConsoleUtil.printSelectedWorDefinition(gameContext);
    }
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
    out.println("Unmatched: " + Ansi.ansi().bold().fgRgb(169,169,169).a(unmatched).reset().toString());
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

    while (counter <= maxTries && !guessed) {
      Scanner scanner = new Scanner(in);
      out.printf("Attempt %s / %s: %n", counter, maxTries);
      String word = scanner.nextLine();
      if (!isValidWord(word)) {
        out.println("Not a valid word!");
        continue;
      }
      guessed = isGuessed(word);
      match(word.toLowerCase(Locale.ROOT));
      counter++;
    }

    finishGame(guessed);
  }

  @Override
  public GameType gameType() {
    return GameType.WORDLE;
  }
}
