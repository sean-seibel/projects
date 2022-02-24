package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.scene.input.KeyEvent;
import model.Board;
import model.ShepherdBoard;
import model.util.Direction;
import view.BoardScene;

public class BoardController implements KeyBasedController {

  private Board board;
  private BoardScene boardScene;

  public static final int MAX_LEVEL = 10;

  public BoardController(int level, BoardScene bs) {
    this.readLevel(level);
    this.boardScene = bs;
  }


  @Override
  public String handleKey(KeyEvent event) {

    boolean moved = true;
    String result = "";

    switch (event.getCode()) {
      case W:
      case UP:
        result = board.move(Direction.UP);
        break;
      case A:
      case LEFT:
        result = board.move(Direction.LEFT);
        break;
      case D:
      case RIGHT:
        result = board.move(Direction.RIGHT);
        break;
      case S:
      case DOWN:
        result = board.move(Direction.DOWN);
        break;
      case SPACE:
        result = board.move(Direction.STAY);
        break;
      case R:
        board.reset();
        break;
      case U:
      case Z:
      case BACK_SPACE:
        board.undo();
        break;
      default:
        moved = false;
        break;
    }

    if (moved) {
      this.boardScene.updateBoard();
    }

    return result;
  }

  public Board getBoard() {
    return board;
  }

  private void readLevel(int i) {
    String flag = "LEVEL" + i; //LEVEL0 etc
    String lastRead = "";

    BufferedReader br;
    try {
      //br = new BufferedReader(new FileReader("res/data/levels.txt"));
      br = new BufferedReader(new InputStreamReader(
              BoardController.class.getResourceAsStream("/data/levels.txt")));
      // this works
    } catch (NullPointerException fnfE) {
      System.out.println("didn't find levels.txt");
      return;
    }

    try {
      while (!flag.equals(lastRead)) {
        lastRead = br.readLine();
        if (lastRead.equals("END")) {
          throw new RuntimeException("level " + flag + " not found");
        }
      }
    } catch (IOException ioe) {
      System.out.println("LEVEL" + i + " was not found");
      return;
    }


    List<String> lines = new ArrayList<>();

    try {
      lines.add(br.readLine());

      Scanner sc = new Scanner(lines.get(0));
      sc.next();
      int length = sc.nextInt();

      for (int l = 0; l < length; l++) {
        lines.add(br.readLine());
      }
    } catch (IOException ioE) {
      System.out.println("LEVEL" + i + " didn't have enough lines");
    }

    this.board = new ShepherdBoard(lines);
  }

}
