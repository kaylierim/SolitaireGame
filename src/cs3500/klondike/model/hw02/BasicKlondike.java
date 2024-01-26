package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cs3500.klondike.model.KlondikeGame;

/**
 * BasicKlondike class represents a basic version implementation
 * of the Klondike solitaire game and extends KlondikeGame.
 */
public class BasicKlondike extends KlondikeGame {

  /**
   * Constructs a BasicKlondike model.
   */
  public BasicKlondike() {
    super();
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException {
    super.startGame(deck, shuffle, numPiles, numDraw);
    for (int i = 0; i < numPiles; i++) {
      visibilityIndex[i] = i;
    }
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile)
          throws IllegalStateException {
    super.movePile(srcPile, numCards, destPile);
    Stack<Card> sourcePile = cascades.get(srcPile);
    Stack<Card> destinationPile = cascades.get(destPile);
    int startingMoveIndex = sourcePile.size() - numCards;
    if (startingMoveIndex < visibilityIndex[srcPile]) {
      throw new IllegalArgumentException("Invalid number of cards");
    }

    if (destinationPile.isEmpty()) {
      if (sourcePile.get(startingMoveIndex).toString().charAt(0) != 'K') {
        throw new IllegalStateException("Cannot move non-king to empty pile");
      }
    } else {
      checkIfCardsAreConsecutiveAlternating(sourcePile.get(startingMoveIndex),
              destinationPile.peek());
    }
    Stack<Card> newStack = new Stack<>();
    for (int i = 0; i < numCards; i++) {
      Card card = sourcePile.pop();
      newStack.push(card);
    }
    while (!newStack.isEmpty()) {
      destinationPile.push(newStack.pop());
    }

    // makes last card of source pile visible after moving the ones on top
    if (!sourcePile.isEmpty()) {
      visibilityIndex[srcPile]--;
    }
  }

  @Override
  public void moveDraw(int destPile) throws IllegalStateException {
    super.moveDraw(destPile);
    Card card = drawPile.remove(0);
    Stack<Card> destinationPile = cascades.get(destPile);

    if (destinationPile.isEmpty()) {
      if (card.toString().charAt(0) != 'K') {
        throw new IllegalStateException("Cannot move non-king to empty pile");
      }
    } else {
      checkIfCardsAreConsecutiveAlternating(card, destinationPile.peek());
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
      if (!sourcePile.isEmpty()) {
        visibilityIndex[srcPile]--;
      }
      return;
    }
    Card topFoundationPileCard = pile.peek();
    suitMatch(card, topFoundationPileCard);
    if (getValueOfCard(sourcePile.peek()) != getValueOfCard(topFoundationPileCard) + 1) {
      throw new IllegalStateException();
    }
    sourcePile.pop();
    pile.push(card);

    // makes last card of source pile visible after moving the one on top
    if (!sourcePile.isEmpty()) {
      visibilityIndex[srcPile]--;
    }
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
