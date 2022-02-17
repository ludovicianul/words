package io.github.ludovicianul.words.game;

import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import javax.inject.Singleton;
import java.util.*;

import static java.lang.System.*;

@Singleton
public class Hangman implements Game {

  private static final int MAX_TRIES = 6;
  private final Map<Integer, Boolean> marked = new HashMap<>();
  private final Set<Character> unmatched = new HashSet<>();

  private GameContext gameContext;

  @Override
  public void play(GameContext gameContext) {
    this.gameContext = gameContext;
    boolean guessed = false;
    int tries = 0;
    Scanner scanner = new Scanner(in);

    printState();

    while (tries <= MAX_TRIES && !guessed) {
      out.printf("Remaining tries %s: %n", MAX_TRIES - tries);
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      boolean matched = matchWord(word);
      if (!matched) {
        tries++;
      }
      guessed =
          marked.size() == gameContext.getSelectedWord().length() || gameContext.isGuessed(word);

      if (!guessed) {
        printState();
      }
    }

    finishGame(guessed);
  }

  private void finishGame(boolean guessed) {
    if (guessed) {
      printEndState();
    } else {
      out.println(Ansi.ansi().bold().fgYellow().a("Better luck next time!").reset());
    }
    ConsoleUtil.printSelectedWorDefinition(
        gameContext.getSelectedWord(), gameContext.getLanguage());
  }

  private void printEndState() {
    String congrats =
        Ansi.ansi()
            .fgGreen()
            .bold()
            .a(Ansi.Attribute.UNDERLINE)
            .a("Congrats! Guessed word:")
            .reset()
            .toString();
    out.println(congrats);

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < gameContext.getSelectedWord().length(); i++) {
      builder.append(
          ConsoleUtil.formatResult(Ansi.Color.GREEN, gameContext.getSelectedWord().charAt(i)));
    }
    out.println(builder);
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
  public void saveState() {}
}
