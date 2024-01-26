package cs3500.klondike.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;

/**
 * KlondikeTextualController class that implements the KlondikeController interface.
 */
public class KlondikeTextualController implements KlondikeController {

  /**
   * Indicates when user wants to quit.
   */
  static class QuitGameException extends Exception {
    public QuitGameException() {
      super();
    }
  }

  private final Appendable appendable;
  private final Scanner sc;
  private KlondikeModel model;
  private KlondikeTextualView view;

  /**
   * Constructs a KlondikeTextualController with a readable and appendable.
   *
   * @throws IllegalArgumentException if either are null.
   */
  public KlondikeTextualController(Readable r, Appendable a) {
    if (r == null || a == null) {
      throw new IllegalArgumentException();
    }
    this.appendable = a;
    this.sc = new Scanner(r);
  }

  private void writeMessageWithNewLine(String message) throws IllegalStateException {
    writeMessage(message);
    writeMessage(System.lineSeparator());
  }

  private void writeMessage(String message) throws IllegalStateException {
    try {
      appendable.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  private int getNextInt() throws QuitGameException {
    while (sc.hasNext()) {
      String nextInput = sc.next();
      if (nextInput.equals("q") || nextInput.equals("Q")) {
        throw new QuitGameException();
      }
      try {
        return Integer.parseInt(nextInput);
      } catch (NumberFormatException e) {
        // do nothing because it needs to continue
        // parsing the input until it finds a valid integer.
      }
    }
    throw new IllegalStateException();
  }

  private String getNextMove() throws QuitGameException {
    String[] validMoves = {"mpp", "md", "mpf", "mdf", "dd"};
    while (sc.hasNext()) {
      String nextInput = sc.next();
      if (nextInput.equals("q") || nextInput.equals("Q")) {
        throw new QuitGameException();
      }
      for (String validMove : validMoves) {
        if (nextInput.equals(validMove)) {
          return nextInput;
        }
      }
      writeInvalidMove();
      writeMessageWithNewLine("Not a valid move.");
      viewRender();
      outputGameScore();
    }
    throw new IllegalStateException();
  }

  private void writeInvalidMove() {
    writeMessage("Invalid move. Play again. ");
  }

  private void movePile() throws QuitGameException {
    int srcPile = getNextInt() - 1;
    int numCards = getNextInt();
    int destPile = getNextInt() - 1;

    try {
      model.movePile(srcPile, numCards, destPile);
    } catch (IllegalArgumentException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Invalid arguments.");
      viewRender();
      movePile();
    } catch (IllegalStateException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Move is not allowed for move pile.");
    }
  }

  private void moveDraw() throws QuitGameException {
    int destPile = getNextInt() - 1;
    try {
      model.moveDraw(destPile);
    } catch (IllegalArgumentException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Invalid arguments.");
      viewRender();
      moveDraw();
    } catch (IllegalStateException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Move is not allowed for move draw.");
    }
  }

  private void movePileToFoundation() throws QuitGameException {
    int srcPile = getNextInt() - 1;
    int foundationPile = getNextInt() - 1;
    try {
      model.moveToFoundation(srcPile, foundationPile);
    } catch (IllegalArgumentException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Invalid arguments.");
      viewRender();
      movePileToFoundation();
    } catch (IllegalStateException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Move is not allowed for move pile to foundation.");
    }
  }

  private void moveDrawToFoundation() throws QuitGameException {
    int foundationPile = getNextInt() - 1;
    try {
      model.moveDrawToFoundation(foundationPile);
    } catch (IllegalArgumentException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Invalid arguments.");
      viewRender();
      moveDrawToFoundation();
    } catch (IllegalStateException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Move is not allowed for move draw to foundation.");
    }
  }

  private void discardDraw() throws QuitGameException {
    try {
      model.discardDraw();
    } catch (IllegalStateException e) {
      writeInvalidMove();
      writeMessageWithNewLine("Cannot discard draw.");
    }
  }

  private void viewRender() {
    try {
      view.render();
      writeMessageWithNewLine("");
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  private void outputGameScore() {
    writeMessageWithNewLine("Score: " + model.getScore());
  }

  @Override
  public void playGame(KlondikeModel model, List<Card> deck, boolean shuffle, int numPiles,
                       int numDraw) {
    this.model = model;
    if (model == null) {
      throw new IllegalArgumentException();
    }
    try {
      model.startGame(deck, shuffle, numPiles, numDraw);
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new IllegalStateException();
    }
    view = new KlondikeTextualView(model, appendable);

    try {
      while (!model.isGameOver()) {
        viewRender();
        outputGameScore();
        String playerInstruction = getNextMove();

        switch (playerInstruction) {
          case "mpp":
            movePile();
            break;
          case "md":
            moveDraw();
            break;
          case "mpf":
            movePileToFoundation();
            break;
          case "mdf":
            moveDrawToFoundation();
            break;
          case "dd":
            discardDraw();
            break;
          case "q":
          case "Q":
            throw new QuitGameException();
          default:
            break;
        }
      }
    } catch (QuitGameException e) {
      executeGameQuit(model);
      return;
    }
    executeGameOver(model);
  }

  private void executeGameOver(KlondikeModel model) {
    boolean gameWon = true;
    viewRender();
    if (model.getNumRows() > 0) {
      writeMessage("Game over. ");
      outputGameScore();
      gameWon = false;
    }
    if (gameWon) {
      writeMessageWithNewLine("You win!");
    }
  }

  private void executeGameQuit(KlondikeModel model) {
    writeMessageWithNewLine("Game quit!");
    writeMessageWithNewLine("State of game when quit:");
    viewRender();
    outputGameScore();
  }
}

