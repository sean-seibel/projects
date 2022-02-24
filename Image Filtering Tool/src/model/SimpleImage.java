package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a rectangular image with at least 1 pixel, which can produce modified versions of
 *   itself, and return its dimensions and color of pixel at a given location.
 */
public class SimpleImage implements Image {

  /**
   * Invariant: pixels will always have dimensions [width][height].
   * Invariant: pixels has at least 1 pixel.
   * Invariant: pixels is not jagged.
   */
  private final Pixel[][] pixels;
  private final int width;
  private final int height;

  /**
   * Constructs the image from an array of pixels.
   * @param pixels the rectangular 2-d array of pixels which will comprise this image.
   * @throws IllegalArgumentException if pixels was null, or non-rectangular,
   *                                  or contained any null pixels.
   */
  public SimpleImage(Pixel[][] pixels) throws IllegalArgumentException {
    if (pixels == null) {
      throw new IllegalArgumentException("Given array of pixels was null.");
    }

    if (pixels.length == 0) {
      throw new IllegalArgumentException("Given array of pixels had a dimension 0.");
    }

    if (pixels[0].length == 0) {
      throw new IllegalArgumentException("Given array of pixels had a dimension 0.");
    }

    for (int i = 0; i < pixels.length; i++) {
      if (pixels[i] == null || pixels[0].length != pixels[i].length) {
        throw new IllegalArgumentException("Pixel array was jagged.");
      }
      for (int j = 0; j < pixels[i].length; j++) {
        if (pixels[i][j] == null) {
          throw new IllegalArgumentException("Null pixel present at " + i + "," + j + ".");
        }
      }
    }

    this.pixels = pixels;
    this.height = pixels[0].length;
    this.width = pixels.length;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public Pixel getPixelAt(int row, int col) throws IllegalArgumentException {
    if (row < 0 || row >= this.height || col < 0 || col >= this.width) {
      throw new IllegalArgumentException("row or column out of range.");
    }
    return getCopyOfPixel(col, row);
  }

  protected Pixel getCopyOfPixel(int col, int row) {
    Pixel pixelToCopy = pixels[col][row]; //immutable -> can just return pixels[col][row]??

    return new RGBPixel(pixelToCopy.getComponent(ColorComponent.Red),
            pixelToCopy.getComponent(ColorComponent.Green),
            pixelToCopy.getComponent(ColorComponent.Blue));
  }

  protected Pixel[][] copyPixels() {
    Pixel[][] newPixels = new Pixel[this.width][this.height];

    for (int w = 0; w < this.width; w++) {
      for (int h = 0; h < this.height; h++) {
        newPixels[w][h] = getCopyOfPixel(w, h);
      }
    }

    return newPixels;
  }

  @Override
  public Image flipVertical() {
    Pixel[][] newPixels = this.copyPixels();
    for (int w = 0; w < this.width; w++) {
      List<Pixel> pixelsOfThisColumn = Arrays.asList(newPixels[w]);
      Collections.reverse(pixelsOfThisColumn);
      newPixels[w] = pixelsOfThisColumn.toArray(new Pixel[0]);
    }
    return new SimpleImage(newPixels);
  }

  @Override
  public Image flipHorizontal() {
    Pixel[][] newPixels = this.copyPixels();
    List<Pixel[]> columnsList = Arrays.asList(newPixels);
    Collections.reverse(columnsList);
    Pixel[][] flippedPixels = columnsList.toArray(new Pixel[0][0]);
    return new SimpleImage(flippedPixels);
  }

  @Override
  public Image adjustBrightness(int delta) {
    Pixel[][] newPixels = new Pixel[this.width][this.height];

    for (int w = 0; w < this.width; w++) {
      for (int h = 0; h < this.height; h++) {
        newPixels[w][h] = getCopyOfPixel(w, h).adjustBrightness(delta);
      }
    }

    return new SimpleImage(newPixels);
  }

  @Override
  public Image grayscale(ColorComponent comp) {
    Pixel[][] newPixels = new Pixel[this.width][this.height];

    for (int w = 0; w < this.width; w++) {
      for (int h = 0; h < this.height; h++) {
        newPixels[w][h] = getCopyOfPixel(w, h).grayscale(comp);
      }
    }

    return new SimpleImage(newPixels);
  }

  @Override
  public Image transformColor(double[][] matrix) throws IllegalArgumentException {
    Pixel[][] newPixels = new Pixel[this.width][this.height];

    for (int w = 0; w < this.width; w++) {
      for (int h = 0; h < this.height; h++) {
        newPixels[w][h] = getCopyOfPixel(w, h).transformColor(matrix);
      }
    }

    return new SimpleImage(newPixels);
  }

  @Override
  public Image filter(double[][] kernel) throws IllegalArgumentException {
    if (kernel == null) {
      throw new IllegalArgumentException("Given kernel was null.");
    }

    if (kernel.length % 2 == 0) {
      throw new IllegalArgumentException("Given kernel had an even dimension.");
    }

    if (kernel[0].length % 2 == 0) {
      throw new IllegalArgumentException("Given kernel had an even dimension.");
    }

    for (int i = 1; i < kernel.length; i++) {
      if (kernel[i] == null || kernel[0].length != kernel[i].length) {
        throw new IllegalArgumentException("Kernel was jagged.");
      }
    }

    Pixel[][] newPixels = new Pixel[this.width][this.height];

    for (int w = 0; w < this.width; w++) {
      for (int h = 0; h < this.height; h++) {
        newPixels[w][h] = this.resultOfKernel(w,h,kernel);
      }
    }

    return new SimpleImage(newPixels);
  }

  @Override
  public Image mosaic(int dotCount) throws IllegalArgumentException {
    if (false) {
      return this.mosaic2(dotCount);
    }
      Pixel[][] pixels = this.copyPixels();
      int xMax = pixels.length;
      int yMax = pixels[0].length;
      int[][] dots = new int[pixels.length][pixels[0].length];
    Pixel[][] newPixels = new Pixel[pixels.length][pixels[0].length];

    if (dotCount < 1) {
      throw new IllegalArgumentException("Seed Count < 1");
    }

      if(dotCount > xMax * yMax) {
        //throw new IllegalArgumentException("Too many seeds!");
        dotCount = xMax * yMax;
      }


      Random r = new Random();

      List<PixelLocation> dotList = new ArrayList<>();

      for (int i = 1; i < dotCount + 1; i++) { //randomly drop some dots
        int x = r.nextInt(xMax);
        int y = r.nextInt(yMax);

        while (dots[x][y] > 0) {
          x++;
          if (x >= xMax) {
            x = 0;
            y++;
            if (y >= yMax) {
              y = 0;
            }
          }
        }

        dots[x][y] = i;
        PixelLocation newPl = new PixelLocation(pixels[x][y], x, y);
        dotList.add(newPl);
      }

      List<List<List<int[]>>> expansionOffsetsByDistanceInOrder = new ArrayList<>();
      for(int i = 0; i < (xMax + yMax) * 2; i++) {
        expansionOffsetsByDistanceInOrder.add(new ArrayList<>());
      }

      List<int[]> frontiers = new ArrayList<>(List.of(new int[]{0,0}));
      int distanceOut = 0;
      for (int i = 0; i < (xMax * yMax) / Math.max(1, dotCount / 5); i++) { //as many distances as there possibly could be (could find tighter upperbound)
        int closestAmongFrontiers = 0;
        double closestDistance = Math.sqrt(
                frontiers.get(0)[0] * frontiers.get(0)[0] +
                        frontiers.get(0)[1] * frontiers.get(0)[1]);
        for (int j = 1; j < frontiers.size(); j++) {
          double distanceJ = Math.sqrt(
                  frontiers.get(j)[0] * frontiers.get(j)[0] +
                          frontiers.get(j)[1] * frontiers.get(j)[1]);
          if (distanceJ < closestDistance) {
            closestAmongFrontiers = j;
            closestDistance = distanceJ;
          }
        }

        int[] closest = frontiers.remove(closestAmongFrontiers);
        int x = closest[0];
        int y = closest[1];

        List<int[]> list = new ArrayList<>();

        if (y == 0) {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{y, x});
          list.add(new int[]{y, -x});

          frontiers.add(new int[]{x + 1, y});
          if (x != 0) {
            frontiers.add(new int[]{x, y + 1});
          }
        } else if (x == y) {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{x, -y});
          list.add(new int[]{-x, -y});
        } else {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{y, x});
          list.add(new int[]{y, -x});
          list.add(new int[]{x, -y});
          list.add(new int[]{-x, -y});
          list.add(new int[]{-y, x});
          list.add(new int[]{-y, -x});

          if (y < Math.min(yMax, xMax)) {
            frontiers.add(new int[]{x, y + 1});
          }
        }

