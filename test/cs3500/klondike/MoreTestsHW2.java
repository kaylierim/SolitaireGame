package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeCard;
import cs3500.klondike.view.KlondikeTextualView;

/**
 * Tests the public model interface.
 */
public class MoreTestsHW2 {

  String validCards = "2♣, A♣, 3♢, 3♣, A♠, 2♠, 3♠, A♢, 2♢, A♡, 2♡, 3♡";
  List<Card> validDeck = makeDeck(validCards.split(", "));
  List<Card> onlyAcesDeck = makeDeck("A♠", "A♡", "A♢", "A♣");
  BasicKlondike smallGame;
  BasicKlondike regGame;
  BasicKlondike gameHasNotStarted;
  BasicKlondike playedThroughGame;
  BasicKlondike onlyAcesGame;


  private List<Card> makeDeck(String... cards) {
    List<Card> newDeck = new ArrayList<>();
    for (String card: cards) {
      newDeck.add(new KlondikeCard(card));
    }
    return newDeck;
  }

  @Before
  public void init() {
    smallGame = new BasicKlondike();
    smallGame.startGame(validDeck, false, 2, 3);
    regGame = new BasicKlondike();
    regGame.startGame(regGame.getDeck(), false, 7, 3);
    gameHasNotStarted = new BasicKlondike();
    playedThroughGame = new BasicKlondike();
    playedThroughGame.startGame(playedThroughGame.getDeck(), false, 7, 3);
    playedThroughGame.moveToFoundation(0, 0);
    playedThroughGame.moveToFoundation(2, 1);
    playedThroughGame.movePile(2, 1, 4);
    playedThroughGame.movePile(5, 1, 0);
    onlyAcesGame = new BasicKlondike();
  }

