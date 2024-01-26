package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;

/**
 * Additional tests for WhiteheadKlondike and LimitedDrawKlondike.
 */
public class MoreTestsHW4 {

  WhiteheadKlondike whiteheadGame;
  LimitedDrawKlondike limitedDrawGame;
  List<Card> sortedDeck;

  @Before
  public void init() {
    whiteheadGame = new WhiteheadKlondike();
    limitedDrawGame = new LimitedDrawKlondike(1);
    sortedDeck = new ArrayList<>(whiteheadGame.getDeck());
    sortedDeck.sort((card1, card2) -> getValue(card1) - getValue(card2));
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

  private void swap(List<Card> deck, int index1, int index2) {
    Card card1 = deck.get(index1);
    deck.set(index1, deck.get(index2));
    deck.set(index2, card1);
  }

  @Test
  public void testLimitedDrawConstructor() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> new LimitedDrawKlondike(-1));
  }

  @Test
  public void testWhiteheadKlondikeAllCardsFaceUp() {
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    for (int col = 0; col < whiteheadGame.getNumPiles(); col++) {
      Assert.assertTrue(whiteheadGame.isCardVisible(col, 0));
    }
  }

  @Test
  public void testWhiteheadKlondikeSingleColoredBuilds() {
    swap(sortedDeck, 0, 21);
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.movePile(1, 1, 0);
    Assert.assertFalse(whiteheadGame.isGameOver());
  }

  @Test
  public void testWhiteheadKlondikeMoveSameSuitMultipleCards() {
    swap(sortedDeck, 0, 8);
    swap(sortedDeck, 9, 13);
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.movePile(1, 1, 0);
    whiteheadGame.movePile(0, 2, 2);
    whiteheadGame.movePile(2, 3, 0);
    Assert.assertFalse(whiteheadGame.isGameOver());
  }

  @Test
  public void testWhiteheadKlondikeRedBuilds() {
    swap(sortedDeck, 27, 28);
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.moveDraw(6);
    Assert.assertFalse(whiteheadGame.isGameOver());
  }

  @Test
  public void testIllegalMoveNonEmptyPile() {
    swap(sortedDeck, 23, 25);
    whiteheadGame.startGame(sortedDeck, false, 7, 3);
    whiteheadGame.moveToFoundation(2, 0);
    whiteheadGame.moveToFoundation(0, 1);
    whiteheadGame.movePile(2, 1, 4);
    Assert.assertThrows(IllegalStateException.class,
        () -> whiteheadGame.movePile(4, 2, 5));
  }

  // Controller tests
  @Test
  public void testQuitWhiteheadKlondikeController() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(whiteheadGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().endsWith("Score: 0\n"));
  }

  @Test
  public void testDiscardDrawWhiteheadKlondikeController() {
    StringReader input = new StringReader("dd q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(whiteheadGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Draw: 4♢, 5♢"));
  }

  @Test
  public void testMoveToFoundationWhiteheadController() {
    StringReader input = new StringReader("mpf 1 1  mpp 2 1 1 mpf 2 1 q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(whiteheadGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Score: 1\n"));
  }

  @Test
  public void testQuitLimitedDrawController() {
    StringReader input = new StringReader("q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(limitedDrawGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().endsWith("Score: 0\n"));
  }

  @Test
  public void testMovePileLimitedDrawController() {
    StringReader input = new StringReader("mpp 1 1 7 mpp 6 1 1\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(limitedDrawGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Score: 0\n"));
  }

  @Test
  public void testMoveDrawLimitedDrawController() {
    StringReader input = new StringReader("md 1 md 2\nq\n");
    StringBuilder actualOutput = new StringBuilder();

    swap(sortedDeck, 0, 3);
    swap(sortedDeck, 7, 4);
    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(limitedDrawGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Score: 0\n"));
  }

  @Test
  public void testMoveToFoundationLimitedDrawController() {
    StringReader input = new StringReader("mpf 1 1 q\n");
    StringBuilder actualOutput = new StringBuilder();

    KlondikeController controller = new KlondikeTextualController(input, actualOutput);
    controller.playGame(limitedDrawGame, sortedDeck, false, 7, 3);

    Assert.assertTrue(actualOutput.toString().contains("Score: 0\n"));
  }
}