        if (Math.floor(closestDistance) / 1 > distanceOut) { //dictates width of the bands
          distanceOut++;
        }

        expansionOffsetsByDistanceInOrder.get(distanceOut).add(list);
      }

      int fillDistance = 0;

      boolean[] uncompletedDots;

      while (!dotList.isEmpty()) {
        uncompletedDots = new boolean[dotList.size()];

        for (int m = 0; m < expansionOffsetsByDistanceInOrder.get(fillDistance).size(); m++) {
          for (int[] offsetPair : expansionOffsetsByDistanceInOrder.get(fillDistance).get(m)) {
            for (int i = 0; i < dotList.size(); i++) {
              int x = dotList.get(i).getX() + offsetPair[0];
              int y = dotList.get(i).getY() + offsetPair[1];
              if (!(x < 0 || y < 0 || x >= xMax || y >= yMax || newPixels[x][y] != null)) {
                newPixels[x][y] = dotList.get(i).getPixel();
                uncompletedDots[i] = true;
              }
            }
          }
        }

        for (int i = uncompletedDots.length - 1; i >= 0; i--) {
          if (!uncompletedDots[i]) {
            dotList.remove(i);
          }
        }
        fillDistance++;
      }

      for(int x = 0; x < xMax; x++) {
        for(int y = 0; y < yMax; y++) {
          if (newPixels[x][y] == null) {
            newPixels[x][y] = new RGBPixel(255, 0, 0);
          }
        }
      }

      return new SimpleImage(newPixels);
    }

    private class PixelLocation {

      Pixel pixel;
      int x;
      int y;

      PixelLocation(Pixel pixel, int x, int y) {
        this.pixel = pixel;
        this.x = x;
        this.y = y;
      }

      public int getX() {
        return x;
      }

      public int getY() {
        return y;
      }

      public Pixel getPixel() {
        return pixel;
      }
    }

    public Image mosaic2(int dotCount) {
      Pixel[][] pixels = this.copyPixels();
      int xMax = pixels.length;
      int yMax = pixels[0].length;
      boolean[][] dots = new boolean[pixels.length][pixels[0].length];
      Pixel[][] newPixels = new Pixel[pixels.length][pixels[0].length];

      if (dotCount < 1) {
        throw new IllegalArgumentException("Seed Count < 1");
      }

      if(dotCount > xMax * yMax) {
        return new SimpleImage(pixels);
      }

      Random r = new Random();

      List<PixelLocation> dotList = new ArrayList<>();

      for (int i = 0; i < dotCount; i++) { //randomly drop some dots
        int x = r.nextInt(xMax);
        int y = r.nextInt(yMax);

        while (dots[x][y]) {
          x++;
          if (x >= xMax) {
            x = 0;
            y++;
            if (y >= yMax) {
              y = 0;
            }
          }
        }

        dots[x][y] = true;
        PixelLocation newPl = new PixelLocation(pixels[x][y], x, y);
        dotList.add(newPl);
        newPixels[x][y] = newPl.getPixel();
      }

      List<PointDistance> frontiers = new ArrayList<>();
      frontiers.add(new PointDistance(0,1));

      int distanceOut = 1;
      boolean[] uncompletedDots = new boolean[dotList.size()];

      while (!dotList.isEmpty()) {

        PointDistance dist = frontiers.remove(0);
        List<PointDistance> toAdd = new ArrayList<>();
        if (dist.x == 0) {
          toAdd.add(new PointDistance(0, dist.y + 1));
        }
        if (dist.x < dist.y) {
          toAdd.add(new PointDistance(dist.x + 1, dist.y));
        }
        //frontiers.sort((pd1, pd2) -> Double.compare(pd1.dist, pd2.dist));
        for (PointDistance pd : toAdd) { // insert those 1-2 in the sorted list
          int i = 0;
          while (i < frontiers.size()) {
            if (frontiers.get(i).dist >= pd.dist) {
              break;
            }
          }
          frontiers.add(i, pd);
        }

        List<int[]> expandTo = dist.getMirrors();

        for (int[] offset : expandTo) {
          for (int i = 0; i < dotList.size(); i++) {
            int x = dotList.get(i).getX() + offset[0];
            int y = dotList.get(i).getY() + offset[1];
            if (!(x < 0 || y < 0 || x >= xMax || y >= yMax || newPixels[x][y] != null)) {
              newPixels[x][y] = dotList.get(i).getPixel();
              uncompletedDots[i] = true;
            }
          }
        }

        if (Math.floor(dist.dist) / 1 > distanceOut) { //dictates width of the bands
          distanceOut++;
          for (int i = uncompletedDots.length - 1; i >= 0; i--) {
            if (!uncompletedDots[i]) {
              dotList.remove(i);
            }
          }
          uncompletedDots = new boolean[dotList.size()];
        }
      }

      for(int x = 0; x < xMax; x++) {
        for(int y = 0; y < yMax; y++) {
          if (newPixels[x][y] == null) {
            newPixels[x][y] = new RGBPixel(255, 0, 0);
          }
        }
      }

      return new SimpleImage(newPixels);
    }

    private static class PointDistance {
      int x;
      int y;
      double dist;

      PointDistance(int x, int y) {
        this.x = x;
        this.y = y;
        this.dist = Math.sqrt(0.0 + x * x + y * y);
      }

      List<int[]> getMirrors() {
        List<int[]> list = new ArrayList<>();
        if (y == 0) {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{y, x});
          list.add(new int[]{y, -x});
        } else if (x == y) {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{x, -y});
          list.add(new int[]{-x, -y});
        } else {
          list.add(new int[]{x, y});
          list.add(new int[]{-x, y});
          list.add(new int[]{y, x});
          list.add(new int[]{y, -x});
          list.add(new int[]{x, -y});
          list.add(new int[]{-x, -y});
          list.add(new int[]{-y, x});
          list.add(new int[]{-y, -x});
        }
        return list;
      }
    }


  private Pixel resultOfKernel(int w, int h, double[][] kernel) {
    int topMostRow = h - (kernel[0].length / 2);
    int leftMostCol = w - (kernel.length / 2);

    double newR = 0.0;
    double newG = 0.0;
    double newB = 0.0;

    for (int r = 0; r < kernel.length; r++) {
      for (int c = 0; c < kernel[r].length; c++) {
        Pixel pixelHere;
        double kernelFactor = kernel[r][c];
        try {
          pixelHere = getCopyOfPixel(leftMostCol + c, topMostRow + r);
        } catch (ArrayIndexOutOfBoundsException e) {
          pixelHere = new RGBPixel(0,0,0);
        }

        newR += kernelFactor * pixelHere.getComponent(ColorComponent.Red);
        newG += kernelFactor * pixelHere.getComponent(ColorComponent.Green);
        newB += kernelFactor * pixelHere.getComponent(ColorComponent.Blue);
      }
    }

    int r = Math.min(255, Math.max(0, (int) Math.round(newR)));
    int g = Math.min(255, Math.max(0, (int) Math.round(newG)));
    int b = Math.min(255, Math.max(0, (int) Math.round(newB)));

    return new RGBPixel(r, g, b);
  }
}
