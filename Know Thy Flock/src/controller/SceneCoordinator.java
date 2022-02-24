package controller;

public interface SceneCoordinator {

  //pull up boardscene
  void openLevel(int level);

  //pull up menu
  void openMenu();

  void openLevelSelect();

  void close();

  void changeSize(int preset);

  void openSettings();

  int getSizePreset();


}