  // test startGame
  @Test
  public void testInvalidDeck() {
    BasicKlondike newGame = new BasicKlondike();
    String invalidCards = "A♣, A♠, A♠, 2♠, 2♠, 3♠, 2♢, 3♢, 3♡";
    List<Card> invalidDeck = makeDeck(invalidCards.split(", "));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.startGame(invalidDeck, false, 2, 1));
    String invalidCards1 = "A♣, 2♣, 3♣, A♠, A♠, 2♠, 2♠, 3♠, 3♠, A♢, 2♢, 3♢, A♡, 2♡";
    List<Card> invalidDeck1 = makeDeck(invalidCards1.split(", "));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.startGame(invalidDeck1, false, 2, 1));
    List<Card> invalidDeck2 = makeDeck("A♣", "A♠", "2♢", "3♠", "7♠");
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.startGame(invalidDeck2, false, 2, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> gameHasNotStarted.startGame(null, false, 2, 1));
  }

  @Test
  public void testInvalidInput() {
    BasicKlondike newGame = new BasicKlondike();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.startGame(validDeck, false, 3, -1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> newGame.startGame(validDeck, false, 5, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> onlyAcesGame.startGame(onlyAcesDeck, false, 3, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> onlyAcesGame.startGame(onlyAcesDeck, false, 0, 1));
  }

  @Test
  public void testMovePile() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.movePile(0, 0, 2));
    Assert.assertThrows(IllegalStateException.class,
        () -> gameHasNotStarted.movePile(0, 0, 2));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.movePile(-1, 1, 2));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.movePile(0, 1, 0));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.movePile(3, 3, 1));
  }

  @Test
  public void testInvalidNumPiles() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> gameHasNotStarted.startGame(gameHasNotStarted.getDeck(),
                false, 10, 1));
  }

  @Test
  public void testGameAlreadyStarted() {
    gameHasNotStarted.startGame(validDeck, false, 2, 1);
    Assert.assertThrows(IllegalStateException.class,
        () -> gameHasNotStarted.startGame(validDeck, false, 2, 1));
  }

  @Test
  public void testGetPileHeight() {
    BasicKlondike game = new BasicKlondike();
    game.startGame(game.getDeck(), true, 3, 1);
    Assert.assertEquals(1, game.getPileHeight(0));
    Assert.assertEquals(2, game.getPileHeight(1));
  }

  @Test
  public void testMoveDraw() {
    regGame.discardDraw();
    regGame.discardDraw();
    regGame.discardDraw();
    regGame.discardDraw();
    regGame.moveDraw(1);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.moveDraw(7));
  }

  @Test
  public void testGetCardAt() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.getCardAt(0, 1));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.getCardAt(1, 0));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> regGame.getCardAt(4));
  }

  @Test
  public void testMoveToFoundation() {
    Assert.assertThrows(IllegalArgumentException.class,
        () -> playedThroughGame.moveToFoundation(0, 4));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> playedThroughGame.moveToFoundation(0, -4));
  }

  @Test
  public void testMoveDrawToFoundation() {
    onlyAcesGame.startGame(onlyAcesDeck, false, 2, 1);
    onlyAcesGame.moveDrawToFoundation(0);
    Assert.assertThrows(IllegalArgumentException.class,
        () -> playedThroughGame.moveToFoundation(0, -1));
    BasicKlondike newGame = new BasicKlondike();
    List<Card> deck = makeDeck("2♠", "2♡", "2♣", "A♠", "A♡",  "A♢", "2♢", "A♣");
    newGame.startGame(deck, false, 3, 3);
    newGame.moveToFoundation(2, 0);
    newGame.moveDrawToFoundation(0);
  }

  // KlondikeCard tests
  @Test
  public void testHashCode() {
    KlondikeCard card1 = new KlondikeCard("3♠");
    KlondikeCard card2 = new KlondikeCard("3♠");
    Assert.assertEquals(card1.hashCode(), card2.hashCode());
    KlondikeCard card3 = new KlondikeCard("3♣");
    KlondikeCard card4 = new KlondikeCard("3♣");
    Assert.assertEquals(card3.hashCode(), card4.hashCode());
    KlondikeCard card5 = new KlondikeCard("3♢");
    KlondikeCard card6 = new KlondikeCard("3♢");
    Assert.assertEquals(card5.hashCode(), card6.hashCode());
    KlondikeCard card7 = new KlondikeCard("3♡");
    KlondikeCard card8 = new KlondikeCard("3♡");
    Assert.assertEquals(card7.hashCode(), card8.hashCode());
  }

  @Test
  public void testGetNumRows() {
    Assert.assertEquals(7, regGame.getNumRows());
  }

  @Test
  public void testGetNumDraw() {
    Assert.assertEquals(3, regGame.getNumDraw());
  }

  @Test
  public void testGetScore() {
    Assert.assertEquals(0, regGame.getScore());
    Assert.assertEquals(2, playedThroughGame.getScore());
  }

  @Test
  public void testEquals() {
    KlondikeCard card1 = new KlondikeCard("3♠");
    KlondikeCard card2 = new KlondikeCard("3♠");
    Assert.assertEquals(card1, card2);
    KlondikeCard card3 = new KlondikeCard("3♠");
    String card4 = "4♢";
    Assert.assertNotEquals(card3, card4);
  }

  // view test
  @Test
  public void viewTest() {
    BasicKlondike game = new BasicKlondike();
    KlondikeTextualView view = new KlondikeTextualView(game);
    game.startGame(game.getDeck(), false, 2, 1);
    String viewToString = "Draw: 4♠\nFoundation: <none>, <none>, <none>, <none>\n A♠  ?\n    3♠";
    Assert.assertEquals(viewToString, view.toString());


    KlondikeTextualView view1 = new KlondikeTextualView(playedThroughGame);
    String view1ToString = "Draw: 3♢, 4♢, 5♢\n"
            + "Foundation: A♠, A♡, <none>, <none>\n"
            + " K♡  ? 3♠  ?  ?  ?  ?\n"
            + "    8♠     ?  ?  ?  ?\n"
            + "           ?  ?  ?  ?\n"
            + "          6♡  ?  ?  ?\n"
            + "            10♡ J♡  ?\n"
            + "             9♠     ?\n"
            + "                   2♢";
    Assert.assertEquals(view1ToString, view1.toString());
  }
}
