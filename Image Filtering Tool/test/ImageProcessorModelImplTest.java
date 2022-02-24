import org.junit.Before;
import org.junit.Test;

import model.ColorComponent;
import model.Image;
import model.ImageProcessorModel;
import model.ImageProcessorModelImpl;
import model.Pixel;
import model.RGBPixel;
import model.SimpleImage;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the ImageProcessorModelImpl.
 */
public class ImageProcessorModelImplTest {

  ImageProcessorModel model;
  Image image22;
  Image image32;

  /**
   * Sets up fields for testing.
   */
  @Before
  public void setup() {
    image22 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    image32 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});
    model = new ImageProcessorModelImpl();
    model.addImage("im22" ,new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}}));
    model.addImage("im32" ,new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}}));
  }

  /**
   * Tests that ImageProcessorModelImpl constructor instantiates model that can
   * add images properly.
   */
  @Test
  public void testConstructor() {
    ImageProcessorModel model1 = new ImageProcessorModelImpl();
    model1.addImage("image" ,new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(0,0,0)},
                {new RGBPixel(180,20,0), new RGBPixel(190,90,190)}}));

    assertTrue(sameColorPixelsInImage(model1.returnImage("image"), new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(0,0,0)},
                {new RGBPixel(180,20,0), new RGBPixel(190,90,190)}})));
  }

  @Test
  public void testAddImage() {

    boolean foundImage1 = true;

    try {
      model.returnImage("image1");
    } catch (IllegalArgumentException e) {
      foundImage1 = false;
    }

    assertFalse(foundImage1);

    model.addImage("image1", new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,255)}}));

    foundImage1 = true;

    try {
      model.returnImage("image1");
    } catch (IllegalArgumentException e) {
      foundImage1 = false;
    }

    assertTrue(foundImage1);
  }

  @Test
  public void testReturnImage() {
    Image image22 = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,0), new RGBPixel(90,90,90)}});
    Image image32 = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)}});

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"),image22));
    assertTrue(sameColorPixelsInImage(model.returnImage("im32"),image32));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReturnImageImageNotFound() {
    model.returnImage("nah");
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkCapitalsMatter() {
    model.returnImage("IM22");
  }

  @Test
  public void testFlipImageVertical() {
    Image image22FlipVer = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,0,0), new RGBPixel(255,0,0)},
                {new RGBPixel(90,90,90), new RGBPixel(180,180,0)}});

    Image stillHere = model.returnImage("im22");

    model.flipImageVertical("im22", "im22-vert");

    assertTrue(sameColorPixelsInImage(model.returnImage("im22-vert"), image22FlipVer));
    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), stillHere));

    Image image32FlipVer = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(255,222,124),
                    new RGBPixel(0,0,255),
                    new RGBPixel(0,100,255)},{
                    new RGBPixel(200,175,115),
                    new RGBPixel(0,0,255),
                    new RGBPixel(0,100,255)}});

    model.flipImageVertical("im32", "im32-vert");

    assertTrue(sameColorPixelsInImage(model.returnImage("im32-vert"), image32FlipVer));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFlipImageVerticalImageNotFound() {
    model.flipImageVertical("nope", "nope-ver");
  }

  @Test
  public void testFlipImageHorizontal() {
    Image image22FlipHor = new SimpleImage(
            new Pixel[][]{{new RGBPixel(180,180,0), new RGBPixel(90,90,90)},
                {new RGBPixel(255,0,0), new RGBPixel(255,0,0)},});

    Image stillHere = model.returnImage("im22");

    model.flipImageHorizontal("im22", "im22-hor");

    assertTrue(sameColorPixelsInImage(model.returnImage("im22-hor"), image22FlipHor));
    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), stillHere));

    Image image32FlipHor = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(200,175,115)},{
                    new RGBPixel(0,100,255),
                    new RGBPixel(0,0,255),
                    new RGBPixel(255,222,124)},});

    model.flipImageHorizontal("im32", "im32-hor");
    assertTrue(sameColorPixelsInImage(model.returnImage("im32-hor"), image32FlipHor));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFlipImageHorizontalImageNotFound() {
    model.flipImageHorizontal("nuh-uh", "nuh-uh-hor");
  }

  @Test
  public void testAdjustBrightness() {
    Image image22darker = new SimpleImage(
            new Pixel[][]{{new RGBPixel(205,0,0), new RGBPixel(205,0,0)},
                {new RGBPixel(130,130,0), new RGBPixel(40,40,40)}});

    model.adjustImageBrightness("im22", "im22-dark", -50);

    assertTrue(sameColorPixelsInImage(model.returnImage("im22-dark"), image22darker));

    Image image32brighter = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(40,140,255),
                    new RGBPixel(40,40,255),
                    new RGBPixel(255,255,164)},{
                    new RGBPixel(40,140,255),
                    new RGBPixel(40,40,255),
                    new RGBPixel(240,215,155)}});

    Image stillHere = model.returnImage("im32");

    model.adjustImageBrightness("im32", "im32-bright", 40);

    assertTrue(sameColorPixelsInImage(model.returnImage("im32-bright"), image32brighter));
    assertTrue(sameColorPixelsInImage(model.returnImage("im32"), stillHere));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAdjustImageBrightnessImageNotFound() {
    model.adjustImageBrightness("no", "no-bright", 10);
  }

  @Test
  public void testGrayscaleImage()  {
    Image image22GrayRed = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(255,255,255)},
                {new RGBPixel(180,180,180), new RGBPixel(90,90,90)}});

    Image image22GrayValue = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,255), new RGBPixel(255,255,255)},
                {new RGBPixel(180,180,180), new RGBPixel(90,90,90)}});

    Image image22GrayLuma = new SimpleImage(
            new Pixel[][]{{new RGBPixel(54,54,54), new RGBPixel(54,54,54)},
                {new RGBPixel(167,167,167), new RGBPixel(90,90,90)}});

    Image image32GrayGreen = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(100,100,100),
                    new RGBPixel(0,0,0),
                    new RGBPixel(222,222,222)},{
                    new RGBPixel(100,100,100),
                    new RGBPixel(0,0,0),
                    new RGBPixel(175,175,175)}});

    Image image32GrayBlue = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(255,255,255),
                    new RGBPixel(255,255,255),
                    new RGBPixel(124,124,124)},{
                    new RGBPixel(255,255,255),
                    new RGBPixel(255,255,255),
                    new RGBPixel(115,115,115)}});

    Image image32GrayIntensity = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(118,118,118),
                    new RGBPixel(85,85,85),
                    new RGBPixel(200,200,200)},{
                    new RGBPixel(118,118,118),
                    new RGBPixel(85,85,85),
                    new RGBPixel(163,163,163)}});

    Image im22StillHere = model.returnImage("im22");
    Image im32StillHere = model.returnImage("im32");

    model.grayscaleImage("im22", "im22-gray-red", ColorComponent.Red);
    model.grayscaleImage("im22", "im22-gray-value", ColorComponent.Value);
    model.grayscaleImage("im22", "im22-gray-luma", ColorComponent.Luma);
    model.grayscaleImage("im32", "im32-gray-green", ColorComponent.Green);
    model.grayscaleImage("im32", "im32-gray-blue", ColorComponent.Blue);
    model.grayscaleImage("im32", "im32-gray-intensity", ColorComponent.Intensity);

    assertTrue(sameColorPixelsInImage(model.returnImage("im22-gray-red"), image22GrayRed));
    assertTrue(sameColorPixelsInImage(model.returnImage("im22-gray-value"), image22GrayValue));
    assertTrue(sameColorPixelsInImage(model.returnImage("im22-gray-luma"), image22GrayLuma));
    assertTrue(sameColorPixelsInImage(model.returnImage("im32-gray-green"), image32GrayGreen));
    assertTrue(sameColorPixelsInImage(model.returnImage("im32-gray-blue"), image32GrayBlue));
    assertTrue(sameColorPixelsInImage(
            model.returnImage("im32-gray-intensity"), image32GrayIntensity));
    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22StillHere));
    assertTrue(sameColorPixelsInImage(model.returnImage("im32"), im32StillHere));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGrayscaleImageImageNotFound() {
    model.grayscaleImage("false", "false-gray", ColorComponent.Intensity);
  }

  @Test
  public void sameNameOverwrites() {
    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), image22));

    model.addImage("im22", new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,255), new RGBPixel(30,90,90)}}));

    Image im22new = new SimpleImage(
            new Pixel[][]{{new RGBPixel(255,255,0), new RGBPixel(255,0,0)},
                {new RGBPixel(180,180,255), new RGBPixel(30,90,90)}});

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));
    //im22 was overwritten with a different image

    model.flipImageHorizontal("im22", "im22");
    im22new = im22new.flipHorizontal();

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));

    model.flipImageVertical("im22", "im22");
    im22new = im22new.flipVertical();

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));

    model.grayscaleImage("im22", "im22", ColorComponent.Intensity);
    im22new = im22new.grayscale(ColorComponent.Intensity);

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));

    model.adjustImageBrightness("im22", "im22", -24);
    im22new = im22new.adjustBrightness(-24);

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));

    model.filterImage("im22", "im22", new double[][]{{0.8}});
    im22new = im22new.filter(new double[][]{{0.8}});

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));

    model.transformImageColor("im22", "im22",
            new double[][]{{0.33, 0.33, 0.33},{0.33, 0.33, 0.33},{0.33, 0.33, 0.33}});
    im22new = im22new.transformColor(
            new double[][]{{0.33, 0.33, 0.33},{0.33, 0.33, 0.33},{0.33, 0.33, 0.33}});

    assertTrue(sameColorPixelsInImage(model.returnImage("im22"), im22new));
  }

  @Test
  public void testTransformImageColor() {
    double[][] matrix1 = new double[][]{
            {0.3, 0.3, 0.2},
            {0.5, 1.0, -0.5},
            {0.7, 0.0, 0.0}};

    double[][] matrix2 = new double[][]{
            {0.1, 0.8, 0.1},
            {0.33, 0.33, 0.34},
            {1.0, -1.0, 1.0}};

    Image im22Transform = new SimpleImage(
            new Pixel[][]{{new RGBPixel(77,128,179), new RGBPixel(77,128,179)},
                {new RGBPixel(108,255,126), new RGBPixel(72,90,63)}});
    Image im32Transform = new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(106,120,155),
                    new RGBPixel(26,87,255),
                    new RGBPixel(216,200,157)},{
                    new RGBPixel(106,120,155),
                    new RGBPixel(26,87,255),
                    new RGBPixel(172,163,140)}});

    model.transformImageColor("im22", "im22-t1", matrix1);
    model.transformImageColor("im32", "im32-t2", matrix2);

    assertTrue(sameColorPixelsInImage(model.returnImage("im22-t1"), im22Transform));

    assertTrue(sameColorPixelsInImage(model.returnImage("im32-t2"), im32Transform));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransformImageColorImageNotFound() {
    model.transformImageColor("incorrect", "incorrect-trans",
            new double[][]{{0.2,0.6,0.2},{0.7,0.0,0.1},{0.0,0.5,0.5}});
  }

  @Test
  public void testFilterImage() {
    model.filterImage("im22", "im22f", new double[][]{{-0.5, 2.0, -0.5}});
    model.filterImage("im32", "im32f", new double[][]{{0.125, 0.125, 0.125},
        {0.125, 0, 0.125},
        {0.125, 0.125, 0.125}});

    assertTrue(sameColorPixelsInImage(model.returnImage("im22f"), new SimpleImage(
            new Pixel[][]{{new RGBPixel(0,0,0), new RGBPixel(233,255,0)},
                {new RGBPixel(0,0,0), new RGBPixel(0,0,0)}})));

    assertTrue(sameColorPixelsInImage(model.returnImage("im32f"), new SimpleImage(
            new Pixel[][]{{
                    new RGBPixel(0,13,96),
                    new RGBPixel(57,75,126),
                    new RGBPixel(25,22,78)},{
                    new RGBPixel(0,13,96),
                    new RGBPixel(57,75,126),
                    new RGBPixel(32,28,79)}})));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFilterImageImageNotFound() {
    model.filterImage("incorrect", "incorrect-trans",
            new double[][]{{0.0,-0.25,0.0},{-0.25,2.0,-0.25},{0.0,-0.25,0.0}});
  }

  private boolean sameColorPixelsInImage(Image im1, Image im2) {
    if (im1.getHeight() != im2.getHeight() || im1.getWidth() != im2.getWidth()) {
      return false;
    }

    boolean sameSoFar = true;

    for (int r = 0; r < im1.getHeight() && sameSoFar; r++) {
      for (int c = 0; c < im1.getWidth() && sameSoFar; c++) {
        Pixel p1 = im1.getPixelAt(r,c);
        Pixel p2 = im2.getPixelAt(r,c);
        sameSoFar &= p1.getComponent(ColorComponent.Red) == p2.getComponent(ColorComponent.Red) &&
                p1.getComponent(ColorComponent.Green) == p2.getComponent(ColorComponent.Green) &&
                p1.getComponent(ColorComponent.Blue) == p2.getComponent(ColorComponent.Blue);
      }
    }

    return sameSoFar;
  }
}
