package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import javax.swing.ImageIcon;

import model.ColorComponent;
import model.Image;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;


/**
 * This class contains utility methods to read a PPM image from file and record its contents as
 * an Image. It also contains a method to save an Image object as a PPM file.
 */
public class ImageUtils {

  /**
   * Read an image file in the PPM format return it as an Image.
   *
   * @param filename the path of the file.
   */
  public static Image readPPM(String filename)
          throws FileNotFoundException {
    Scanner sc;

    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException("File " + filename + " not found!");
    }
    StringBuilder builder = new StringBuilder();
    //read the file line by line, and populate a string. This will throw away any comment lines
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s + System.lineSeparator());
      }
    }

    //now set up the scanner to read from the string we just built
    sc = new Scanner(builder.toString());

    String token;

    token = sc.next();
    if (!token.equals("P3")) {
      throw new FileNotFoundException("File found was not raw ppm");
    }
    int width = sc.nextInt();
    int height = sc.nextInt();
    int maxValue = sc.nextInt();

    Pixel[][] pixels = new Pixel[width][height];

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = (int) Math.round(sc.nextInt() * (255.0 / maxValue));
        int g = (int) Math.round(sc.nextInt() * (255.0 / maxValue));
        int b = (int) Math.round(sc.nextInt() * (255.0 / maxValue));
        pixels[j][i] = new RGBPixel(r, g, b);
      }
    }

    return new SimpleImage(pixels);
  }

  /**
   * Read an image file in a java supported image format and returns it as an Image.
   *
   * @param filename the path of the file.
   */
  public static Image readOther(String filename)
          throws IOException {

    File imageFile = new File(filename);

    if (!imageFile.exists()) {
      throw new FileNotFoundException("File " + filename + " not found!");
    }

    BufferedImage img = ImageIO.read(imageFile);

    int width = img.getWidth();
    int height = img.getHeight();

    Pixel[][] pixels = new Pixel[width][height];


    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int pixel = img.getRGB(j, i);
        Color color = new Color(pixel, true);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        pixels[j][i] = new RGBPixel(r, g, b);
      }
    }

    return new SimpleImage(pixels);
  }

  /**
   * Saves the information in an Image object as a PPM file which visually represents the Image.
   *
   * @param image the image to save.
   * @param name  the filepath to save the image to.
   * @throws IOException if there is an error writing to the file.
   */
  public static void savePPM(Image image, String name)
          throws IOException {

    FileWriter ppmWriter = new FileWriter(name);

    ppmWriter.write("P3\n#Created by Image Processor\n");

    ppmWriter.write(image.getWidth() + " " + image.getHeight() + "\n");

    ppmWriter.write("255\n");

    for (int i = 0; i < image.getHeight(); i++) {
      for (int j = 0; j < image.getWidth(); j++) {
        int r = image.getPixelAt(i, j).getComponent(ColorComponent.Red);
        int g = image.getPixelAt(i, j).getComponent(ColorComponent.Green);
        int b = image.getPixelAt(i, j).getComponent(ColorComponent.Blue);
        ppmWriter.write(r + "\n");
        ppmWriter.write(g + "\n");
        ppmWriter.write(b + "\n");
      }
    }

    ppmWriter.close();
  }

  /**
   * Saves the information in an Image object as a specified image type.
   * @param image Image to save.
   * @param name File name to save the image to.
   * @throws IOException If there is an error writing to the image file.
   */
  public static void saveOther(Image image, String name) throws IOException {

    BufferedImage bufferedImage = convertToBufferedImage(image);

    File outputFile = new File(name);

    FileWriter fw = new FileWriter(outputFile);

    String fileType = getFileType(name);

    switch (fileType) {
      case "jpg":
      case "jpeg": //special case to write jpeg w/o compression
        ImageWriter writer = ImageIO.getImageWritersByFormatName(fileType).next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType("JPEG");
        param.setCompressionQuality(1.0f); //retain best quality
        writer.setOutput(new FileImageOutputStream(outputFile));
        writer.write(null, new IIOImage(bufferedImage, null, null), param);
        break;
      default:
        ImageIO.write(bufferedImage, fileType, outputFile);
        break;
    }
  }

  /**
   * Makes an image icon given an image.
   * @param image to convert.
   * @return ImageIcon of given image.
   */
  public static ImageIcon makeImageIcon(Image image) {
    return new ImageIcon(convertToBufferedImage(image));
  }

  /**
   * Creates BufferedImage displaying a histogram of given Image, with specified number of buckets,
   * and width of each column.
   * @param image to use for histogram.
   * @param numBuckets for the histogram.
   * @param colWidthInPixels column width in pixels of the histogram image.
   * @return BufferedImage of the histogram.
   */
  public static BufferedImage makeHistogram(Image image, int numBuckets, int colWidthInPixels) {
    double bucketWidth = 256.0 / (numBuckets + 0.0);
    int[] redBuckets = new int[numBuckets];
    int[] greenBuckets = new int[numBuckets];
    int[] blueBuckets = new int[numBuckets];
    int[] intensityBuckets = new int[numBuckets];

    int mostInABucket = 0;

    for (int i = 0; i < image.getHeight(); i++) {
      for (int j = 0; j < image.getWidth(); j++) {
        int r = image.getPixelAt(i, j).getComponent(ColorComponent.Red);
        int g = image.getPixelAt(i, j).getComponent(ColorComponent.Green);
        int b = image.getPixelAt(i, j).getComponent(ColorComponent.Blue);
        redBuckets[(int) Math.floor(r / bucketWidth)]++;
        blueBuckets[(int) Math.floor(g / bucketWidth)]++;
        greenBuckets[(int) Math.floor(b / bucketWidth)]++;
        intensityBuckets[(int) Math.floor(((r + g + b) / 3.0) / bucketWidth)]++;
      }
    }

    for (int i = 0; i < numBuckets; i++) {
      mostInABucket = Math.max(mostInABucket,
              Math.max(Math.max(redBuckets[i], blueBuckets[i]),
              Math.max(greenBuckets[i], intensityBuckets[i])));
    }

    BufferedImage bufferedImage = new BufferedImage((numBuckets - 1) * colWidthInPixels,
            numBuckets * colWidthInPixels,
            BufferedImage.TYPE_INT_RGB);

    Graphics2D g = bufferedImage.createGraphics();

    int lastRHeight = (int) (redBuckets[0] / (mostInABucket + 0.0) * bufferedImage.getHeight());
    int lastGHeight = (int) (greenBuckets[0] / (mostInABucket + 0.0) * bufferedImage.getHeight());
    int lastBHeight = (int) (blueBuckets[0] / (mostInABucket + 0.0) * bufferedImage.getHeight());
    int lastIHeight =
            (int) (intensityBuckets[0] / (mostInABucket + 0.0) * bufferedImage.getHeight());

    g.setStroke(new BasicStroke((float) (colWidthInPixels / 12.0)));

    for (int i = 1; i < numBuckets; i++) {
      int rHeight = (int) (redBuckets[i] / (mostInABucket + 0.0) * bufferedImage.getHeight());
      int gHeight = (int) (greenBuckets[i] / (mostInABucket + 0.0) * bufferedImage.getHeight());
      int bHeight = (int) (blueBuckets[i] / (mostInABucket + 0.0) * bufferedImage.getHeight());
      int iHeight = (int) (intensityBuckets[i] / (mostInABucket + 0.0) * bufferedImage.getHeight());

      g.setColor(Color.RED);
      g.drawLine((i - 1) * colWidthInPixels, bufferedImage.getHeight() - lastRHeight,
              (i) * colWidthInPixels, bufferedImage.getHeight() - rHeight);


      g.setColor(Color.GREEN);
      g.drawLine(((i - 1)) * colWidthInPixels, bufferedImage.getHeight() - lastGHeight,
              ((i)) * colWidthInPixels, bufferedImage.getHeight() - gHeight);

      g.setColor(Color.BLUE);
      g.drawLine(((i - 1)) * colWidthInPixels, bufferedImage.getHeight() - lastBHeight,
              ((i)) * colWidthInPixels, bufferedImage.getHeight() - bHeight);

      g.setColor(Color.WHITE);
      g.drawLine(((i - 1)) * colWidthInPixels, bufferedImage.getHeight() - lastIHeight,
              ((i)) * colWidthInPixels, bufferedImage.getHeight() - iHeight);

      lastRHeight = rHeight;
      lastGHeight = gHeight;
      lastBHeight = bHeight;
      lastIHeight = iHeight;
    }

    g.dispose();

    return bufferedImage;
  }

  private static BufferedImage convertToBufferedImage(Image image) {
    BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
            image.getHeight(),
            BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < bufferedImage.getHeight(); i++) {
      for (int j = 0; j < bufferedImage.getWidth(); j++) {
        Color c = new Color(image.getPixelAt(i, j).getComponent(ColorComponent.Red),
                image.getPixelAt(i, j).getComponent(ColorComponent.Green),
                image.getPixelAt(i, j).getComponent(ColorComponent.Blue));
        bufferedImage.setRGB(j, i, c.getRGB());
      }
    }

    return bufferedImage;
  }

  private static String getFileType(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
  }

}

