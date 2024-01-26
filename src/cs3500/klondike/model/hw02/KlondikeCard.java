package cs3500.klondike.model.hw02;

/**
 * KlondikeCard class represents a card in a Klondike solitaire game and
 * implements the Card interface. Each card has a value and a suit.
 */
public class KlondikeCard implements Card {

  /**
   * Enum of all possible card values.
   */
  public enum CardValue {
    _A(1, "A"),
    _2(2, "2"),
    _3(3, "3"),
    _4(4, "4"),
    _5(5, "5"),
    _6(6, "6"),
    _7(7, "7"),
    _8(8, "8"),
    _9(9, "9"),
    _10(10, "10"),
    _J(11, "J"),
    _Q(12, "Q"),
    _K(13, "K");

    private final String text;
    private final int value;

    CardValue(int value, String text) {
      this.value = value;
      this.text = text;
    }

    private static CardValue parse(String value) {
      for (CardValue s: CardValue.values()) {
        if (s.text.equals(value)) {
          return s;
        }
      }
      throw new IllegalArgumentException("Cannot match value string " + value);
    }

    @Override
    public String toString() {
      return text;
    }
  }

  /**
   * Enum of all possible suits.
   */
  public enum Suit {
    Club("♣"),
    Spade("♠"),
    Diamond("♢"),
    Heart("♡");

    private final String text;

    Suit(String text) {
      this.text = text;
    }

    private static Suit parse(String suit) {
      for (Suit s: Suit.values()) {
        if (s.text.equals(suit)) {
          return s;
        }
      }
      throw new IllegalArgumentException("Cannot match Suit string " + suit);
    }

    @Override
    public String toString() {
      return text;
    }
  }

  private final CardValue cardValue;
  private final Suit suit;
  private final String card;

  /**
   * Constructs a card with a String input.
   * Initializes card value and suit according to the String.
   */
  public KlondikeCard(String card) {
    suit = Suit.parse(card.substring(card.length() - 1));
    cardValue = CardValue.parse(card.substring(0, card.length() - 1));
    this.card = card;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof  KlondikeCard) {
      return card.equals(other.toString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += cardValue.value * 31;
    switch (suit) {
      case Club:
        hash += 1;
        break;
      case Spade:
        hash += 2;
        break;
      case Diamond:
        hash += 3;
        break;
      case Heart:
        hash += 4;
        break;
      default:
        break;
    }
    return hash;
  }

  @Override
  public String toString() {
    return card;
  }

  @Override
  public boolean isCardRed() {
    return suit == Suit.Heart || suit == Suit.Diamond;
  }
}
