package cs3500.klondike.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeCard;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * Abstract class of a Klondike game that implements KlondikeModel.
 */
public abstract class KlondikeGame implements KlondikeModel {
  protected final List<Stack<Card>> cascades;
  protected final List<Stack<Card>> foundationPiles;
  protected List<Card> drawPile;
  // "recycles" discard pile into draw pile when draw
  // pile is empty, and when it is allowed by the model
  protected List<Card> discardPile;
  protected List<Card> deck;
  protected boolean hasGameStarted;
  protected int maxNumDrawCards;
  // visibility index for each pile,
  // when visibilityIndex == 0, all cards are visible in that pile
  // if visibilityIndex == 5, all cards from index 5 and on are visible
  protected int[] visibilityIndex;

  /**
   * Constructs a Klondike model.
   */
  public KlondikeGame() {
    cascades = new ArrayList<>();
    foundationPiles = new ArrayList<>();
    drawPile = new ArrayList<>();
    discardPile = new ArrayList<>();
    hasGameStarted = false;
    String standardDeck = "A♠, 2♠, 3♠, 4♠, 5♠, 6♠, 7♠, 8♠, 9♠, 10♠, J♠, Q♠, K♠, "
            + "A♡, 2♡, 3♡, 4♡, 5♡, 6♡, 7♡, 8♡, 9♡, 10♡, J♡, Q♡, K♡, "
            + "A♢, 2♢, 3♢, 4♢, 5♢, 6♢, 7♢, 8♢, 9♢, 10♢, J♢, Q♢, K♢, "
            + "A♣, 2♣, 3♣, 4♣, 5♣, 6♣, 7♣, 8♣, 9♣, 10♣, J♣, Q♣, K♣";
    String[] deck = standardDeck.split(", ");
    List<Card> newDeck = new ArrayList<>();
    for (String card: deck) {
      newDeck.add(new KlondikeCard(card));
    }
    this.deck = newDeck;
  }

  // all helper methods used:
  private static List<List<Card>> collectAces(List<Card> deck) {
    List<List<Card>> suitPiles = new ArrayList<>();
    for (int i = 0; i < deck.size(); i++) {
      Card card = deck.get(i);
      if (card.toString().startsWith("A")) {
        suitPiles.add(new ArrayList<>(List.of(card)));
        deck.set(i, null);
      }
    }
    deck.removeIf(Objects::isNull);
    return suitPiles;
  }

  private static String convertValueToCardString(int value) {
    switch (value) {
      case 13:
        return "K";
      case 12:
        return "Q";
      case 11:
        return "J";
      default:
        return String.valueOf(value);
    }
  }

  private static void collectSuitPile(List<Card> suitPile, List<Card> deck) {
    char currentSuit = suitPile.get(0).toString().charAt(1);
    boolean found = false;
    for (int value = 2; value < 14; value++) {
      String cardString = convertValueToCardString(value) + currentSuit;
      for (int i = 0; i < deck.size(); i++) {
        Card card = deck.get(i);
        if (cardString.equals(card.toString())) {
          suitPile.add(card);
          deck.remove(i);
          found = true;
          break;
        }
      }
      if (!found) {
        break;
      }
    }
  }

