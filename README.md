**Solitaire Game**

This is a Java implementation of a game of Solitaire. To play the game, the user would first enter the desired 
game mode into the command line. There are three different selections of gameplay: Basic Klondike, Limited Draw
Klondike, and Whitehead Klondike. The rules of each of the games are as follows:

| Basic                                            | Limited Draw                          | Whitehead                                        |
|--------------------------------------------------|---------------------------------------|--------------------------------------------------|
| Follows the default rules of a game of solitaire | Limited (specified) amount of redraws | New cascades must be with non-alternating colors |
|                                                  |                                       | Can put a non-King card on an empty pile         |



Changes made to improve my code over the duration of the assignment:
BasicKlondike
- Deleted everything keeping track of the drawPile visibility
- Kept track of visibility within the model (not the card class)
- Kept track of drawPile by using a discardPile as well (for better abstraction)
- Wrote JavaDoc

KlondikeCard
- Improved JavaDoc

KlondikeTextualController
- Explained why I did nothing with the NumberFormatException
- Made playGame under 50 lines long
- Made System.lineSeparator() only appear once# SolitaireGame
