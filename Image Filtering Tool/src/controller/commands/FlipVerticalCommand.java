package controller.commands;

import model.ImageProcessorModel;

/**
 * Represents a single specification of the flipImageVertical method, which can be applied to
 * a model object.
 */
public class FlipVerticalCommand implements ImageProcessorCommand {

  private final String name;
  private final String newName;

  /**
   * Constructs the command with the specified arguments to be passed to the model.
   * @param name a name of an image in the model.
   * @param newName the name to save the result of the command as in the model.
   */
  public FlipVerticalCommand(String name, String newName) {
    this.name = name;
    this.newName = newName;
  }

  @Override
  public void runCommand(ImageProcessorModel m)
          throws IllegalArgumentException {
    m.flipImageVertical(name, newName);
  }
}
