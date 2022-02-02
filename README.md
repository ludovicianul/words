# Word Guess
Wordle-like game with multiple dictionaries and multiple word lengths.

# Use
Download the native binary for your platform from the releases page: 

Add it to PATH. Example for macOS:

```shell
> cp word-guess-macos /usr/local/bin/wg
```

# Running the game
You can use 2 dictionaries: English and Romanian and you can play using multiple word lengths.

```shell
> wg 5 ro
```

This will run the game with 5-letter Romanian words.

```shell
> wp 6 en
```

This will run the game with 6-letter English words.

Minimum number of letters is 4 and maximum is 9.

# Dictionaries
English dictionary used from here: [https://github.com/dwyl/english-words](https://github.com/dwyl/english-words).

Romanian dictionary used from here: [https://dexonline.ro/scrabble](https://dexonline.ro/scrabble).