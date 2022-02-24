package controller.commands;

import model.ImageProcessorModel;

/**
 * Represents a single specification of the flipImageHorizontal method, which can be applied to
 * a model object.
 */
public class FlipHorizontalCommand implements ImageProcessorCommand {

  private final String name;
  private final String newName;

  /**
   * Constructs this instance of the command with specified arguments.
   *
   * @param name    the name of the image in the model.
   * @param newName the name to save the image as in the model.
   */
  public FlipHorizontalCommand(String name, String newName) {
    this.name = name;
    this.newName = newName;
  }

  @Override
  public void runCommand(ImageProcessorModel m)
          throws IllegalArgumentException {
    m.flipImageHorizontal(name, newName);
  }
}
