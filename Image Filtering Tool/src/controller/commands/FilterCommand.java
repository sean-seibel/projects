package controller.commands;

import model.ImageProcessorModel;

/**
 * Represents a command to apply a filter to an image in an image processor model.
 */
public abstract class FilterCommand implements ImageProcessorCommand {

  protected String name;
  protected String newName;
  protected double[][] kernel;

  /**
   * Constructs the command.
   * @param name the name of the image to filter.
   * @param newName the name to store the filtered image as.
   * @param kernel the kernel by which to filter the image.
   */
  FilterCommand(String name, String newName, double[][] kernel) {
    this.name = name;
    this.newName = newName;
    this.kernel = kernel;
  }

  @Override
  public void runCommand(ImageProcessorModel m) throws IllegalArgumentException {
    m.filterImage(name, newName, kernel);
  }

}
