package controller.commands;

import model.ColorComponent;
import model.ImageProcessorModel;

/**
 * Represents a single specification of the grayscaleImage method, which can be applied to
 *  a model object.
 */
public class GrayscaleCommand implements ImageProcessorCommand {

  private final ColorComponent comp;
  private final String name;
  private final String newName;

  /**
   * Constructs the command with specified arguments, which can then grayscale an image in the
   *   model as per those arguments.
   * @param comp the component by which to grayscale the image in the model.
   * @param name the name of the image to apply this method to in the model.
   * @param newName the name to save the result as in the model.
   */
  public GrayscaleCommand(ColorComponent comp, String name, String newName) {
    this.comp = comp;
    this.name = name;
    this.newName = newName;
  }

  @Override
  public void runCommand(ImageProcessorModel m)
          throws IllegalArgumentException {
    m.grayscaleImage(name, newName, comp);
  }
}
