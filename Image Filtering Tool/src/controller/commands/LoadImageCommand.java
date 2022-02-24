package controller.commands;

import java.io.IOException;

import model.ImageProcessorModel;
import utils.ImageUtils;

/**
 * Represents a command to load an image from a ppm, jpg, bmp, or png file, and add it to a model.
 */
public class LoadImageCommand implements ImageProcessorCommand {

  private final String file;
  private final String name;

  /**
   * Constructs an instance of a command to load an image from a file into a model.
   * @param file the file path of the ppm image.
   * @param name the name by which the image will be referred in the model.
   */
  public LoadImageCommand(String file, String name) {
    this.file = file;
    this.name = name;
  }

  @Override
  public void runCommand(ImageProcessorModel m)
          throws IllegalArgumentException, IOException {

    if (file.endsWith(".ppm")) {
      m.addImage(name, ImageUtils.readPPM(file));
    } else {
      m.addImage(name, ImageUtils.readOther(file));
    }
  }
}
