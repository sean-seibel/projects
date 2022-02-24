package view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.BoardController;
import controller.save.SaveDataHandler;
import controller.SceneCoordinator;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import model.Board;
import model.terrain.Terrain;
import model.unit.Unit;
import model.util.DefaultMap;
import model.util.Direction;

public class BoardScene extends Scene {

  private final SceneCoordinator sc;

  private final Map<Unit, int[]> unitLocations; //unit ids to most recent locs.
  private final Map<Unit, SpriteMap<Direction>> unitImages; //unit ids to image objects
  private final Map<Unit, SpriteMap<Direction>> unitPaints;

  private final BoardController cont;
  private final Board board;
  private final int level;

  private final Group root;
  private final Group backgroundRoot;
  private final Group foregroundRoot;
  private final Group overlayRoot;

  private final Map<Unit, Cycle<ColorAdjust>> spraypaints;
  private final Map<String, Map<Direction, Image>> nameDirecImage;
  private final Map<Unit, Node> unitNodes;

  private final int spaceWidth;
  private final int spaceHeight;
  private final Duration animDuration = Duration.millis(100);

  private static final Color bgColor = Color.WHITESMOKE;

  public BoardScene(Group parent, int level, int width, int height, SceneCoordinator sc) {
    super(parent, width, height, bgColor);

    this.sc = sc;

    this.root = parent;
    this.foregroundRoot = new Group();
    this.backgroundRoot = new Group();
    this.overlayRoot = new Group();
    this.root.getChildren().add(foregroundRoot);
    this.root.getChildren().add(backgroundRoot);
    this.root.getChildren().add(overlayRoot);
    this.backgroundRoot.toBack();
    this.overlayRoot.toFront();
    this.cont = new BoardController(level, this);
    this.board = cont.getBoard();
    this.level = level;

    this.unitLocations = new HashMap<>();
    this.unitImages = new HashMap<>();
    this.unitPaints = new HashMap<>();
    this.unitNodes = new HashMap<>();
    this.nameDirecImage = new HashMap<>();
    initImageMap();
    this.spraypaints = new HashMap<>();
    initSpraypaint();


    spaceWidth = (int) this.getWidth() / board.getWidth();
    spaceHeight = (int) this.getHeight() / board.getHeight();

    pause();
    resume(); // get ready to receive normal input
              // ^ add some timer stuff to above (timer should be relatively good bc anims use realtime anyway?)
              //   unless javafx Duration is way better than builtin stuff (idk why it would be)

    this.initializeBackground();
    this.initializeForeground();
    this.fadeIn();
  }

  //make these nicer
  private void initSpraypaint() {
    ColorAdjust[] tints = new ColorAdjust[] {
            new ColorAdjust(0.0, 0.0, 0.961, 0.0), //perfecto
            new ColorAdjust(0.85555555556, 1.0, 0.451, 0.0), //good
            new ColorAdjust(0.0, 1.0, 0.6, 0.0), //good
            new ColorAdjust(-1.0, 1.0, 0.7, 0.0), //not different enough
            new ColorAdjust(-0.666667, 1.0, 0.6, 0.0),
            new ColorAdjust(0.277777777, 1.0,0.392,0.0) //ok if you want brown
    };

    for (Unit unit : board.getUnitList()) {
      spraypaints.put(unit, new Cycle<>(tints));
    }
  }

