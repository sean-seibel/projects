package view;

import controller.SceneCoordinator;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MenuScene extends Scene {

  private final SceneCoordinator sc;
  private final Group root;

  public MenuScene(Group parent, int width, int height, SceneCoordinator sc) {
    super(parent, width, height);
    this.root = parent;
    this.sc = sc;

    int buttonWidth = (int)(this.getWidth() * 0.75);
    int buttonHeight = (int)(this.getHeight() * 0.125);
    int borderWidth = Math.max(1, (int) (buttonHeight * 0.1));
    int xOffset = (int)(this.getWidth() * 0.125);

//    Text ktfTitle = new Text("Know Thy Flock");
//    ktfTitle.setFont(Font.font("Nanum Pen Script",this.getHeight() * 0.25));
//
//    ktfTitle.setX((this.getWidth() - ktfTitle.getLayoutBounds().getWidth()) / 2.0);
//    ktfTitle.setY(this.getHeight() * 0.25);

    ImageView ktfTitle = new ImageView(new Image("images/title/title1.png",
            this.getWidth(), this.getHeight() * 0.25, false, true));

    HBox ims = new HBox(0,
            new ImageView(new Image("images/sheep/sheep_down_eye_1.png",
                    this.getWidth() * 0.1, this.getWidth() * 0.1, false, false)),
            new ImageView(new Image("images/sheep/sheep_down_eye_1.png",
                    this.getWidth() * 0.1, this.getWidth() * 0.1, false, false)),
            new ImageView(new Image("images/shepherd/shep_down.png",
                    this.getWidth() * 0.1, this.getWidth() * 0.1, false, false)),
            new ImageView(new Image("images/sheep/sheep_down_eye_1.png",
                    this.getWidth() * 0.1, this.getWidth() * 0.1, false, false)),
            new ImageView(new Image("images/sheep/sheep_down_eye_1.png",
                    this.getWidth() * 0.1, this.getWidth() * 0.1, false, false))
    );

    ims.setLayoutX(this.getWidth() * 0.25);
    ims.setLayoutY(this.getHeight() * 0.3);

    root.getChildren().addAll(
            new KtfButton(
                    xOffset,
                    (int)(this.getHeight() * 0.5),
                    buttonWidth,
                    buttonHeight,
                    borderWidth,
                    "Levels",
                    mouseEvent -> {this.sc.openLevelSelect();}),
            new KtfButton(
                    xOffset,
                    (int)(this.getHeight() * 0.66),
                    buttonWidth,
                    buttonHeight,
                    borderWidth,
                    "Settings",
                    mouseEvent -> {this.sc.openSettings();}),
            new KtfButton(
                    xOffset,
                    (int)(this.getHeight() * 0.82),
                    buttonWidth,
                    buttonHeight,
                    borderWidth,
                    "Quit",
                    mouseEvent -> {this.sc.close();}),
            ktfTitle,
            ims
    );
  }
}
