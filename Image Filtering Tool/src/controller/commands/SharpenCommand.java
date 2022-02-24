package controller.commands;

/**
 * Represents a command to sharpen an image in an image processor model.
 */
public class SharpenCommand extends FilterCommand {

  /**
   * Constructs the command, specifying a kernel which causes a sharpening effect on the image.
   * @param name the name of the image to sharpen.
   * @param newName the name under which to store the sharpened version of the image.
   */
  public SharpenCommand(String name, String newName) {
    super(name, newName, new double[][]{
            {-0.125, -0.125, -0.125, -0.125, -0.125},
            {-0.125, 0.25, 0.25, 0.25, -0.125},
            {-0.125, 0.25, 1.0, 0.25, -0.125},
            {-0.125, 0.25, 0.25, 0.25, -0.125},
            {-0.125, -0.125, -0.125, -0.125, -0.125}});
  }
}
