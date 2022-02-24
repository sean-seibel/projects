package controller.commands;

import model.ImageProcessorModel;

/**
 * Represents a single specification of the adjustImageBrightness method, which can be applied to
 * a model object.
 */
public class AdjustBrightnessCommand implements ImageProcessorCommand {

  private final int delta;
  private final String name;
  private final String newName;

  /**
   * Constructs this command instance with the specified arguments.
   *
   * @param delta   the amount by which to modify the brightness.
   * @param name    the name of the image in the processor.
   * @param newName the name to save the image as in the processor
   */
  public AdjustBrightnessCommand(int delta, String name, String newName) {
    this.delta = delta;
    this.name = name;
    this.newName = newName;
  }

  @Override
  public void runCommand(ImageProcessorModel m)
          throws IllegalArgumentException {
    m.adjustImageBrightness(name, newName, delta);
  }
}
