package io.github.ludovicianul.words.game;

public interface Game {

  void play(GameContext gameContext);

  GameType gameType();
}
