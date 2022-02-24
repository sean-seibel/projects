package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an implementation of an ImageProcessorModel. It stores a mapping of names to images.
 */
public class ImageProcessorModelImpl implements ImageProcessorModel {

  final protected Map<String, Image> directory;

  /**
   * Constructs the model, initializing a map with no stored images.
   */
  public ImageProcessorModelImpl() {
    directory = new HashMap<String, Image>();
  }

  @Override
  public void addImage(String name, Image im) {
    directory.put(name, im);
  }

  @Override
  public Image returnImage(String name) throws IllegalArgumentException {
    nameCheck(name);
    return directory.get(name);
  }

  @Override
  public void grayscaleImage(String name, String newName, ColorComponent comp) {
    nameCheck(name);
    directory.put(newName, directory.get(name).grayscale(comp));
  }

  @Override
  public void flipImageHorizontal(String name, String newName) {
    nameCheck(name);
    directory.put(newName, directory.get(name).flipHorizontal());
  }

  @Override
  public void flipImageVertical(String name, String newName) {
    nameCheck(name);
    directory.put(newName, directory.get(name).flipVertical());
  }

  @Override
  public void adjustImageBrightness(String name, String newName, int delta) {
    nameCheck(name);
    directory.put(newName, directory.get(name).adjustBrightness(delta));
  }

  @Override
  public void transformImageColor(String name, String newName, double[][] matrix)
          throws IllegalArgumentException {
    nameCheck(name);
    directory.put(newName, directory.get(name).transformColor(matrix));
  }

  @Override
  public void filterImage(String name, String newName, double[][] kernel)
          throws IllegalArgumentException {
    nameCheck(name);
    directory.put(newName, directory.get(name).filter(kernel));
  }

  protected void nameCheck(String name) throws IllegalArgumentException {
    if (!directory.containsKey(name)) {
      throw new IllegalArgumentException(name + " not found.");
    }
  }
}
