package controller.commands;

import java.io.IOException;

import model.ImageProcessorModel;
import utils.ImageUtils;

/**
 * Represents a command to save an image from an
 * ImageProcessorModel as a ppm, jpg, bmp, or png file.
 */
public class SaveImageCommand implements ImageProcessorCommand {

  private final String name;
  private final String fileName;

  /**
   * Constructs the command, which can save an image of some specific name from an
   *   ImageProcessorModel into some file location as a ppm.
   * @param name the name of the image in the model.
   * @param fileName the location to which to save the image.
   */
  public SaveImageCommand(String name, String fileName) {
    this.name = name;
    this.fileName = this.cleanFileName(fileName);
  }

  @Override
  public void runCommand(ImageProcessorModel m) throws IllegalArgumentException, IOException {
    if (this.fileName.endsWith(".ppm")) {
      ImageUtils.savePPM(m.returnImage(this.name), this.fileName);
    } else {
      ImageUtils.saveOther(m.returnImage(this.name), this.fileName);
    }
  }

  private String cleanFileName(String fileName) {
    if (fileName.endsWith("/")) {
      fileName = fileName.substring(0, fileName.length() - 1);
      cleanFileName(fileName);
    }
    return fileName;
  }
}
