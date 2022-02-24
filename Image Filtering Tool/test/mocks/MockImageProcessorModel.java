package mocks;

import java.io.IOException;

import model.ColorComponent;
import model.Image;
import model.ImageProcessorModel;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;

/**
 * A mock ImageProcessorModel for testing purposes. In place of functional model methods,
 *   this object will record its input in a log.
 */
public class MockImageProcessorModel implements ImageProcessorModel {

  private final Appendable log;

  /**
   * Constructor sets the model's log.
   * @param log the object on which all inputs will be recorded.
   */
  public MockImageProcessorModel(Appendable log) {
    this.log = log;
  }

  @Override
  public void addImage(String name, Image im) {

    try {
      log.append("addImage " + name + " " + im.getWidth() + "x" + im.getHeight() + " image\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public Image returnImage(String name) throws IllegalArgumentException {
    try {
      log.append("returnImage " + name + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
    return new SimpleImage(new Pixel[][]{{new RGBPixel(255, 255, 255)}});
  }

  @Override
  public void grayscaleImage(String name, String newName, ColorComponent comp)
          throws IllegalArgumentException {
    String componentName;

    switch (comp) {
      case Red:
        componentName = "red";
        break;
      case Blue:
        componentName = "blue";
        break;
      case Green:
        componentName = "green";
        break;
      case Intensity:
        componentName = "intensity";
        break;
      case Luma:
        componentName = "luma";
        break;
      case Value:
        componentName = "value";
        break;
      default:
        componentName = "UNIDENTIFIED COMPONENT";
    }

    try {
      log.append("grayscaleImage " + name + " " + newName + " " + componentName + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public void flipImageHorizontal(String name, String newName) throws IllegalArgumentException {
    try {
      log.append("flipImageHorizontal " + name + " " + newName + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public void flipImageVertical(String name, String newName) throws IllegalArgumentException {
    try {
      log.append("flipImageVertical " + name + " " + newName + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public void adjustImageBrightness(String name, String newName, int delta)
          throws IllegalArgumentException {
    try {
      log.append("adjustImageBrightness " + name + " " + newName + " " + delta + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public void transformImageColor(String name, String newName, double[][] matrix)
          throws IllegalArgumentException {
    try {
      log.append("transformImageColor " + name + " " + newName);
      for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix.length; j++) {
          log.append(" " + matrix[i][j]);
        }
        log.append(" /");
      }
      log.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }

  @Override
  public void filterImage(String name, String newName, double[][] kernel)
          throws IllegalArgumentException {
    try {
      log.append("filterImage " + name + " " + newName);
      for (int i = 0; i < kernel.length; i++) {
        for (int j = 0; j < kernel.length; j++) {
          log.append(" " + kernel[i][j]);
        }
        log.append(" /");
      }
      log.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Writing to log failed.");
    }
  }
}

