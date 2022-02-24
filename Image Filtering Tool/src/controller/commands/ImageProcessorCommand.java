package controller.commands;

import java.io.FileNotFoundException;
import java.io.IOException;

import model.ImageProcessorModel;

/**
 * Represents a single command of certain arguments
 * which the Image Processor can apply to its model.
 */
public interface ImageProcessorCommand {

  /**
   * Executes this command.
   *
   * @param m the model on which to run this command.
   * @throws FileNotFoundException    if the command attempts to read from a file that does not
   *                                  exist.
   * @throws IllegalArgumentException if the arguments are illegal for the model method.
   * @throws IOException              if the command fails to write to a destination correctly.
   */
  void runCommand(ImageProcessorModel m)
          throws FileNotFoundException, IllegalArgumentException, IOException;
}
