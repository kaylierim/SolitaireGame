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
