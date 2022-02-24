package controller.save;

import java.io.File;
import java.net.URISyntaxException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class CorruptedSaveAlert {

  /**
   * Returns one of "quit program" or "try again".
   * @return string
   */
  public static String display() {
    ButtonType tryAgain = new ButtonType("Quit Program", ButtonBar.ButtonData.APPLY);
    ButtonType quit = new ButtonType("Try Again", ButtonBar.ButtonData.CANCEL_CLOSE);
    Alert err = new Alert(Alert.AlertType.ERROR, "", tryAgain, quit);
    err.setHeaderText("Unable to read save file");
    err.setTitle("Error");
    try {
      err.setContentText("ktf.save could not be correctly read or updated. Move, rename, " +
              "or delete the existing kft.save in " +
              new File(CorruptedSaveAlert.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .getParentFile().getAbsolutePath() + " . Once this is done, hit \"Try Again\" to attempt " +
              "to create a new file. Or hit \"Quit Program\" to exit, and lose your save data.");
    } catch (URISyntaxException e) {
      err.setContentText("ktf.save could not be correctly read or updated, move, rename, " +
              "or delete the existing kft.save" + ". Once this is done, hit \"Try Again\" to attempt " +
              "to create a new file. Or hit \"Quit Program\" to exit, and lose your save data.");
    }

    ButtonType result = err.showAndWait().get();

    String ret = "quit program";

    if (result.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
      ret = "try again";
    }

    return ret;
  }
}