  //remakes all images, and puts them in their correct places
  // deletes active fade-in effect
  private void initializeForeground() {
    //foregroundRoot.getChildren().removeAll(unitImages.values());
    foregroundRoot.getChildren().removeAll(unitNodes.values());
    //List<Node> moveToFront = new ArrayList<>(foregroundRoot.getChildren());

    List<Unit> unitsOnBoard = board.getUnitList();

    for (int i = 0; i < unitsOnBoard.size(); i++) {
      Group unitGroup = new Group();
      Unit unit = unitsOnBoard.get(i);

      if (!unitImages.containsKey(unit)) {
        SpriteMap<Direction> thisImage;
        thisImage = new SpriteMap<>(nameDirecImage.get(unit.getName()).get(Direction.DOWN));
        thisImage.put(Direction.LEFT, nameDirecImage.get(unit.getName()).get(Direction.LEFT));
        thisImage.put(Direction.RIGHT, nameDirecImage.get(unit.getName()).get(Direction.RIGHT));
        thisImage.put(Direction.UP, nameDirecImage.get(unit.getName()).get(Direction.UP));
        unitImages.put(unit, thisImage);
      }

      if (unit.getName().equals("sheep") && !unitPaints.containsKey(unit)) {
        SpriteMap<Direction> paintByDirec = new SpriteMap<>(new Image("images/sheep/paint_down.png",
                this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
        paintByDirec.put(Direction.UP, new Image("images/sheep/paint_up.png",
                this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
        paintByDirec.put(Direction.LEFT, new Image("images/sheep/paint_left.png",
                this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
        paintByDirec.put(Direction.RIGHT, new Image("images/sheep/paint_right.png",
                this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
        paintByDirec.toFront();
        paintByDirec.setByKey(unit.getDirection());
        unitPaints.put(unit, paintByDirec);
      }

      unitImages.get(unit).setByKey(unitsOnBoard.get(i).getDirection());
      unitGroup.relocate(spaceWidth * unitsOnBoard.get(i).getLocation()[0],
              spaceHeight * unitsOnBoard.get(i).getLocation()[1]);
      unitGroup.getChildren().add(unitImages.get(unit));

      SpriteMap<Direction> paintMap = unitPaints.get(unit);
      if (paintMap != null) {
        paintMap.setEffect(spraypaints.get(unit).get());
        unitGroup.getChildren().add(paintMap);
        unitGroup.setOnMouseClicked(mouseEvent -> {
          spraypaints.get(unit).next();
          paintMap.setEffect(spraypaints.get(unit).get());
        });
      }

      unitNodes.put(unit, unitGroup);

      unitLocations.put(unit, unit.getLocation());
      foregroundRoot.getChildren().add(unitGroup);
    }
  }

  private void initImageMap () {
    Map<Direction, Image> sheepMap = new DefaultMap<>(new Image("images/sheep/sheep_down_eye_1.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    sheepMap.put(Direction.LEFT, new Image("images/sheep/sheep_left_eye_1.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    sheepMap.put(Direction.RIGHT, new Image("images/sheep/sheep_right_eye_1.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    sheepMap.put(Direction.UP, new Image("images/sheep/sheep_up_eye_1.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));

    Map<Direction, Image> shepherdMap = new DefaultMap<>(new Image("images/shepherd/shep_up.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    shepherdMap.put(Direction.LEFT, new Image("images/shepherd/shep_left.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    shepherdMap.put(Direction.RIGHT, new Image("images/shepherd/shep_right.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));
    shepherdMap.put(Direction.DOWN, new Image("images/shepherd/shep_down.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));

    Map<Direction, Image> rockMap = new DefaultMap<>(new Image("images/rock/rock_1.png",
            this.getWidth() / board.getWidth(), this.getHeight() / board.getHeight(), false, false));

    nameDirecImage.put("sheep", sheepMap);
    nameDirecImage.put("shepherd", shepherdMap);
    nameDirecImage.put("rock", rockMap);


  }


  private void initializeBackground() {
    Terrain[][] ters = board.getTerrain();
    for (int x = 0; x < ters.length; x++) {
      for (int y = 0; y < ters[x].length; y++) {
        if (ters[x][y] != null && ters[x][y].getName().equals("portal")) {
          ImageView im = new ImageView(new Image("images/portal/portal_1.png", spaceWidth, spaceHeight, false, false));
          im.setX(spaceWidth * x);
          im.setY(spaceHeight * y);
          backgroundRoot.getChildren().add(im);
        }
      }
    }
  }

  //puts whitesmoke rectangle over whole scene which fades out
  private void fadeIn() {
    Shape bigRect = new Rectangle(this.getWidth(), this.getHeight(), bgColor);
    overlayRoot.getChildren().add(bigRect);
    FadeTransition fade = new FadeTransition(Duration.millis(1000), bigRect);
    fade.setFromValue(1.0);
    fade.setToValue(0.0);
    TranslateTransition still = new TranslateTransition(Duration.millis(200), bigRect);
    SequentialTransition seq = new SequentialTransition(still, fade);
    seq.setOnFinished(actionEvent -> {
      overlayRoot.getChildren().remove(bigRect);
    });
    seq.play();
  }

  private void pause() {
    Group pauseOverlay = new Group();
    Group clearOverlay = new Group();
    clearOverlay.getChildren().addAll(
            new Rectangle(this.getWidth(), this.getHeight(), Color.TRANSPARENT), pauseOverlay);
    Node rect = new Rectangle(this.getWidth(), this.getHeight() / 2, Color.WHITE);

    KtfButton resumeButton = new KtfButton(
            rect.getLayoutBounds().getWidth() * 0.075,
            rect.getLayoutBounds().getHeight() * 0.3,
            rect.getLayoutBounds().getWidth() * 0.45,
            rect.getLayoutBounds().getHeight() * 0.4,
            rect.getLayoutBounds().getHeight() * 0.035,
            "Resume",
            mouseEvent -> {this.resume();}
    );

    KtfButton menuButton = new KtfButton(
            rect.getLayoutBounds().getWidth() * 0.575,
            rect.getLayoutBounds().getHeight() * 0.3,
            rect.getLayoutBounds().getWidth() * 0.35,
            rect.getLayoutBounds().getHeight() * 0.4,
            rect.getLayoutBounds().getHeight() * 0.035,
            "Menu",
            mouseEvent -> {this.sc.openMenu();}
    );

    pauseOverlay.getChildren().addAll(rect, resumeButton, menuButton);
    rect.toBack();

    pauseOverlay.setLayoutY(this.getHeight() / 4);
    overlayRoot.getChildren().add(clearOverlay);

    this.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case P:
        case ESCAPE:
          resume();
          break;
        default:
          break;
      }
    });
  }

  private void resume() {
    overlayRoot.getChildren().clear();
    this.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case P:
        case ESCAPE:
          pause();
          break;
        default:
          initializeForeground();
          processResult(cont.handleKey(e));
          break;
      }
    });
  }

  private void endLevel() {
    Group pauseOverlay = new Group();
    Node rect = new Rectangle(this.getWidth(), this.getHeight() / 2, Color.WHITE);

    double y = this.getHeight() * 0.15;

    KtfButton nextLevel = new KtfButton(
            this.getWidth() * 0.25,
            y,
            this.getWidth() * 0.7,
            this.getHeight() * 0.2,
            Math.max(1, this.getHeight() / 50.0),
            "Next Level",
            mouseEvent -> {this.sc.openLevel(level + 1);}
    );

    KtfButton menuButton = new KtfButton(
            (this.getWidth() * 0.05),
            y,
            (this.getHeight() * 0.2),
            (this.getHeight() * 0.2),
            Math.max(1, this.getHeight() / 50.0),
            "≡",
            mouseEvent -> this.sc.openLevelSelect()
    );
    //nextLevel.resizeText(70.0);

    if (this.level >= BoardController.MAX_LEVEL) {
      nextLevel.setInactive();
    }

    pauseOverlay.getChildren().addAll(rect, nextLevel, menuButton);
    rect.toBack();

    pauseOverlay.setLayoutY(this.getHeight() / 4);
    pauseOverlay.setOpacity(0.0);
    overlayRoot.getChildren().add(pauseOverlay);

    this.setOnKeyPressed(keyEvent -> {
      switch (keyEvent.getCode()) {
        case ENTER:
          if (this.level < BoardController.MAX_LEVEL) {
            this.sc.openLevel(level + 1);
          }
          break;
        case ESCAPE:
        case BACK_SPACE:
        case M:
          this.sc.openLevelSelect();
          break;
        default:
          break;
      }
    });
    FadeTransition ft = new FadeTransition(Duration.millis(1000), pauseOverlay);
    ft.setFromValue(0.0);
    ft.setToValue(1.0);

    ft.play();
  }

  public void updateBoard() {
    List<Unit> unitsOnBoard = board.getUnitList();

    ParallelTransition anims = new ParallelTransition();

    for (int i = 0; i < unitsOnBoard.size(); i++) {
      Unit unit = unitsOnBoard.get(i);

      //update image
      unitImages.get(unit).setByKey(unit.getDirection());
      if (unitPaints.containsKey(unit)) {
        unitPaints.get(unit).setByKey(unit.getDirection());
      }
      //

      int xShift = spaceWidth * (unit.getLocation()[0] -
              unitLocations.get(unit)[0]);
      int yShift = spaceHeight * (unit.getLocation()[1] -
              unitLocations.get(unit)[1]);

      TranslateTransition thisAnim = new TranslateTransition(animDuration, unitNodes.get(unit));
      thisAnim.setByX(xShift);
      thisAnim.setByY(yShift);
      anims.getChildren().add(thisAnim);

      unitLocations.put(unit, unit.getLocation());
    }

    anims.play();
  }

  private void processResult(String result) {
    if (result.equals("win")) {
      endLevel();
      SaveDataHandler.updateMoveTotalIfNewRecord(level, board.getMoves());
    }
  }



}
