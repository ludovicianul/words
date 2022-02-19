package io.github.ludovicianul.words.game.stats;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.GameOutcome;
import io.github.ludovicianul.words.game.GameType;

import java.util.Objects;

public class Stats {
  private int total;
  private int fails;
  private int bestMoves;
  private long bestTime;
  private GameType gameType;
  private int wordLength;
  private Language language;

  public int getFails() {
    return fails;
  }

  public Stats fails(int fails) {
    this.fails = fails;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stats stats = (Stats) o;
    return wordLength == stats.wordLength
        && gameType == stats.gameType
        && language == stats.language;
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameType, wordLength, language);
  }

  public Stats increaseTotal() {
    total = total + 1;
    return this;
  }

  public boolean increaseFails(GameOutcome outcome) {
    if (outcome == GameOutcome.FAIL) {
      fails = fails + 1;
      return true;
    }
    return false;
  }

  public GameType getGameType() {
    return gameType;
  }

  public Stats gameType(GameType gameType) {
    this.gameType = gameType;
    return this;
  }

  public int getWordLength() {
    return wordLength;
  }

  public Stats wordLength(int wordLength) {
    this.wordLength = wordLength;
    return this;
  }

  public Language getLanguage() {
    return language;
  }

  public Stats language(Language language) {
    this.language = language;
    return this;
  }

  public int getTotal() {
    return total;
  }

  public Stats total(int total) {
    this.total = total;
    return this;
  }

  public int getBestMoves() {
    return bestMoves;
  }

  public Stats bestMoves(int bestMoves) {
    this.bestMoves = bestMoves;
    return this;
  }

  public long getBestTime() {
    return bestTime;
  }

  public Stats bestTime(long bestTime) {
    this.bestTime = bestTime;
    return this;
  }

  public boolean updateBestTime(long time) {
    if (time < bestTime) {
      this.bestTime = time;
      return true;
    }
    return false;
  }

  public boolean updateBestMoves(int moves) {
    if (moves < this.bestMoves) {
      this.bestMoves = moves;
      return true;
    }
    return false;
  }
}
