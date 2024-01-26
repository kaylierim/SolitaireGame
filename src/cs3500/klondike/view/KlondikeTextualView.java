package cs3500.klondike.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A simple text-based rendering of the Klondike game.
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel model;
  private final Appendable output;

  public KlondikeTextualView(KlondikeModel model) {
    this(model, null);
  }

  public KlondikeTextualView(KlondikeModel model, Appendable output) {
    this.model = model;
    this.output = output;
  }

  // Your implementation goes here
  @Override
  public void render() throws IOException {
    output.append(this.toString());
  }

  private List<List<String>> makeCascadeStringList() {
    List<List<String>> cascadeStringList = new ArrayList<>();
    for (int i = 0; i < model.getNumPiles(); i++) {
      ArrayList<String> pileList = new ArrayList<>();
      int pileHeight = model.getPileHeight(i);
      if (pileHeight == 0) {
        pileList.add("X");
      } else {
        for (int j = 0; j < pileHeight; j++) {
          if (model.isCardVisible(i, j)) {
            Card card = model.getCardAt(i, j);
            pileList.add(card.toString());
          } else {
            pileList.add("?");
          }
        }
      }
      cascadeStringList.add(pileList);
    }
    return cascadeStringList;
  }

  private int getMaxRows(List<List<String>> cascadeStringList) {
    int max = 0;
    for (List<String> strings : cascadeStringList) {
      int size = strings.size();
      if (max < size) {
        max = size;
      }
    }
    return max;
  }

  private void appendSpacesAtBeginningOfLine(StringBuilder sb, int row, int pileIndex,
                                             List<List<String>> cascadeStringList) {
    for (int i = 0; i <= pileIndex; i++) {
      if (row > cascadeStringList.get(i).size() - 1) {
        sb.append("   ");
      } else {
        break;
      }
    }
  }

  private void appendSpacesAtMiddleOfLine(StringBuilder sb, int row, int pileIndex,
                                          List<List<String>> cascadeStringList) {
    int lastPileWithCardInThatRow = -1;
    for (int i = 0; i < pileIndex; i++) {
      if (cascadeStringList.get(i).size() >= row + 1) {
        lastPileWithCardInThatRow = i;
      }
    }
    if (lastPileWithCardInThatRow != -1) {
      int spaceBetween = 3 * (pileIndex - lastPileWithCardInThatRow - 1);
      for (int i = 0; i < spaceBetween; i++) {
        sb.append(" ");
      }
    }
  }

  private String toStringCascade() {
    StringBuilder sb = new StringBuilder();
    List<List<String>> cascadeStringList = makeCascadeStringList();
    int row = 0;
    int maxRows = getMaxRows(cascadeStringList);
    int pileIndex = 1;
    while (row < maxRows) {
      if (row > 0) {
        appendSpacesAtBeginningOfLine(sb, row, pileIndex, cascadeStringList);
        pileIndex++;
      }
      for (int i = 0; i < cascadeStringList.size(); i++) {
        List<String> pile = cascadeStringList.get(i);
        if (row < pile.size()) {
          String cardString = pile.get(row);
          if (i > 1) {
            if (cascadeStringList.get(i - 1).size() <= row) {
              appendSpacesAtMiddleOfLine(sb, row, i, cascadeStringList);
            }
          }
          // if card is an X
          if (cardString.length() == 1) {
            sb.append("  ");
          } else if (cardString.length() == 2) {
            sb.append(" ");
          }
          sb.append(cardString);
        }
      }
      if (row + 1 == maxRows) {
        return sb.toString();
      } else {
        sb.append("\n");
        row++;
      }
    }
    return sb.toString();
  }

  private String toStringDrawPile() {
    StringBuilder sb = new StringBuilder();
    List<Card> drawCards = model.getDrawCards();
    for (int i = 0; i < drawCards.size(); i++) {
      Card card = drawCards.get(i);
      if (i + 1 == drawCards.size()) {
        sb.append(card.toString());
      } else {
        sb.append(card.toString()).append(", ");
      }
    }
    return sb.toString();
  }

  private String toStringFoundationPiles() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < model.getNumFoundations(); i++) {
      Card card = model.getCardAt(i);
      if (card == null) {
        sb.append("<none>");
      } else {
        sb.append(card);
      }
      if (i + 1 != model.getNumFoundations()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  /**
   * Returns a string interpretation of the view.
   */
  public String toString() {
    return "Draw: "
            + toStringDrawPile() + "\n"
            + "Foundation: "
            + toStringFoundationPiles() + "\n"
            + toStringCascade();
  }
}
