package io.github.ludovicianul.word.guess;

import static java.lang.System.in;
import static java.lang.System.lineSeparator;
import static java.lang.System.out;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import org.fusesource.jansi.Ansi;

@QuarkusMain
public class WordGuess implements QuarkusApplication {

  private static final Map<Integer, Integer> MAX_TRIES = Map.of(4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10);
  private static final StringBuilder GUESS_MATRIX = new StringBuilder();

  private static List<String> words = new ArrayList<>();
  private static int counter = 1;
  private static String selectedWord;
  private static int wordSize = 5;
  private static String language = "en";

  private static InputStream getWordsInputStream() {
    InputStream wordsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("words_" + language + ".txt");
    if (wordsStream == null) {
      wordsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("words_en.txt");
    }
    return wordsStream;
  }

  private static void loadWords(String... args) throws IOException {
    if (args != null && args.length == 2 && args[1].length() != 2) {
      loadUserProvidedDictionary(args[1]);
    } else {
      loadBuiltInDictionary();
    }

    printStartingLines();
  }

  private static void printStartingLines() {
    String lettersFormat = Ansi.ansi().fgGreen().bold().a(wordSize + " letter").reset().toString();
    String maxTriesFormat = Ansi.ansi().fgYellow().bold().a(MAX_TRIES.get(wordSize)).reset().toString();
    String languageFormat = Ansi.ansi().fgYellow().bold().a(language).reset().toString();
    String wordsFormat = Ansi.ansi().fgGreen().bold().a(words.size()).reset().toString();

    out.printf("Playing with %s words.%n", lettersFormat);
    out.printf("Max tries: %s. Language: %s. %n", maxTriesFormat, languageFormat);
    out.printf("%s words loaded. %n%n", wordsFormat);
  }

  private static void loadUserProvidedDictionary(String file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      words = reader.lines().filter(word -> word.length() == wordSize).collect(Collectors.toList());
      language = "user provided";
    } catch (IOException e) {
      System.err.printf("Could not load user provided dictionary %s: %s %n", file, e.getMessage());
    }
  }

  private static void loadBuiltInDictionary() throws IOException {
    try (InputStream wordsStream = getWordsInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(wordsStream)))) {
      words = reader.lines().filter(word -> word.length() == wordSize).collect(Collectors.toList());
    }
  }

  private static void selectWord() {
    SecureRandom random = new SecureRandom();
    int item = random.nextInt(words.size());
    selectedWord = words.get(item);
  }

  private static void setWordSizeAndLanguage(String... args) {
    try {
      if (args != null) {
        wordSize = Integer.parseInt(args[0]);
      }
      if (args != null && args.length == 2 && args[1].trim().length() == 2) {
        language = args[1].toLowerCase(Locale.ROOT).trim();
      }
      if (wordSize > 9 || wordSize < 4) {
        out.println("Length not supported. Defaulting to 5");
        wordSize = 5;
      }
    } catch (Exception e) {
      out.println("Invalid word size. Defaulting to 5 and language en");
    }
  }

  private static void playGame(String[] args) throws IOException {
    setWordSizeAndLanguage(args);
    loadWords(args);
    selectWord();
    startGame();
  }

  private static void startGame() {
    boolean guessed = false;
    int maxTries = MAX_TRIES.get(wordSize);

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

  private static void finishGame(boolean guessed) {
    if (guessed) {
      printGuessMatrix();
    } else {
      String selectedWordFormat = Ansi.ansi().fgYellow().bold().a(selectedWord).reset().toString();
      String wordDefinition = Ansi.ansi().a(Ansi.Attribute.INTENSITY_FAINT).a(WordDefinition.getDefinition(selectedWord, language)).reset().toString();

      out.printf("%nBetter luck next time. Selected word was: %s - %s%n", selectedWordFormat, wordDefinition);
    }
  }

  private static void printGuessMatrix() {
    String sequenceFormat = Ansi.ansi().fgGreen().bold().a(Ansi.Attribute.UNDERLINE).a("Congrats! Guess sequence:").reset().toString();
    out.println("   ");
    out.println(sequenceFormat);
    out.println(GUESS_MATRIX);
  }

  private static boolean isValidWord(String word) {
    return word.length() == selectedWord.length() && words.contains(word.toLowerCase(Locale.ROOT));
  }

  private static void match(String word) {
    StringBuilder finalPrint = new StringBuilder();
    Set<Integer> markedIndexes = new HashSet<>();

    for (int i = 0; i < word.length(); i++) {
      int indexOfCurrentChar = nextIndexOf(word.charAt(i), markedIndexes);
      if (selectedWord.charAt(i) == word.charAt(i)) {
        markedIndexes.clear();
        markedIndexes.add(i);
        finalPrint.append(formatResult(Ansi.Color.GREEN, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.GREEN, ' '));
      } else if (indexOfCurrentChar != -1 && selectedWord.charAt(indexOfCurrentChar) != word.charAt(indexOfCurrentChar)) {
        markedIndexes.add(indexOfCurrentChar);
        finalPrint.append(formatResult(Ansi.Color.YELLOW, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.YELLOW, ' '));
      } else {
        finalPrint.append(formatResult(Ansi.Color.WHITE, word.charAt(i)));
        GUESS_MATRIX.append(formatResult(Ansi.Color.WHITE, ' '));
      }
    }
    GUESS_MATRIX.append(lineSeparator());
    out.println(finalPrint);
  }

  private static int nextIndexOf(char currentChar, Set<Integer> markedIndexes) {
    int index = selectedWord.indexOf(currentChar);

    if (markedIndexes.contains(index)) {
      index = selectedWord.indexOf(currentChar, index + 1);
    }
    return index;
  }

  private static String formatResult(Ansi.Color color, char currentChar) {
    return Ansi.ansi().fgBlack().bg(color).bold().a((" " + currentChar + " ").toUpperCase(Locale.ROOT)).reset().toString();
  }

  private static boolean isGuessed(String word) {
    return selectedWord.equalsIgnoreCase(word);
  }

  @Override
  public int run(String... args) throws Exception {
    playGame(args);
    return 0;
  }

}
