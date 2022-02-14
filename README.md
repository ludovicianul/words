# Words
`Words` is a set of command line word-based puzzle games. Best way to spend those minutes within the CLI while your builds are running ;).  

Each game has multiple dictionaries and word lengths. With `Words` you will also learn new words.At the end of each game you will get its short definition as well as a link to more detailed explanations.

<p  align="center">
  <img src="images/all_games.png" />
</p>

# Use
Download the native binary for your platform from the releases page: [https://github.com/ludovicianul/words/releases](https://github.com/ludovicianul/words/releases).

You can also add `words` to PATH so that you have it available at any time. Example for macOS:

```shell
> cp words-macos /usr/local/bin/words
```

# Running the game
`Words` has (currently) 3 built-in games: Wordle, Hangman and Three_Words. Each of these games have 2 built-in dictionaries: English and Romanian. The games can be played with words length between 4 and 15.

```shell
words -w [letters] -l [language] -d [user_dictionary] -g [game]
```

When no params are provided, the default is: Wordle with 5-letter words and the built-in English dictionary.

## Examples:

This will run Wordle with 5-letter Romanian words.

```shell
> words -w 5 -l ro
```

This will run Hangman with 6-letter English words.

```shell
> words -w 6 -l en -g hangman
```

This will run Wordle with 6-letter words and a user supplied dictionary.

```shell
> words -w 6 -d /Users/word/dictionary.txt
```

# Playing Wordle
Based on Wordle gameplay: [https://en.wikipedia.org/wiki/Wordle](https://en.wikipedia.org/wiki/Wordle).

# Playing Hangman
Based on Hangman gameplay: [https://en.wikipedia.org/wiki/Hangman_(game)](https://en.wikipedia.org/wiki/Hangman_(game))

# Playing Three_Words
For this one you are given 3 words. From each word a number of letters have been removed (we call these words obfuscated) and added to a single shuffled set (available letters).
The objective is to reconstruct those initial 3 words. The game accepts any combination that can be made using the obfuscated words and available letters.

# Dictionaries
English dictionary used: [https://www.wordgamedictionary.com/sowpods/download/sowpods.txt](https://www.wordgamedictionary.com/sowpods/download/sowpods.txt).

Romanian dictionary used: [https://dexonline.ro/scrabble](https://dexonline.ro/scrabble).