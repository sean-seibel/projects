import controller.SceneCoordinator;
import controller.SceneMaster;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    SceneCoordinator sc = new SceneMaster(stage, 3);
    sc.openMenu();
  }
}
