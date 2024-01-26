package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

/**
 * Tests methods in the BasicKlondike class.
 */
public class ExamplarModelTests {

  BasicKlondike nonShuffledBaseGame;

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

  private void swap(List<Card> deck, int index1, int index2) {
    Card card1 = deck.get(index1);
    deck.set(index1, deck.get(index2));
    deck.set(index2, card1);
  }

  @Before
  public void init() {
    nonShuffledBaseGame = new BasicKlondike();
    ArrayList<Card> deck = new ArrayList<>(nonShuffledBaseGame.getDeck());
    deck.sort((card1, card2) -> getValue(card1) - getValue(card2));
    nonShuffledBaseGame.startGame(deck, false, 7, 3);
  }

  // moveToFoundation tests:
  @Test
  public void testMoveToFoundationThrowsIllegalStateException() {
    Assert.assertThrows(IllegalStateException.class,
        () -> nonShuffledBaseGame.moveToFoundation(5, 3));
  }

  @Test
  public void testMoveToFoundationForWrongSuit() {
    BasicKlondike allAcesGame = new BasicKlondike();
    ArrayList<Card> deck1 = new ArrayList<>(allAcesGame.getDeck());
    for (int i = 0; i < deck1.size(); i++) {
      if (deck1.get(i).toString().equals("A♣")) {
        swap(deck1, i, 0);
      } else if (deck1.get(i).toString().equals("2♢")) {
        swap(deck1, i, 2);
      }
    }
    allAcesGame.startGame(deck1, false, 2, 1);
    allAcesGame.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class,
        () -> allAcesGame.moveToFoundation(1, 0));
  }

  @Test
  public void testMoveToFoundationForWrongValue() {
    BasicKlondike newGame = new BasicKlondike();
    ArrayList<Card> deck1 = new ArrayList<>(newGame.getDeck());
    for (int i = 0; i < deck1.size(); i++) {
      if (deck1.get(i).toString().equals("A♣")) {
        swap(deck1, i, 0);
      } else if (deck1.get(i).toString().equals("3♣")) {
        swap(deck1, i, 2);
      }
    }
    newGame.startGame(deck1, false, 2, 1);
    newGame.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class,
        () -> newGame.moveToFoundation(1, 0));
  }

  // movePile tests:
  @Test
  public void testMovePileNonKingToEmptySpot() {
    BasicKlondike newGame = new BasicKlondike();
    ArrayList<Card> deck2 = new ArrayList<>(newGame.getDeck());
    for (int i = 0; i < deck2.size(); i++) {
      if (deck2.get(i).toString().equals("A♣")) {
        swap(deck2, i, 0);
      } else if (deck2.get(i).toString().equals("2♢")) {
        swap(deck2, i, 2);
      }
    }
    newGame.startGame(deck2, false, 2, 1);
    newGame.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class,
        () -> newGame.movePile(1, 1, 0));
  }

  @Test
  public void testMovePileThrowsIllegalStateExceptionOnEmptySpot() {
    BasicKlondike newGame = new BasicKlondike();
    ArrayList<Card> deck2 = new ArrayList<>(newGame.getDeck());
    for (int i = 0; i < deck2.size(); i++) {
      if (deck2.get(i).toString().equals("A♣")) {
        swap(deck2, i, 0);
      }
    }
    newGame.startGame(deck2, false, 2, 1);
    newGame.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.movePile(0, 1, 1));
  }

  @Test
  public void testMovePileThrowsIllegalStateException() {
    Assert.assertThrows(IllegalStateException.class,
        () -> nonShuffledBaseGame.movePile(0, 1, 2));
  }

  @Test
  public void testMovePileThrowsIllegalArgumentException() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> nonShuffledBaseGame.movePile(0, 1, 7));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> nonShuffledBaseGame.movePile(0, 2, 1));
  }

  // moveDrawToFoundation tests:
  @Test
  public void testMoveDrawToFoundationThrowsIllegalStateExceptionNoDrawCards() {
    nonShuffledBaseGame.discardDraw();
    nonShuffledBaseGame.discardDraw();
    nonShuffledBaseGame.discardDraw();
    Assert.assertThrows(IllegalStateException.class,
        () -> nonShuffledBaseGame.moveDrawToFoundation(0));
  }

  // moveDraw tests
  @Test
  public void testMoveDrawThrowsIllegalStateException() {
    BasicKlondike newGame = new BasicKlondike();
    newGame.startGame(newGame.getDeck(), false, 7, 1);
    Assert.assertThrows(IllegalStateException.class,
        () -> newGame.moveDraw(6));
  }

  @Test
  public void testMoveDrawThrowsIllegalStateExceptionNoDrawCards() {
    BasicKlondike twoPileGame = new BasicKlondike();
    twoPileGame.startGame(twoPileGame.getDeck(), false, 2, 3);
    Card firstDraw = twoPileGame.getDrawCards().get(0);
    for (int i = 0; i < 49; i++) {
      nonShuffledBaseGame.discardDraw();
    }
    Assert.assertEquals(firstDraw, twoPileGame.getDrawCards().get(0));
  }

  @Test
  public void testMoveDrawThrowsIllegalArgumentException() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> nonShuffledBaseGame.moveDraw(7));
  }
}
