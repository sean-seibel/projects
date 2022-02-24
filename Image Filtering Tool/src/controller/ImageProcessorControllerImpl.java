package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

import controller.commands.BlurCommand;
import controller.commands.GrayscaleMatrixCommand;
import controller.commands.LoadImageCommand;
import controller.commands.AdjustBrightnessCommand;
import controller.commands.FlipHorizontalCommand;
import controller.commands.FlipVerticalCommand;
import controller.commands.GrayscaleCommand;
import controller.commands.ImageProcessorCommand;
import controller.commands.SaveImageCommand;
import controller.commands.SepiaCommand;
import controller.commands.SharpenCommand;
import model.ColorComponent;
import model.ImageProcessorModel;
import view.ImageProcessorView;

/**
 * An implementation of ImageProcessorController, which reads input from a Readable, and outputs
 * messages to an ImageProcessorView. Has capability to load, save, and perform a
 * variety of operations on images.
 */
public class ImageProcessorControllerImpl implements ImageProcessorController {

  private final ImageProcessorModel model;
  private final Readable in;
  private final ImageProcessorView view;

  private boolean programQuit;

  private final Map<String, Function<Scanner, ImageProcessorCommand>> modelCommands;

  /**
   * Constructs the controller, allowing specification of a model, input source, and view.
   * initializes the list of known commands which may be run on an image in the system.
   * Does not initialize any image to begin with; new images must be loaded in.
   *
   * @param model the system of storing and modifying images that the controller works with.
   * @param in    the source of input, as a sequence of characters.
   * @param view  the view which messages will be rendered to.
   * @throws IllegalArgumentException if any parameters are null.
   */
  public ImageProcessorControllerImpl(
          ImageProcessorModel model, Readable in, ImageProcessorView view)
          throws IllegalArgumentException {
    if (model == null) {
      throw new IllegalArgumentException("Model was null.");
    }
    if (in == null) {
      throw new IllegalArgumentException("Input was null.");
    }
    if (view == null) {
      throw new IllegalArgumentException("View was null.");
    }

    this.model = model;
    this.in = in;
    this.view = view;

    this.programQuit = false;

    this.modelCommands = new HashMap<String, Function<Scanner, ImageProcessorCommand>>();
    this.putCommands();
  }

  private void putCommands() {
    this.modelCommands.put("red-component",
        s -> new GrayscaleCommand(ColorComponent.Red, s.next(), s.next()));
    this.modelCommands.put("green-component",
        s -> new GrayscaleCommand(ColorComponent.Green, s.next(), s.next()));
    this.modelCommands.put("blue-component",
        s -> new GrayscaleCommand(ColorComponent.Blue, s.next(), s.next()));
    this.modelCommands.put("luma-component",
        s -> new GrayscaleCommand(ColorComponent.Luma, s.next(), s.next()));
    this.modelCommands.put("value-component",
        s -> new GrayscaleCommand(ColorComponent.Value, s.next(), s.next()));
    this.modelCommands.put("intensity-component",
        s -> new GrayscaleCommand(ColorComponent.Intensity, s.next(), s.next()));
    this.modelCommands.put("horizontal-flip",
        s -> new FlipHorizontalCommand(s.next(), s.next()));
    this.modelCommands.put("vertical-flip",
        s -> new FlipVerticalCommand(s.next(), s.next()));
    this.modelCommands.put("brighten",
        s -> new AdjustBrightnessCommand(s.nextInt(), s.next(), s.next()));
    this.modelCommands.put("load",
        s -> new LoadImageCommand(s.next(), s.next()));
    this.modelCommands.put("save",
        s -> new SaveImageCommand(s.next(), s.next()));
    this.modelCommands.put("sepia",
        s -> new SepiaCommand(s.next(), s.next()));
    this.modelCommands.put("grayscale",
        s -> new GrayscaleMatrixCommand(
                0.2126, 0.7152, 0.0722, s.next(), s.next()));
    this.modelCommands.put("blur",
        s -> new BlurCommand(s.next(), s.next()));
    this.modelCommands.put("sharpen",
        s -> new SharpenCommand(s.next(), s.next()));
  }

  /**
   * Begins the Processor program. Prompts input for commands. Once a command is correctly
   * specified, then prompts user for the arguments to that command, if there are any. If the
   * arguments are valid, the command will then be executed. If any mistake or error occurs with
   * respect to the inputs given, a message will be rendered with some explanation as to what
   * the error was, and the program will continue. The commands are case-insensitive, but any
   * String arguments are not.
   *
   * @throws IllegalStateException if the input can not be read from or runs out of information,
   *                               or if rendering a message to the view fails.
   */
  @Override
  public void activateProcessor() throws IllegalStateException {
    Scanner sc = new Scanner(in);

    try {
      view.renderMessage("Welcome to Image Processor. \"menu\" for command list.\n");
      while (!programQuit) {
        view.renderMessage("Command:\n");
        String token = sc.next().toLowerCase();

        switch (token) {
          case "q":
          case "quit":
            programQuit = true;
            break;
          case "menu":
            displayTextMenu();
            break;
          default:
            if (modelCommands.getOrDefault(token, null) == null) {
              view.renderMessage("Command \"" + token + "\" not found.\n");
            } else {
              view.renderMessage("Arguments:\n");
              try {
                ImageProcessorCommand currentCommand =
                        modelCommands.get(token).apply(sc);
                currentCommand.runCommand(model);
              } catch (FileNotFoundException fnfE) {
                view.renderMessage("File not found!\n");
              } catch (IllegalArgumentException iaE) {
                view.renderMessage("No such name in the system.\n");
              } catch (InputMismatchException imE) {
                view.renderMessage("Incorrect form of parameters.\n");
                sc.next(); // InputMismatch doesn't remove latest
              }
            }
        }
      }
      view.renderMessage("Thank you for using Image Processor!\n");
    } catch (IOException ioE) {
      throw new IllegalStateException("Input or output failed!");
    } catch (NoSuchElementException nseE) {
      throw new IllegalStateException("Ran out of input");
    }
  }

  private void displayTextMenu() throws IOException {
    view.renderMessage("  Command: q, quit\n" +
            "  Command: menu\n" +
            "  Command: load  |  Arguments: file-name image-name\n" +
            "  Command: save  |  Arguments: image-name file-name\n" +
            "  Command: red-component  |  Arguments: image-name new-image-name\n" +
            "  Command: green-component  |  Arguments: image-name new-image-name\n" +
            "  Command: blue-component  |  Arguments: image-name new-image-name\n" +
            "  Command: luma-component  |  Arguments: image-name new-image-name\n" +
            "  Command: value-component  |  Arguments: image-name new-image-name\n" +
            "  Command: intensity-component  |  Arguments: image-name new-image-name\n" +
            "  Command: horizontal-flip  |  Arguments: image-name new-image-name\n" +
            "  Command: vertical-flip  |  Arguments: image-name new-image-name\n" +
            "  Command: brighten  |  Arguments: brightness-change image-name new-image-name\n" +
            "  Command: grayscale  |  Arguments: image-name new-image-name\n" +
            "  Command: sepia  |  Arguments: image-name new-image-name\n" +
            "  Command: blur  |  Arguments: image-name new-image-name\n" +
            "  Command: sharpen  |  Arguments: image-name new-image-name\n");
  }
}
