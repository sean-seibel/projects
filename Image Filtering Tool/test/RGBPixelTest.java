import org.junit.Before;
import org.junit.Test;

import model.ColorComponent;
import model.Pixel;
import model.RGBPixel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test class for RGBPixel.
 */
public class RGBPixelTest {

  Pixel black;
  Pixel white;
  Pixel red;
  Pixel green;
  Pixel blue;
  Pixel rgb6;
  Pixel rgb7;
  Pixel rgb8;
  Pixel rgb9;

  /**
   * sets up Pixel objects.
   */
  @Before public void setup() {
    this.black = new RGBPixel(0, 0, 0);
    this.white = new RGBPixel(255, 255, 255);
    this.red = new RGBPixel(255, 0, 0);
    this.green = new RGBPixel(0, 255, 0);
    this.blue = new RGBPixel(0, 0, 255);
    this.rgb6 = new RGBPixel(100, 23, 203);
    this.rgb7 = new RGBPixel(12, 242, 154);
    this.rgb8 = new RGBPixel(195, 176, 103);
    this.rgb9 = new RGBPixel(3, 50, 230);

  }

  /**
   * tests that RGBPixel constructor sets the r,g,b values correctly when initialized.
   */
  @Test
  public void testRGBPixelConstructor() {
    Pixel rgb1 = new RGBPixel(10,24,246);
    assertEquals(10, rgb1.getComponent(ColorComponent.Red));
    assertEquals(24, rgb1.getComponent(ColorComponent.Green));
    assertEquals(246, rgb1.getComponent(ColorComponent.Blue));

  }

  /**
   * tests that RGBPixel constructor throws an exception if the inputted values are not
   * between 0 and 255.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionRedLow() {
    new RGBPixel(-1, 23, 35);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionRedHigh() {
    new RGBPixel(256, 23, 35);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionGreenLow() {
    new RGBPixel(40, -20, 35);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionGreenHigh() {
    new RGBPixel(40, 290, 35);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionBlueLow() {
    new RGBPixel(0, 23, -907);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionBlueHigh() {
    new RGBPixel(40, 23, 355);
  }



  /**
   * tests that getComponent returns correct component of RGBPixel.
   */
  @Test
  public void testGetComponent() {
    assertEquals(0, this.black.getComponent(ColorComponent.Red));
    assertEquals(255, this.red.getComponent(ColorComponent.Red));
    assertEquals(100, this.rgb6.getComponent(ColorComponent.Red));

    assertEquals(255, this.white.getComponent(ColorComponent.Green));
    assertEquals(255, this.green.getComponent(ColorComponent.Green));
    assertEquals(242, this.rgb7.getComponent(ColorComponent.Green));

    assertEquals(0, this.black.getComponent(ColorComponent.Blue));
    assertEquals(255, this.blue.getComponent(ColorComponent.Blue));
    assertEquals(103, this.rgb8.getComponent(ColorComponent.Blue));

    assertEquals(0, this.black.getComponent(ColorComponent.Luma));
    assertEquals(182, this.green.getComponent(ColorComponent.Luma));
    assertEquals(53, this.rgb9.getComponent(ColorComponent.Luma));

    assertEquals(0, this.black.getComponent(ColorComponent.Intensity));
    assertEquals(85, this.blue.getComponent(ColorComponent.Intensity));
    assertEquals(108, this.rgb6.getComponent(ColorComponent.Intensity));

    assertEquals(0, this.black.getComponent(ColorComponent.Value));
    assertEquals(255, this.red.getComponent(ColorComponent.Value));
    assertEquals(242, this.rgb7.getComponent(ColorComponent.Value));
  }

