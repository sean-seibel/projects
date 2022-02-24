package controller.commands;

/**
 * Represents a command to apply a sepia filter to an image in an image processor model.
 */
public class SepiaCommand extends TransformColorCommand {

  /**
   * Constructs the command, specifying an appropriate color transformation matrix to convert into
   * sepia tone.
   * @param name the name of the image to apply the filter to.
   * @param newName the name under which to store the sepia-ed image.
   */
  public SepiaCommand(String name, String newName) {
    super(name, newName, new double[][]{
            {0.393, 0.769, 0.189},
            {0.349, 0.686, 0.168},
            {0.272, 0.534, 0.131}});
  }
}
