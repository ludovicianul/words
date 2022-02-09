package io.github.ludovicianul.words.game;

import io.github.ludovicianul.words.definition.Language;

import java.util.List;

public class GameContext {

  private List<String> words;
  private String selectedWord;
  private Language language;

  public Language getLanguage() {
    return language;
  }

  public GameContext language(Language language) {
    this.language = language;
    return this;
  }

  public List<String> getWords() {
    return words;
  }

  public GameContext words(List<String> words) {
    this.words = words;
    return this;
  }

  public String getSelectedWord() {
    return selectedWord;
  }

  public GameContext selectedWord(String selectedWord) {
    this.selectedWord = selectedWord;
    return this;
  }
}
