package controller.commands;

/**
 * Represents a single command to blur an image in an image processor model.
 */
public class BlurCommand extends FilterCommand {

  /**
   * Constructs the filter command with a kernel that blurs.
   * @param name the name of the image to blur.
   * @param newName the name to store the blurred version under.
   */
  public BlurCommand(String name, String newName) {
    super(name, newName, new double[][]{
            {0.0625, 0.125, 0.0625},
            {0.125, 0.25, 0.125},
            {0.0625, 0.125, 0.0625}});
  }
}
