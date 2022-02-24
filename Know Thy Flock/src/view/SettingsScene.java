package view;

import controller.save.SaveDataHandler;
import controller.SceneCoordinator;
import controller.SceneMaster;
import javafx.scene.Group;
import javafx.scene.Scene;

public class SettingsScene extends Scene {

  private Group root;
  private SceneCoordinator sc;

  public SettingsScene(Group parent, int width, int height, SceneCoordinator sc) {
    super(parent, width, height);
    this.root = parent;
    this.sc = sc;

    this.root.getChildren().add(
            new KtfButton(
                    (int) (this.getWidth() * 0.025),
                    (int) (this.getWidth() * 0.025),
                    (int)((height / 5.00) * 0.7),
                    (int)((height / 5.00) * 0.7),
                    Math.max(1, height * 0.014),
                    "<",
                    mouseEvent -> this.sc.openMenu()
            )
    );
    int buttonWidth = (int)(this.getWidth() * 0.85);
    int buttonHeight = (int)(this.getHeight() * 0.2);
    int borderWidth = Math.max(1, (int) (buttonHeight * 0.1));

    KtfButton resetButton = new KtfButton(
            (int)(this.getWidth() * 0.075),
            (int)(this.getHeight() * 0.75),
            buttonWidth,
            buttonHeight,
            borderWidth,
            "Erase Save Data",
            mouseEvent -> {});
    resetButton.setOnClick(mouseEvent -> {
      resetButton.setText("Are you sure?");
      resetButton.setOnClick(mouseEvent1 -> {
        SaveDataHandler.resetSaveData();
        resetButton.setInactive();
        resetButton.setText("Erase Save Data");
      });
    });

    KtfButton changeSizeButton = new KtfButton(
            (int)(this.getWidth() * 0.075),
            (int)(this.getHeight() * 0.5),
            buttonWidth,
            buttonHeight,
            borderWidth,
            "Change Size",
            mouseEvent -> {
              int s = this.sc.getSizePreset();
              s++;
              if (s > SceneMaster.NUM_SIZES) { s = 1; }
              this.sc.changeSize(s);
              this.sc.openSettings();
            });



    root.getChildren().addAll(
            changeSizeButton,
            resetButton
    );

    if (SaveDataHandler.getLevelsBeaten()[1] < 1) {
      resetButton.setInactive();
    }
  }
}
