# Word Guess
Wordle-like command line game with multiple dictionaries and multiple word lengths. Play and learn new words. 
Everytime you fail to guess the selected word, you will get its short definition as well as a link to more detailed explanations.

<p  align="center">
  <img src="./images/game1.png" width="50%"  />
  <img src="./images/game2.png" width="50%" /> 
</p>

# Use
Download the native binary for your platform from the releases page: [https://github.com/ludovicianul/word-guess/releases](https://github.com/ludovicianul/word-guess/releases).

You can also `word-guess` to PATH so that you have available at any time. Example for macOS:

```shell
> cp word-guess-macos /usr/local/bin/wg
```

# Running the game
Word-Guess has 2 built-in dictionaries: English and Romanian. The game can be played with words length between 4 and 9.

```shell
wg [letters] [language] [user_dictionary]
```

When no params are provided, the default is: 5-letter words with the built-in English dictionary.

## Examples:

This will run the game with 5-letter Romanian words.

```shell
> wg 5 ro
```

This will run the game with 6-letter English words.

```shell
> wp 6 en
```

This will run the game with 5-letter words and a user supplied dictionary.

```shell
> wp 6 /Users/word/dictionary.txt
```

# Dictionaries
English dictionary used: [https://www.wordgamedictionary.com/sowpods/download/sowpods.txt](https://www.wordgamedictionary.com/sowpods/download/sowpods.txt).

Romanian dictionary used: [https://dexonline.ro/scrabble](https://dexonline.ro/scrabble).