package view;

import controller.BoardController;
import controller.save.SaveDataHandler;
import controller.SceneCoordinator;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class LevelSelectScene extends Scene {

  private final Group root;
  private final SceneCoordinator sc;

  public LevelSelectScene(Group parent, int width, int height, SceneCoordinator sc) {
    super(parent, width, height);
    this.root = parent;
    this.sc = sc;

    int buttonWidth = (int) (height / 5.00);
    int xOffset = (int) (width / 14.0);
    int yOffsetLower = (int) (height / 4.0);
    int yOffsetUpper = (int) (height / 4.0); //text should touch the bottom of this bound
    int xSpacing = (int)((this.getWidth() - 2 * xOffset - buttonWidth) / 4.0) ;
    int ySpacing = (int)((this.getHeight() - yOffsetUpper - yOffsetLower - buttonWidth) / 1.0) ;
    int borderWidth = Math.max(1, (int) (buttonWidth * 0.08));
    int level = 1;

    int[] levelsBeaten = SaveDataHandler.getLevelsBeaten();

    for (int y = 0; y < 2; y++) {
      for (int x = 0; x < 5; x++) {
        this.placeLevelButton(level, xOffset + x * xSpacing, yOffsetUpper + y * ySpacing,
                buttonWidth, buttonWidth, borderWidth, levelsBeaten);
        level++;
      }
    }

    Text total = new Text("Total: ---");
    total.setFont(Font.font("Georgia", FontPosture.ITALIC, this.getHeight() * 0.07));
    total.setX((this.getWidth() - total.getLayoutBounds().getWidth()) / 2.0);
    total.setY(this.getHeight() * 0.93);
    if (levelsBeaten[BoardController.MAX_LEVEL] > 0) {
      int sum = 0;
      for (int i = 1; i <= BoardController.MAX_LEVEL; i++) {
        sum += levelsBeaten[i];
      }
      total.setText("Total: " + sum);
    }

    this.root.getChildren().addAll(
            new KtfButton(
                    (int) (this.getWidth() * 0.025),
                    (int) (this.getWidth() * 0.025),
                    (int)(buttonWidth * 0.7),
                    (int)(buttonWidth * 0.7),
                    Math.max(1, (int) (borderWidth * 0.7)),
                    "<",
                    mouseEvent -> this.sc.openMenu()
            ),
            total
    );
  }

  private void placeLevelButton(int level, int x, int y, int width, int height, int borderWidth, int[] levs) {
    KtfButton button = new KtfButton(x, y, width, height, borderWidth,
            level + "",
            mouseEvent -> {sc.openLevel(level);}
    );

    if (levs[level - 1] == 0) {
      button.setInactive();
    }

    Text moves = new Text("---");
    moves.setFont(new Font("Georgia", height * 0.175));
    moves.setX(x);
    moves.setY(y + height * 1.2 + borderWidth);

    if (levs[level] > 0) {
      moves.setText("Moves: " + levs[level]);
    }

    root.getChildren().addAll(button, moves);
  }
}
