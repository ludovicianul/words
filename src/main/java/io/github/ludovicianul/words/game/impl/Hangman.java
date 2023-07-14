package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import jakarta.inject.Singleton;
import java.util.*;

import static java.lang.System.*;

@Singleton
public class Hangman implements Game {

  private static final int MAX_TRIES = 6;
  private final Map<Integer, Boolean> marked = new HashMap<>();
  private final Set<Character> unmatched = new HashSet<>();

  private GameContext gameContext;
  private GameOutcome gameOutcome;
  private int attempts;

  @Override
  public void play(GameContext gameContext) {
    this.gameContext = gameContext;
    boolean guessed = false;
    Scanner scanner = new Scanner(in);

    printState();

    while (attempts <= MAX_TRIES && !guessed) {
      out.printf("Remaining tries %s: %n", MAX_TRIES - attempts);
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      boolean matched = matchWord(word);
      if (!matched) {
        attempts++;
      }
      guessed =
          marked.size() == gameContext.getSelectedWord().length() || gameContext.isGuessed(word);

      if (!guessed) {
        clearScreen();
        printState();
      }
    }

    finishGame(guessed);
  }

  private void clearScreen() {
    out.println(Ansi.ansi().cursorUp(6).eraseScreen(Ansi.Erase.FORWARD));
  }

  private void finishGame(boolean guessed) {
    if (guessed) {
      gameOutcome = GameOutcome.SUCCESS;
      printEndState();
    } else {
      gameOutcome = GameOutcome.FAIL;
      out.println(Ansi.ansi().bold().fgYellow().a("Better luck next time!").reset());
    }
    ConsoleUtil.printSelectedWorDefinition(
        gameContext.getSelectedWord(), gameContext.getLanguage());
  }

  private void printEndState() {
    clearScreen();
    printState();
    String congrats =
        Ansi.ansi()
            .fgGreen()
            .bold()
            .a(Ansi.Attribute.UNDERLINE)
            .a("Congrats! You won!")
            .reset()
            .toString();
    out.println(congrats);
  }

  private void printState() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < gameContext.getSelectedWord().length(); i++) {
      if (marked.get(i) != null) {
        builder.append(
            ConsoleUtil.formatResult(Ansi.Color.GREEN, gameContext.getSelectedWord().charAt(i)));
      } else {
        builder.append(ConsoleUtil.formatResult(Ansi.Color.WHITE, '•'));
      }
    }
    builder.append(lineSeparator());
    builder.append(Ansi.ansi().a("Unmatched: ").bold().fgRgb(169, 169, 169).a(unmatched).reset());
    builder.append(lineSeparator());
    out.println(builder);
  }

  private boolean matchWord(String word) {
    boolean matched = false;
    if (word.length() == 1) {
      char input = word.charAt(0);
      for (int i = 0; i < gameContext.getSelectedWord().length(); i++) {
        if (gameContext.getSelectedWord().charAt(i) == input) {
          matched = true;
          marked.put(i, true);
        }
      }
      if (!matched) {
        unmatched.add(input);
      }
    } else {
      matched = gameContext.isGuessed(word);
    }
    return matched;
  }

  @Override
  public GameType gameType() {
    return GameType.HANGMAN;
  }

  @Override
  public int getAttempts() {
    return attempts + 1;
  }

  @Override
  public GameOutcome outcome() {
    return gameOutcome;
  }
}
