package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * Factory class of a Klondike game.
 */
public class KlondikeCreator {

  /**
   * Enum class of all possible game types.
   */
  public enum GameType {
    BASIC, LIMITED, WHITEHEAD;

    /**
     * Creates a KlondikeModel based on the given GameType.
     */
    public static KlondikeModel create(GameType type) {
      switch (type) {
        case BASIC:
          return new BasicKlondike();
        case LIMITED:
          return new LimitedDrawKlondike(2);
        case WHITEHEAD:
          return new WhiteheadKlondike();
        default:
          return null;
      }
    }
  }
}
