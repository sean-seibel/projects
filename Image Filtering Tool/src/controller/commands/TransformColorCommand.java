package controller.commands;

import model.ImageProcessorModel;

/**
 * Represents a command to apply a color transformation to an image in an image processor model
 * using a matrix which transforms the RGB values of each pixel.
 */
public abstract class TransformColorCommand implements ImageProcessorCommand {

  protected String name;
  protected String newName;
  protected double[][] filter;

  /**
   * Constructs the command.
   * @param name the name of the image to which this transformation will be applied.
   * @param newName the name that the transformed image will be stored under.
   * @param filter the 3x3 matrix specifying the RGB transformations.
   */
  TransformColorCommand(String name, String newName, double[][] filter) {
    this.name = name;
    this.newName = newName;
    this.filter = filter;
  }

  @Override
  public void runCommand(ImageProcessorModel m) throws IllegalArgumentException {
    m.transformImageColor(name, newName, filter);
  }
}
