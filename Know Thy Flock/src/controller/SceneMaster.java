package controller;

import javafx.scene.Group;
import javafx.stage.Stage;
import view.BoardScene;
import view.LevelSelectScene;
import view.MenuScene;
import view.SettingsScene;

public class SceneMaster implements SceneCoordinator {

  private final Stage stage;
  private int width;
  private int height;
  private int sizePreset;

  public static final int NUM_SIZES = 5;

  public SceneMaster(Stage stage, int sizePreset) {
    this.stage = stage;
    this.changeSize(sizePreset);
    this.stage.setTitle("Know Thy Flock");
    this.stage.setResizable(false);  //stage.setHeight() includes the bar up top
    this.stage.show();
  }

  @Override
  public void openLevel(int level) {
    stage.setScene(new BoardScene(new Group(), level, width, height, this));
  }

  @Override
  public void openMenu() {
    stage.setScene(new MenuScene(new Group(), width, height, this));
  }

  @Override
  public void openLevelSelect() {
    stage.setScene(new LevelSelectScene(new Group(), width, height, this));
  }

  @Override
  public void close() {
    stage.close();
  }

  @Override
  public void changeSize(int preset) {
    switch (preset) {
      case 1:
        this.width = 400;
        this.height = 300;
        break;
      case 2:
        this.width = 550;
        this.height = 400;
        break;
      case 3:
        this.width = 700;
        this.height = 500;
        break;
      case 4:
        this.width = 1000;
        this.height = 700;
        break;
      case 5:
        this.width = 1200;
        this.height = 800;
        break;
    }
    sizePreset = preset;
  }

  @Override
  public int getSizePreset() {
    return sizePreset;
  }

  @Override
  public void openSettings() {
    this.stage.setScene(new SettingsScene(new Group(), width, height, this));
  }
}
