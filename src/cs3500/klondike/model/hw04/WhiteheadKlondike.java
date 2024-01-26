package cs3500.klondike.model.hw04;

import cs3500.klondike.model.KlondikeGame;
import cs3500.klondike.model.hw02.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * WhiteheadKlondike class represents the whitehead version implementation
 * of the Klondike solitaire game and extends KlondikeGame.
 */
public class WhiteheadKlondike extends KlondikeGame {

  /**
   * Constructs a WhiteheadKlondike model.
   */
  public WhiteheadKlondike() {
    super();
  }

  // all helper methods used:
  private void checkIfCardsAreSameColor(Card card1, Card card2) {
    if (card1.isCardRed() != card2.isCardRed()) {
      throw new IllegalStateException();
    }
  }

  private void checkIfAllCardsHaveSameSuit(List<Card> cards) {
    char suit = '♠';
    for (int i = 0; i < cards.size(); i++) {
      String cardString = cards.get(i).toString();
      char cardSuit = cardString.charAt(cardString.length() - 1);
      if (i == 0) {
        suit = cardSuit;
      } else if (cardSuit != suit) {
        throw new IllegalStateException();
      }
    }
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile)
          throws IllegalStateException {
    super.movePile(srcPile, numCards, destPile);
    Stack<Card> sourcePile = cascades.get(srcPile);
    Stack<Card> destinationPile = cascades.get(destPile);
    int startingMoveIndex = sourcePile.size() - numCards;
    if (numCards > 1) {
      List<Card> cardsToMove = new ArrayList<>();
      for (int i = startingMoveIndex; i < sourcePile.size(); i++) {
        cardsToMove.add(sourcePile.get(i));
      }
      checkIfAllCardsHaveSameSuit(cardsToMove);
    } else if (!destinationPile.isEmpty()) {
      // numCards is 1
      checkIfCardsAreSameColor(sourcePile.peek(), destinationPile.peek());
    }

    Stack<Card> newStack = new Stack<>();
    for (int i = 0; i < numCards; i++) {
      Card card = sourcePile.pop();
      newStack.push(card);
    }
    while (!newStack.isEmpty()) {
      destinationPile.push(newStack.pop());
    }
  }

  @Override
  public void moveDraw(int destPile) throws IllegalStateException {
    super.moveDraw(destPile);
    Card card = drawPile.remove(0);
    Stack<Card> destinationPile = cascades.get(destPile);

    if (!destinationPile.isEmpty()) {
      checkIfCardsAreSameColor(card, destinationPile.peek());
    }
    destinationPile.push(card);
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
          throws IllegalStateException {
    super.moveToFoundation(srcPile, foundationPile);
    Stack<Card> sourcePile = cascades.get(srcPile);
    Card card = sourcePile.peek();
    Stack<Card> pile = foundationPiles.get(foundationPile);

    // if this foundation pile is empty, the card added must be an ace
    if (pile.isEmpty()) {
      checkIfCardIsAce(card);
      sourcePile.pop();
      pile.push(card);
      return;
    }
    Card topFoundationPileCard = pile.peek();
    suitMatch(card, topFoundationPileCard);
    if (getValueOfCard(sourcePile.peek()) != getValueOfCard(topFoundationPileCard) + 1) {
      throw new IllegalStateException();
    }
    sourcePile.pop();
    pile.push(card);
  }

  @Override
  public void discardDraw() throws IllegalStateException {
    super.discardDraw();
    if (drawPile.isEmpty()) {
      drawPile = discardPile;
      discardPile = new ArrayList<>();
    }
  }
}