  /**
   * tests that grayscaling RGBPixel by each component works properly.
   */
  @Test
  public void testGrayscale() {
    //tests grayscaling by red value
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0),
            this.black.grayscale(ColorComponent.Red)));
    assertTrue(this.sameRGBPixel(new RGBPixel(100,100,100),
            this.rgb6.grayscale(ColorComponent.Red)));
    assertTrue(this.sameRGBPixel(new RGBPixel(12,12,12),
            this.rgb7.grayscale(ColorComponent.Red)));

    //tests grayscaling by green value
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            this.white.grayscale(ColorComponent.Green)));
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0),
            this.blue.grayscale(ColorComponent.Green)));
    assertTrue(this.sameRGBPixel(new RGBPixel(50,50,50),
            this.rgb9.grayscale(ColorComponent.Green)));

    //tests grayscaling by blue value
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            this.blue.grayscale(ColorComponent.Blue)));
    assertTrue(this.sameRGBPixel(new RGBPixel(203,203,203),
            this.rgb6.grayscale(ColorComponent.Blue)));
    assertTrue(this.sameRGBPixel(new RGBPixel(103,103,103),
            this.rgb8.grayscale(ColorComponent.Blue)));

    //tests grayscaling by luma value
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0),
            this.black.grayscale(ColorComponent.Luma)));
    assertTrue(this.sameRGBPixel(new RGBPixel(182,182,182),
            this.green.grayscale(ColorComponent.Luma)));
    assertTrue(this.sameRGBPixel(new RGBPixel(53,53,53),
            this.rgb9.grayscale(ColorComponent.Luma)));

    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            new RGBPixel(255,255,255).grayscale(ColorComponent.Luma)));
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            new RGBPixel(254,255,255).grayscale(ColorComponent.Luma)));
    assertTrue(this.sameRGBPixel(new RGBPixel(254,254,254),
            new RGBPixel(255,254,255).grayscale(ColorComponent.Luma)));
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            new RGBPixel(255,255,254).grayscale(ColorComponent.Luma)));

    //tests grayscaling by intensity value
    assertTrue(this.sameRGBPixel(new RGBPixel(85,85,85),
            this.red.grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(85,85,85),
            this.blue.grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(108,108,108),
            this.rgb6.grayscale(ColorComponent.Intensity)));

    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            new RGBPixel(255,255,255).grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(254,254,254),
            new RGBPixel(254,255,255).grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(254,254,254),
            new RGBPixel(255,254,255).grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(254,254,254),
            new RGBPixel(255,255,254).grayscale(ColorComponent.Intensity)));
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0),
            new RGBPixel(0,1,1).grayscale(ColorComponent.Intensity)));

    //tests grayscaling by value
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0),
            this.black.grayscale(ColorComponent.Value)));
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255),
            this.red.grayscale(ColorComponent.Value)));
    assertTrue(this.sameRGBPixel(new RGBPixel(242,242,242),
            this.rgb7.grayscale(ColorComponent.Value)));

  }

  /**
   * tests that adjust brightness works properly, sets r/g/b value to 255 or 0 if
   * the delta causes it to be greater than 255 or less than 0.
   */
  @Test
  public void testAdjustBrightness() {

    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0), this.black.adjustBrightness(-10)));
    assertTrue(this.sameRGBPixel(new RGBPixel(45,45,45), this.black.adjustBrightness(45)));
    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255), this.black.adjustBrightness(275)));

    assertTrue(this.sameRGBPixel(new RGBPixel(255,255,255), this.white.adjustBrightness(1)));
    assertTrue(this.sameRGBPixel(new RGBPixel(200,200,200), this.white.adjustBrightness(-55)));
    assertTrue(this.sameRGBPixel(new RGBPixel(0,0,0), this.white.adjustBrightness(-315)));

    assertTrue(this.sameRGBPixel(new RGBPixel(0,45,225), this.rgb9.adjustBrightness(-5)));
    assertTrue(this.sameRGBPixel(new RGBPixel(52,255,194), this.rgb7.adjustBrightness(40)));
    assertTrue(this.sameRGBPixel(new RGBPixel(85,66,0), this.rgb8.adjustBrightness(-110)));

    assertTrue(this.sameRGBPixel(new RGBPixel(0,45,225), this.rgb9.adjustBrightness(-5)));
    assertTrue(this.sameRGBPixel(new RGBPixel(52,255,194), this.rgb7.adjustBrightness(40)));
    assertTrue(this.sameRGBPixel(new RGBPixel(85,66,0), this.rgb8.adjustBrightness(-110)));

    assertTrue(this.sameRGBPixel(new RGBPixel(255,32,32), this.red.adjustBrightness(32)));
    assertTrue(this.sameRGBPixel(new RGBPixel(0,179,0), this.green.adjustBrightness(-76)));
    assertTrue(this.sameRGBPixel(new RGBPixel(49,49,255), this.blue.adjustBrightness(49)));
  }

  @Test
  public void testTransformColor() {
    double[][] matrix1 = new double[][]{
            {0.3, 0.3, 0.2},
            {0.5, 1.0, -0.5},
            {0.7, 0.0, 0.0}};

    double[][] matrix2 = new double[][]{
            {0.1, 0.8, 0.1},
            {0.33, 0.33, 0.34},
            {1.0, -1.0, 1.0}};

    assertTrue(sameRGBPixel(black.transformColor(matrix1), new RGBPixel(0,0,0)));
    assertTrue(sameRGBPixel(white.transformColor(matrix1), new RGBPixel(204,255,179)));
    assertTrue(sameRGBPixel(red.transformColor(matrix1), new RGBPixel(77,128,179)));
    assertTrue(sameRGBPixel(green.transformColor(matrix2), new RGBPixel(204,84,0)));
    assertTrue(sameRGBPixel(blue.transformColor(matrix2), new RGBPixel(26,87,255)));
    assertTrue(sameRGBPixel(rgb6.transformColor(matrix2), new RGBPixel(49,110,255)));
    assertTrue(sameRGBPixel(rgb7.transformColor(matrix1), new RGBPixel(107,171,8)));
    assertTrue(sameRGBPixel(rgb8.transformColor(matrix1), new RGBPixel(132,222,137)));
    assertTrue(sameRGBPixel(rgb9.transformColor(matrix2), new RGBPixel(63,96,183)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException1() {
    red.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException2() {
    red.transformColor(new double[][]{{0.33, 0.33}, {0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException3() {
    red.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33}, {0.33, 0.33, 0.33}});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformColorException4() {
    red.transformColor(new double[][]{{0.33, 0.33, 0.33}, {0.33, 0.33, 0.33}, {0.33, 0.33}});
  }

  //private helper to check if two RGBPixels have same values for red, green and blue
  private boolean sameRGBPixel(Pixel pixel1, Pixel pixel2) {
    return
            pixel1.getComponent(ColorComponent.Red) ==
                    pixel2.getComponent(ColorComponent.Red) &&
            pixel1.getComponent(ColorComponent.Green) ==
                    pixel2.getComponent(ColorComponent.Green) &&
            pixel1.getComponent(ColorComponent.Blue) ==
                    pixel2.getComponent(ColorComponent.Blue);
  }
}