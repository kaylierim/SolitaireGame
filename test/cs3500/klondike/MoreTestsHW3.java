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
 * Tests methods in the KlondikeTextualController class.
 */
public class MoreTestsHW3 {

  BasicKlondike model;
  List<Card> deck2;
  List<Card> aceDeck;

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

  private int find(List<Card> deck, String cardString) {
    for (int cardIndex = 0; cardIndex < deck.size(); cardIndex++) {
      String string = deck.get(cardIndex).toString();
      if (string.equals(cardString)) {
        return cardIndex;
      }
    }
    return -1;
  }

  @Before
  public void init() {
    model = new BasicKlondike();
    aceDeck = findAll(model.getDeck(), "A");
    deck2 = new ArrayList<>(model.getDeck());
    deck2.sort((card1, card2) -> getValue(card1) - getValue(card2));
  }

  @Test
  public void testCorrectCardAtPosition() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, deck2, false, 7, 3);
    Assert.assertEquals("A♣", model.getCardAt(0, 0).toString());
    Assert.assertEquals("8♣", model.getCardAt(1, 1).toString());
  }

  @Test
  public void testInvalidMoveWithInvalidPile() {
    StringReader input = new StringReader("mdf 7\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testNullModel() {
    StringReader input = new StringReader("mdf 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> controller.playGame(null, aceDeck, false, 2, 1));
  }

  @Test
  public void testPlayGameThrowsIllegalStateException() {
    StringReader input = new StringReader("mdf 1\nq\n");
    input.close();
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    Assert.assertThrows(IllegalStateException.class,
        () -> controller.playGame(model, aceDeck, false, 2, 1));
  }

  @Test
  public void testMoveDrawToFoundationValidMoves() {
    StringReader input = new StringReader("mdf 1 mdf 2\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    swap(aceDeck, 1,find(aceDeck, "A♣"));
    swap(aceDeck, 2, find(aceDeck, "A♢"));
    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 1, 3);
    Assert.assertTrue(actualOutput.toString().contains("Foundation: A♣, A♢"));
  }

  @Test
  public void testInvalidMoveWithZero() {
    StringReader input = new StringReader("mdf 0 1 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testControllerConstructor() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new KlondikeTextualController(null, new StringBuilder()));
  }

  @Test
  public void testInvalidArguments() {
    StringReader input = new StringReader("mpp -1 1 1 2 md -1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testPlayGame() {
    StringReader input = new StringReader("mpp 1 1 2\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertThrows(IllegalStateException.class,
        () -> controller.playGame(model, aceDeck, false, 2, 1));
  }

  @Test
  public void testInvalidMove() {
    StringReader input = new StringReader("md 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 1, 3);

    Assert.assertTrue(actualOutput.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testGameWon() {
    StringReader input = new StringReader("mpf 1 1 mpf 2 2 mpf 2 3 mdf 4\nQ\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertTrue(actualOutput.toString().contains("You win!"));
  }

  @Test
  public void testDiscardDraw() {
    StringReader input = new StringReader("mpf 1 1 mpf 2 2 mpf 2 3 dd mdf 4\nQ\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(model, aceDeck, false, 2, 1);

    Assert.assertTrue(actualOutput.toString().contains("You win!"));
  }
}
