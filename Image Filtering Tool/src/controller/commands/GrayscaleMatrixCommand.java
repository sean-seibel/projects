package controller.commands;

/**
 * Represents a command to grayscale an image in an image processor model.
 */
public class GrayscaleMatrixCommand extends TransformColorCommand {

  /**
   * Constructs the command, with some specification of the formula by which to grayscale.
   * @param rFactor the amount of the red component to include.
   * @param gFactor the amount of the green component to include.
   * @param bFactor the amount of the blue component to include.
   * @param name the name of the image to grayscale.
   * @param newName the name to store the grayscaled image under.
   */
  public GrayscaleMatrixCommand(double rFactor, double gFactor, double bFactor,
                                String name, String newName) {
    super(name, newName, new double[][]{
            {rFactor, gFactor, bFactor},
            {rFactor, gFactor, bFactor},
            {rFactor, gFactor, bFactor}});
  }
}
