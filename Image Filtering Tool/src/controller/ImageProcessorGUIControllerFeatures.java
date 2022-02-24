package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import model.ColorComponent;
import model.Image;
import utils.ImageUtils;
import view.ImageProcessorGUIView;

/**
 * An implementation of the ImageProcessorController Features that takes in an ImageProcessorGUIView
 * and based on the users interactions with the GUI can load, save, and perform a
 * variety of operations on images.
 */
public class ImageProcessorGUIControllerFeatures implements ImageProcessorControllerFeatures {

  private final Map<String, Function<Scanner, Image>> commands;
  private Image image;
  private final ImageProcessorGUIView view;

  /**
   * Constructs an ImageProcessorGUIControllerFeatures with a specified GUI for interaction with
   * the user.
   * @param view The GUI that the controller uses.
   */
  public ImageProcessorGUIControllerFeatures(ImageProcessorGUIView view) {
    this.view = view;
    this.view.passFeatures(this);
    commands = new HashMap<String, Function<Scanner, Image>>();
    this.setCommands();
  }

  private void setCommands() {
    commands.put("horizontal-flip", s -> this.image.flipHorizontal());
    commands.put("vertical-flip", s -> this.image.flipVertical());
    commands.put("grayscale", s -> this.image.transformColor((
            new double[][]{{0.2126, 0.7152, 0.0722},
                {0.2126, 0.7152, 0.0722},
                {0.2126, 0.7152, 0.0722}})));
    commands.put("sepia", s -> this.image.transformColor((
            new double[][]{{0.393, 0.769, 0.189},
                {0.349, 0.686, 0.168},
                {0.272, 0.534, 0.131}})));
    commands.put("red-component", s -> this.image.grayscale(ColorComponent.Red));
    commands.put("green-component", s -> this.image.grayscale(ColorComponent.Green));
    commands.put("blue-component", s -> this.image.grayscale(ColorComponent.Blue));
    commands.put("value-component", s -> this.image.grayscale(ColorComponent.Value));
    commands.put("intensity-component", s -> this.image.grayscale(ColorComponent.Intensity));
    commands.put("luma-component", s -> this.image.grayscale(ColorComponent.Luma));
    commands.put("sharpen", s -> this.image.filter(
            new double[][]{
                    {-0.125, -0.125, -0.125, -0.125, -0.125},
                    {-0.125, 0.25, 0.25, 0.25, -0.125},
                    {-0.125, 0.25, 1.0, 0.25, -0.125},
                    {-0.125, 0.25, 0.25, 0.25, -0.125},
                    {-0.125, -0.125, -0.125, -0.125, -0.125}}));
    commands.put("blur", s -> this.image.filter(
            new double[][]{
                    {0.0625, 0.125, 0.0625},
                    {0.125, 0.25, 0.125},
                    {0.0625, 0.125, 0.0625}}));
    commands.put("brighten", s -> this.image.adjustBrightness(s.nextInt()));
    commands.put("mosaic", s -> this.image.mosaic(s.nextInt()));
  }

  /**
   * Scans given command string. If it is a valid command and if there is an image loaded into
   * the controller already it runs the given command on the image.
   * @param command string.
   */
  @Override
  public void runCommand(String command) {
    Scanner sc = new Scanner(command);
    String token = sc.next();
    if (this.image != null) {
      this.image = commands.getOrDefault(token, s -> this.image).apply(sc);
      view.setImage(this.image);
    }
  }

  /**
   * Supports loading in images of type .ppm, .bmp, .jpg, .png. If file reading fails, calls GUI to
   * display message to user indicating such.
   * @param filepath of the image.
   */
  @Override
  public void loadImage(String filepath) {
    try {
      if (filepath.endsWith(".ppm")) {
        this.image = ImageUtils.readPPM(filepath);
      } else {
        this.image = ImageUtils.readOther(filepath);
      }
      view.setImage(image);
    } catch (IOException e) {
      view.showMessage("File reading failed.");
    }
  }

  /**
   * Supports saving images of type .ppm, .bmp, .jpg, .png. If file saving fails, calls GUI to
   * display message to user indicating such.
   * @param filepath to save to.
   */
  @Override
  public void saveImage(String filepath) {
    try {
      if (filepath.endsWith(".ppm")) {
        ImageUtils.savePPM(this.image, filepath);
      } else {
        ImageUtils.saveOther(this.image, filepath);
      }
      view.showMessage("Save successful.");
    } catch (IOException e) {
      view.showMessage("File saving failed.");
    }
  }
}