  protected void checkDeckValidity(List<Card> deck) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null");
    }

    for (Card card : deck) {
      if (card == null) {
        throw new IllegalArgumentException();
      }
    }

    List<Card> deckCopy = new ArrayList<>(deck);
    List<List<Card>> suitPiles = collectAces(deckCopy);
    for (List<Card> suitPile : suitPiles) {
      collectSuitPile(suitPile, deckCopy);
    }
    // makes sure each suit pile is the same size
    int size = -1;
    for (List<Card> suitPile : suitPiles) {
      if (size == -1) {
        size = suitPile.size();
      } else if (suitPile.size() != size) {
        throw new IllegalArgumentException("Inconsistent runs");
      }
    }
    // if there are left over cards, the deck is invalid
    if (!deckCopy.isEmpty()) {
      throw new IllegalArgumentException("Inconsistent runs");
    }
  }

  protected static int checkNumPilesValidity(int numPiles, int deckSize) {
    if (numPiles <= 0) {
      throw new IllegalArgumentException();
    }
    int minDeckSize = 0;
    for (int i = 1; i <= numPiles; i++) {
      minDeckSize += i;
    }
    if (minDeckSize > deckSize) {
      throw new IllegalArgumentException("Not enough cards in deck for " + numPiles + " piles.");
    }
    return minDeckSize;
  }

  private int countNumAces(List<Card> deck) {
    int count = 0;
    for (Card card : deck) {
      if (card.toString().startsWith("A")) {
        count++;
      }
    }
    return count;
  }

  protected void checkIfGameStarted() {
    if (!hasGameStarted) {
      throw new IllegalStateException("Game hasn't started");
    }
  }

  private void checkNumDrawValidity(int numDraw) {
    if (numDraw <= 0) {
      throw new IllegalArgumentException();
    }
  }

  protected void checkIfCardsAreConsecutiveAlternating(Card srcCard, Card destCard) {
    boolean srcCardRed = destCard.isCardRed();
    boolean destCardRed = srcCard.isCardRed();
    if (srcCardRed == destCardRed) {
      throw new IllegalStateException("Invalid move");
    }
    if (getValueOfCard(srcCard) + 1 != getValueOfCard(destCard)) {
      throw new IllegalStateException("Invalid move");
    }
  }

  private void checkPileNumValidity(int pileNum) {
    if (pileNum < 0) {
      throw new IllegalArgumentException("Pile number cannot be negative");
    } else if (pileNum >= cascades.size()) {
      throw new IllegalArgumentException("Invalid pile number");
    }
  }


  private void removeAndAddToFoundationPile(Stack<Card> foundationPile, Card card) {
    // remove from draw pile
    drawPile.remove(0);
    foundationPile.push(card);
  }

  private void checkFoundationPileNumValidity(int pileNum) {
    if (pileNum < 0) {
      throw new IllegalArgumentException("Pile number cannot be negative");
    } else if (pileNum >= foundationPiles.size()) {
      throw new IllegalArgumentException("Invalid pile number");
    }
  }

  protected int getValueOfCard(Card card) {
    if (card.toString().length() == 3) {
      return 10;
    }
    char value = card.toString().charAt(0);
    switch (value) {
      case 'A':
        return 1;
      case 'K':
        return 13;
      case 'Q':
        return 12;
      case 'J':
        return 11;
      default:
        return Character.digit(value, 10);
    }
  }

  protected void suitMatch(Card card1, Card card2) {
    String card1String = card1.toString();
    String card2String = card2.toString();
    char suit1 = card1String.charAt(card1String.length() - 1);
    char suit2 = card2String.charAt(card2String.length() - 1);
    if (suit1 != suit2) {
      throw new IllegalStateException();
    }
  }

  protected void checkIfCardIsAce(Card card) {
    if (getValueOfCard(card) != 1) {
      throw new IllegalStateException();
    }
  }

  @Override
  public List<Card> getDeck() {
    return deck;
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException {
    if (hasGameStarted) {
      throw new IllegalStateException();
    }

    // makes sure deck, numPiles, and numDraw are valid
    checkDeckValidity(deck);
    int numCardsInCascade = checkNumPilesValidity(numPiles, deck.size());
    checkNumDrawValidity(numDraw);

    // shuffles the deck if shuffle is true
    if (shuffle) {
      Collections.shuffle(deck);
    }
    this.deck = deck;

    // initializes number of foundation piles according to the number of aces
    for (int i = 0; i < countNumAces(deck); i++) {
      foundationPiles.add(new Stack<>());
    }

    // make cascade
    // initializes how many piles needed
    for (int i = 0; i < numPiles; i++) {
      cascades.add(new Stack<>());
    }

    // puts cards in respective stacks
    // row starts at 0 index
    int row = 0;
    int cardIndex = 0;

    while (cardIndex < numCardsInCascade) {
      for (int j = row; j < numPiles; j++) {
        Card card = deck.get(cardIndex);
        cascades.get(j).push(card);
        cardIndex++;
      }
      row++;
    }

    // initialize visibilityIndex
    visibilityIndex = new int[numPiles];

    // make draw pile
    for (int i = numCardsInCascade; i < deck.size(); i++) {
      drawPile.add(deck.get(i));
    }

    maxNumDrawCards = numDraw;
    hasGameStarted = true;
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile)
          throws IllegalStateException {
    checkIfGameStarted();
    if (srcPile == destPile) {
      throw new IllegalArgumentException("Source pile and destination pile cannot be the same");
    }
    checkPileNumValidity(srcPile);
    checkPileNumValidity(destPile);
    if (numCards == 0) {
      throw new IllegalArgumentException("Must move at least one card");
    }
    int startingMoveIndex = cascades.get(srcPile).size() - numCards;
    if (startingMoveIndex < 0) {
      throw new IllegalArgumentException("Invalid number of cards");
    }
  }

  @Override
  public void moveDraw(int destPile) throws IllegalStateException {
    checkIfGameStarted();
    checkPileNumValidity(destPile);
    if (drawPile.isEmpty()) {
      throw new IllegalStateException();
    }
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
          throws IllegalStateException {
    checkIfGameStarted();
    checkPileNumValidity(srcPile);
    checkFoundationPileNumValidity(foundationPile);
    if (cascades.get(srcPile).isEmpty()) {
      throw new IllegalStateException();
    }
  }

  @Override
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    checkIfGameStarted();
    checkFoundationPileNumValidity(foundationPile);
    if (drawPile.isEmpty()) {
      throw new IllegalStateException();
    }
    Card card = drawPile.get(0);
    Stack<Card> pile = foundationPiles.get(foundationPile);
    if (pile.isEmpty()) {
      checkIfCardIsAce(card);
      removeAndAddToFoundationPile(pile, card);
      return;
    }
    Card topFoundationPileCard = pile.peek();
    suitMatch(card, topFoundationPileCard);
    if (getValueOfCard(card) != getValueOfCard(topFoundationPileCard) + 1) {
      throw new IllegalStateException();
    }
    removeAndAddToFoundationPile(pile, card);
  }

  @Override
  public void discardDraw() throws IllegalStateException {
    checkIfGameStarted();
    if (drawPile.isEmpty()) {
      throw new IllegalStateException();
    }
    Card card = drawPile.remove(0);
    discardPile.add(card);
  }

  @Override
  public int getNumRows() {
    checkIfGameStarted();
    int maxHeight = 0;
    for (Stack<Card> cascade : cascades) {
      int size = cascade.size();
      if (maxHeight < size) {
        maxHeight = size;
      }
    }
    return maxHeight;
  }

  @Override
  public int getNumPiles() {
    checkIfGameStarted();
    return cascades.size();
  }

  @Override
  public int getNumDraw() {
    checkIfGameStarted();
    return maxNumDrawCards;
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    checkIfGameStarted();
    return drawPile.isEmpty();
  }

  @Override
  public int getScore() throws IllegalStateException {
    checkIfGameStarted();
    int score = 0;
    for (Stack<Card> pile : foundationPiles) {
      if (pile.isEmpty()) {
        break;
      }
      score += pile.size();
    }
    return score;
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    checkIfGameStarted();
    checkPileNumValidity(pileNum);
    return cascades.get(pileNum).size();
  }

  private void checkValidCardIndexInPile(int card, List<Card> pile) {
    if (card < 0 || card >= pile.size()) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    checkIfGameStarted();
    checkPileNumValidity(pileNum);
    Stack<Card> pile = cascades.get(pileNum);
    checkValidCardIndexInPile(card, pile);
    return visibilityIndex[pileNum] <= card;
  }

  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    checkIfGameStarted();
    checkPileNumValidity(pileNum);
    Stack<Card> pile = cascades.get(pileNum);
    checkValidCardIndexInPile(card, pile);
    Card specifiedCard = pile.get(card);
    if (!isCardVisible(pileNum, card)) {
      throw new IllegalArgumentException();
    }
    return specifiedCard;
  }

  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {
    checkIfGameStarted();
    checkFoundationPileNumValidity(foundationPile);
    if (foundationPiles.get(foundationPile).isEmpty()) {
      return null;
    }
    return foundationPiles.get(foundationPile).peek();
  }

  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    checkIfGameStarted();
    List<Card> drawCards = new ArrayList<>();
    for (int i = 0; i < maxNumDrawCards; i++) {
      if (drawPile.size() < i + 1) {
        for (int j = 0; j < maxNumDrawCards - i; j++) {
          if (discardPile.size() > j) {
            drawCards.add(discardPile.get(j));
          }
        }
        break;
      }
      drawCards.add(drawPile.get(i));
    }
    return drawCards;
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    checkIfGameStarted();
    return foundationPiles.size();
  }
}
