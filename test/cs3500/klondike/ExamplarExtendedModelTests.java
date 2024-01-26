package cs3500.klondike;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for WhiteheadKlondike and LimitedDrawKlondike.
 */
public class ExamplarExtendedModelTests {

  WhiteheadKlondike whiteheadGame;
  LimitedDrawKlondike limitedDrawGame;
  List<Card> sortedDeck;
  List<Card> aceDeck;

  @Before
  public void init() {
    whiteheadGame = new WhiteheadKlondike();
    limitedDrawGame = new LimitedDrawKlondike(1);
    sortedDeck = new ArrayList<>(limitedDrawGame.getDeck());
    sortedDeck.sort((card1, card2) -> getValue(card1) - getValue(card2));
    aceDeck = findAll(limitedDrawGame.getDeck(), "A");
  }

  private int getValueOfSuit(char suit) {
    switch (suit) {
      case '♣':
        return 0;
      case '♠':
        return 1;
      case '♢':
        return 2;
      case '♡':
        return 3;
      default:
        return -1;
    }
  }

  private int getValueOfCard(String value) {
    switch (value) {
      case "A":
        return 1;
      case "K":
        return 13;
      case "Q":
        return 12;
      case "J":
        return 11;
      default:
        return Integer.valueOf(value, 10);
    }
  }

  private int getValue(Card card) {
    String cardString = card.toString();
    int suitIndex = cardString.length() - 1;
    return getValueOfCard(cardString.substring(0, suitIndex))
            + 14 * getValueOfSuit(cardString.charAt(suitIndex));
  }

  private List<Card> findAll(List<Card> deck, String cardString) {
    List<Card> cards = new ArrayList<>();
    for (Card card : deck) {
      if (card.toString().startsWith(cardString)) {
        cards.add(card);
      }
    }
    return cards;
  }

  private void swap(List<Card> deck, int index1, int index2) {
    Card card1 = deck.get(index1);
    deck.set(index1, deck.get(index2));
    deck.set(index2, card1);
  }

  @Test
  public void testLimitedDrawKlondikeDrawCardSize() {
    limitedDrawGame.startGame(aceDeck, false, 1, 2);
    Assert.assertEquals(2, limitedDrawGame.getDrawCards().size());
    limitedDrawGame.discardDraw();
    Assert.assertEquals(2, limitedDrawGame.getDrawCards().size());
    limitedDrawGame.discardDraw();
    Assert.assertEquals(2, limitedDrawGame.getDrawCards().size());
  }

  @Test
  public void testLimitedDrawKlondikeDrawCardSizeIsZero() {
    limitedDrawGame.startGame(aceDeck, false, 1, 2);
    for (int i = 0; i < 6; i++) {
      limitedDrawGame.discardDraw();
    }
    Assert.assertEquals(0, limitedDrawGame.getDrawCards().size());
    Assert.assertThrows(IllegalStateException.class, () -> limitedDrawGame.discardDraw());
  }

  @Test
  public void testLimitedDrawKlondikeDrawCardLargerScale() {
    limitedDrawGame.startGame(sortedDeck, false, 7, 3);
    for (int i = 0; i < 24; i++) {
      limitedDrawGame.discardDraw();
    }
    Assert.assertEquals(3, limitedDrawGame.getDrawCards().size());
    for (int i = 0; i < 21; i++) {
      limitedDrawGame.discardDraw();
    }
    Assert.assertEquals(3, limitedDrawGame.getDrawCards().size());
    limitedDrawGame.discardDraw();
    Assert.assertEquals(2, limitedDrawGame.getDrawCards().size());
    limitedDrawGame.discardDraw();
    Assert.assertEquals(1, limitedDrawGame.getDrawCards().size());
    limitedDrawGame.discardDraw();
    Assert.assertEquals(0, limitedDrawGame.getDrawCards().size());
  }

  @Test
  public void testWhiteheadKlondikeMoveAnyCardToEmptyPile() {
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.moveToFoundation(0, 0);
    whiteheadGame.moveDraw(0);
    Assert.assertFalse(whiteheadGame.isGameOver());
  }

  @Test
  public void testWhiteheadKlondikeBlackBuilds() {
    swap(sortedDeck, 0, 8);
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.movePile(1, 1, 0);
    Assert.assertFalse(whiteheadGame.isGameOver());
  }

  @Test
  public void testIllegalMoveEmptyPile() {
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.moveToFoundation(2, 0);
    whiteheadGame.moveToFoundation(0, 1);
    whiteheadGame.movePile(2, 1, 4);
    Assert.assertThrows(IllegalStateException.class,
        () -> whiteheadGame.movePile(4, 2, 0));
  }

  @Test
  public void testCannotMoveAlternatingColors() {
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    Assert.assertThrows(IllegalStateException.class,
        () -> whiteheadGame.movePile(0, 1, 6));
  }
}
