package io.github.ludovicianul.words.game.util;

import io.github.ludovicianul.words.definition.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FileUtils {
  private static InputStream getWordsInputStream(Language language) {
    return Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream("words_" + language.name().toLowerCase(Locale.ROOT) + ".txt");
  }

  public static List<String> loadBuiltInDictionary(Language language) throws IOException {
    try (InputStream wordsStream = getWordsInputStream(language);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(wordsStream)))) {
      return reader.lines().collect(Collectors.toList());
    }
  }
}
