package io.github.ludovicianul.words.game.impl;

import io.github.ludovicianul.words.game.Game;
import io.github.ludovicianul.words.game.GameContext;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.util.ConsoleUtil;
import org.fusesource.jansi.Ansi;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.in;
import static java.lang.System.out;

@Singleton
public class ThreeWords implements Game {
  private static final char WILD_CHAR = '•';
  private static final int NO_OF_WORDS = 3;

  private final List<String> randomWords = new ArrayList<>();
  private final List<String> obfuscatedWords = new ArrayList<>();
  private final List<List<Character>> allPermutations = new ArrayList<>();
  private final List<List<String>> allWordsCombinations = new ArrayList<>();

  private final List<Character> removedChars = new ArrayList<>();
  private final List<Integer> removedIndexes = new ArrayList<>();
  private final List<Character> shuffledRemovedChars = new ArrayList<>();
  private GameContext gameContext;
  private GameOutcome gameOutcome;
  private int attempts;

  private void generateWords() {
    for (int i = 0; i < NO_OF_WORDS; i++) {
      randomWords.add(
          gameContext
              .getWords()
              .get(ThreadLocalRandom.current().nextInt(gameContext.getWords().size())));
    }
  }

  private void removeChars() {
    for (String word : randomWords) {
      for (int i = 0; i < gameContext.getRemovedLetters(); i++) {
        int currentIndex =
            ThreadLocalRandom.current().nextInt(gameContext.getSelectedWord().length());
        while (word.charAt(currentIndex) == WILD_CHAR) {
          currentIndex =
              ThreadLocalRandom.current().nextInt(gameContext.getSelectedWord().length());
        }
        removedIndexes.add(currentIndex);
        removedChars.add(word.charAt(currentIndex));
        word = this.replaceChar(word, currentIndex, WILD_CHAR);
      }
      obfuscatedWords.add(word);
    }
  }

  private String replaceChar(String inputString, int atIndex, char withChar) {
    StringBuilder builder = new StringBuilder(inputString);
    builder.setCharAt(atIndex, withChar);
    return builder.toString();
  }

  private void printObfuscatedWords() {
    obfuscatedWords.forEach(
        word -> System.out.println(ConsoleUtil.formatString(word, Ansi.Color.WHITE)));
    System.out.println();
  }

  private void createWordCombinations() {
    this.getAllRec(removedChars.size(), new ArrayList<>(removedChars), allPermutations);
    for (List<Character> permutation : allPermutations) {
      List<String> candidate = new ArrayList<>();
      for (int i = 0; i < NO_OF_WORDS; i++) {
        String word = obfuscatedWords.get(i);
        for (int j = 0; j < gameContext.getRemovedLetters(); j++) {
          word =
              this.replaceChar(
                  word,
                  removedIndexes.get(i * gameContext.getRemovedLetters() + j),
                  permutation.get(i * gameContext.getRemovedLetters() + j));
        }
        if (gameContext.getWords().contains(word)) {
          candidate.add(word);
        }
      }
      if (candidate.size() == NO_OF_WORDS) {
        allWordsCombinations.add(candidate);
      }
    }
  }

  public void getAllRec(int n, List<Character> elements, List<List<Character>> results) {
    if (n == 1) {
      results.add(new ArrayList<>(elements));
    } else {
      for (int i = 0; i < n - 1; i++) {
        this.getAllRec(n - 1, elements, results);
        if (n % 2 == 0) {
          Collections.swap(elements, i, n - 1);
        } else {
          Collections.swap(elements, 0, n - 1);
        }
      }
      this.getAllRec(n - 1, elements, results);
    }
  }

  private void startGame() {
    long startTime = System.currentTimeMillis();
    List<String> guessedAndObfuscated = new ArrayList<>(obfuscatedWords);
    List<String> guessedWords = new ArrayList<>();
    List<Character> guessedChars = new ArrayList<>();
    List<String> candidate = Collections.emptyList();

    Scanner scanner = new Scanner(in);

    while (guessedWords.size() < NO_OF_WORDS) {
      attempts++;
      out.println("Enter your guess: ");
      String word = scanner.nextLine().toUpperCase(Locale.ROOT);
      Optional<List<String>> possibleCandidate =
          allWordsCombinations.stream()
              .filter(
                  permutation ->
                      permutation.contains(word) && permutation.containsAll(guessedWords))
              .findFirst();
      if (possibleCandidate.isPresent()) {
        guessedWords.add(word);
        int indexOfWord = possibleCandidate.get().indexOf(word);
        guessedChars.addAll(this.getGuessedChars(indexOfWord));
        guessedAndObfuscated.set(indexOfWord, word);
        candidate = possibleCandidate.get();
      } else {
        out.println("Not a valid combination!");
      }
      printState(guessedAndObfuscated);
      printRemainingLetters(guessedChars);
      out.println();
    }

    finishGame(startTime, candidate);
  }

  private void finishGame(long startTime, List<String> candidate) {
    gameOutcome = GameOutcome.SUCCESS;
    out.println(
        Ansi.ansi()
            .bold()
            .fgGreen()
            .a("Congrats! You finished in: ")
            .a(((System.currentTimeMillis() - startTime) / 1000))
            .a(" seconds")
            .reset());
    candidate.forEach(
        word -> ConsoleUtil.printSelectedWorDefinition(word, gameContext.getLanguage()));
  }

  public void printRemainingLetters(List<Character> guessedChars) {
    List<Character> remaining = new ArrayList<>(shuffledRemovedChars);
    for (Character character : guessedChars) {
      remaining.remove(character);
    }
    out.println(
        "Remaining letters: " + Ansi.ansi().bold().fgRgb(169, 169, 169).a(remaining).reset());
  }

  private void printState(List<String> guessedAndObfuscated) {
    for (String word : guessedAndObfuscated) {
      if (word.indexOf(WILD_CHAR) != -1) {
        out.println(ConsoleUtil.formatString(word, Ansi.Color.WHITE));
      } else {
        out.println(ConsoleUtil.formatString(word, Ansi.Color.GREEN));
      }
    }
  }

  private List<Character> getGuessedChars(int index) {
    List<Character> characters = new ArrayList<>();
    for (int i = 0; i < gameContext.getRemovedLetters(); i++) {
      characters.add(removedChars.get(index * gameContext.getRemovedLetters() + i));
    }
    return characters;
  }

  private void shuffleAndPrintRemovedLetters() {
    shuffledRemovedChars.addAll(removedChars);
    Collections.shuffle(shuffledRemovedChars);
    System.out.println(
        "Available letters: "
            + Ansi.ansi().bold().fgRgb(169, 169, 169).a(shuffledRemovedChars).reset());
  }

  @Override
  public void play(GameContext gameContext) {
    this.gameContext = gameContext;
    generateWords();
    removeChars();
    createWordCombinations();
    printObfuscatedWords();
    shuffleAndPrintRemovedLetters();
    startGame();
  }

  @Override
  public GameType gameType() {
    return GameType.THREE_WORDS;
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
