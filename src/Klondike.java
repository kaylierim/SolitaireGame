import java.io.InputStreamReader;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.KlondikeCreator;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;

/**
 * Serves as the entry point for a Klondike game.
 */
public final class Klondike {

  private static KlondikeCreator.GameType getGameType(String s) {
    switch (s) {
      case "basic":
        return KlondikeCreator.GameType.BASIC;
      case "limited":
        return KlondikeCreator.GameType.LIMITED;
      case "whitehead":
        return KlondikeCreator.GameType.WHITEHEAD;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static KlondikeModel getKlondikeModel(String[] args, KlondikeCreator.GameType gameType) {
    KlondikeModel model;
    if (gameType != KlondikeCreator.GameType.LIMITED) {
      model = KlondikeCreator.GameType.create(gameType);
    } else {
      int numTimesRedrawAllowed = Integer.parseInt(args[1]);
      model = new LimitedDrawKlondike(numTimesRedrawAllowed);
    }
    return model;
  }

  /**
   * Entry point for a Klondike game.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException();
    }
    int currentArgIndex = 0;
    KlondikeCreator.GameType gameType = getGameType(args[0]);
    KlondikeModel model = getKlondikeModel(args, gameType);
    if (gameType == KlondikeCreator.GameType.LIMITED) {
      currentArgIndex++;
    }
    int numPiles = 7;
    int numDraw = 3;
    if (args.length > currentArgIndex + 1) {
      numPiles = Integer.parseInt(args[++currentArgIndex]);
    }
    if (args.length > currentArgIndex + 1) {
      numDraw = Integer.parseInt(args[++currentArgIndex]);
    }
    try {
      new KlondikeTextualController(new InputStreamReader(System.in), System.out)
              .playGame(model, model.getDeck(), true, numPiles, numDraw);
    } catch (IllegalArgumentException | IllegalStateException e) {
      // do nothing
    }
  }
}
