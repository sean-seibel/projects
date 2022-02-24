import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import controller.ImageProcessorController;
import controller.ImageProcessorControllerImpl;
import controller.ImageProcessorGUIControllerFeatures;
import model.ImageProcessorModelImpl;
import view.ImageProcessorGraphicsView;
import view.ImageProcessorTextView;

/**
 * A program which allows for processing of JPG, BMP, PNG, and PPM images.
 */
public class ImageProcessor {

  /**
   * Runs the program, either reading and writing input from the console, or using a GUI.
   * @param args no arguments : run GUI.
   *             "-text" : run text-based program.
   *             "-file file-path" : open a script of commands from the file found at file-path, and
   *                                 run them in sequence.
   * @throws FileNotFoundException if the file path can not be found.
   * @throws IllegalArgumentException if the first argument was not "-file" or "-text"
   */
  public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {

    Readable in = null;

    if (args.length > 0) {
      if (args[0].equals("-file")) {
        in = new FileReader(args[1]);
      }
      if (args[0].equals("-text")) {
        in = new InputStreamReader(System.in);
      }

      ImageProcessorController cont = new ImageProcessorControllerImpl(
              new ImageProcessorModelImpl(),
              in,
              new ImageProcessorTextView(System.out)
      );

      cont.activateProcessor();
    } else {
      new ImageProcessorGUIControllerFeatures(new ImageProcessorGraphicsView());
    }
  }
}
