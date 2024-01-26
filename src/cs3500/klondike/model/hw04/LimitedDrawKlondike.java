package cs3500.klondike.model.hw04;

import cs3500.klondike.model.KlondikeGame;
import cs3500.klondike.model.hw02.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * LimitedDrawKlondike class represents the limited draw version implementation
 * of the Klondike solitaire game and extends KlondikeGame.
 */
public class LimitedDrawKlondike extends KlondikeGame {

  private final int numTimesRedrawAllowed;
  private int numTimesRecycledDrawPile;

  /**
   * Constructs a LimitedDrawKlondike model with the amount of times allowed to redraw.
   */
  public LimitedDrawKlondike(int numTimesRedrawAllowed) {
    super();
    if (numTimesRedrawAllowed < 0) {
      throw new IllegalArgumentException();
    }
    this.numTimesRedrawAllowed = numTimesRedrawAllowed;
    numTimesRecycledDrawPile = 0;
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
    if (drawPile.isEmpty() && numTimesRecycledDrawPile < numTimesRedrawAllowed) {
      drawPile = discardPile;
      discardPile = new ArrayList<>();
      numTimesRecycledDrawPile++;
    }
  }

  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    checkIfGameStarted();
    List<Card> drawCards = new ArrayList<>();
    for (int i = 0; i < maxNumDrawCards; i++) {
      if (drawPile.size() < i + 1 && numTimesRecycledDrawPile < numTimesRedrawAllowed) {
        // the draw pile is empty and another redraw is allowed
        for (int j = 0; j < maxNumDrawCards - i; j++) {
          drawCards.add(discardPile.get(j));
        }
        break;
      } else if (drawPile.size() >= i + 1) {
        drawCards.add(drawPile.get(i));
      }
    }
    return drawCards;
  }
}
