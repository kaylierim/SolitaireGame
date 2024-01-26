package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

/**
 * Tests methods in the KlondikeTextualController class (Examplar).
 */
public class ExamplarControllerTests {

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

  BasicKlondike model;
  List<Card> deck2;
  List<Card> aceDeck;

  @Before
  public void init() {
    model = new BasicKlondike();
    aceDeck = findAll(model.getDeck(), "A");
    deck2 = new ArrayList<>(model.getDeck());
    deck2.sort((card1, card2) -> getValue(card1) - getValue(card2));
  }

  @Test
  public void testQuit() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().endsWith("Score: 0\n"));
  }

  @Test
  public void testQuitAgain() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("                   2♢\n"));
  }

  @Test
  public void testUnexpectedValue() {
    StringReader input = new StringReader("mdf p\n     1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);
    Assert.assertTrue(actualOutput.toString().contains("Score: 1\n"));
  }

  @Test
  public void testMoveDraw() {
    StringReader input = new StringReader("md 0 0 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    swap(deck2, 0, 3);
    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testMoveDrawValidMoves() {
    StringReader input = new StringReader("md 1 md 2\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    swap(deck2, 0, 3);
    swap(deck2, 7, 4);
    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);
    Assert.assertTrue(actualOutput.toString().contains(" 4♣  ?  ?  ?  ?  ?  ?\n"
            + " 3♢"));
  }

  @Test
  public void testMovePileValidMoves() {
    StringReader input = new StringReader("mpp 1 1 7 mpp 6 1 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);
    Assert.assertTrue(actualOutput.toString().contains(" K♠  ?  ?  ?  ?  ?  ?"));
  }

  @Test
  public void testMovePile() {
    StringReader input = new StringReader("mpp 0 0 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testMovePileToFoundation() {
    StringReader input = new StringReader("mpf 0 0 1 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);
    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testUnderstanding() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);
    String expected = "Draw: 3♢, 4♢, 5♢\n"
            + "Foundation: <none>, <none>, <none>, <none>\n"
            + " A♣  ?  ?  ?  ?  ?  ?\n"
            + "    8♣  ?  ?  ?  ?  ?\n"
            + "       A♠  ?  ?  ?  ?\n"
            + "          6♠  ?  ?  ?\n"
            + "            10♠  ?  ?\n"
            + "                K♠  ?\n"
            + "                   2♢\n"
            + "Score: 0\n"
            + "Game quit!\n"
            + "State of game when quit:\n"
            + "Draw: 3♢, 4♢, 5♢\n"
            + "Foundation: <none>, <none>, <none>, <none>\n"
            + " A♣  ?  ?  ?  ?  ?  ?\n"
            + "    8♣  ?  ?  ?  ?  ?\n"
            + "       A♠  ?  ?  ?  ?\n"
            + "          6♠  ?  ?  ?\n"
            + "            10♠  ?  ?\n"
            + "                K♠  ?\n"
            + "                   2♢\n"
            + "Score: 0\n";
    Assert.assertEquals(expected, actualOutput.toString());
  }

  @Test
  public void testUnderstanding1() {
    StringReader input = new StringReader("mpp");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    Assert.assertThrows(IllegalStateException.class,
        () -> controller.playGame(model, deck2, false, 7, 3));
  }

  @Test
  public void testUnderstanding2() {
    StringReader input = new StringReader("mpl 1 1 2");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    Assert.assertThrows(IllegalStateException.class,
        () -> controller.playGame(model, deck2, false, 7, 3));
  }
}
